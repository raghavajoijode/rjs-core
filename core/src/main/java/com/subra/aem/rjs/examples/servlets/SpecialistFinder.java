package com.subra.aem.rjs.examples.servlets;



import com.day.cq.dam.api.Asset;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "=specialistfinder Demo Servlet",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET, "sling.servlet.paths=" + "/bin/subra/specialistfinder"})
public class SpecialistFinder extends SlingAllMethodsServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(SpecialistFinder.class);
    private static final String STATUS = "status";
    private static final String DISTANCE = "distance";

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        LOGGER.debug("BP-1 :- Entered SpecialistFinder Servlet, doGet()");
        try {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            JSONObject finalObject = new JSONObject(); // The Output Object
            JSONArray objectsArr = new JSONArray(); // The result array

            String dataSource = request.getParameter("dataSource");
            String unitType = request.getParameter("unitType");
            String radius = request.getParameter("radius");

            double radiusInMeters = Integer.MAX_VALUE;
            if (unitType.equalsIgnoreCase("metric")) {
                radiusInMeters = Integer.parseInt(radius) * 1000.0;
            } else if (unitType.equalsIgnoreCase("imperial")) {
                radiusInMeters = Integer.parseInt(radius) * 1609.3;
            }
            if (request.getResourceResolver().getResource(dataSource) != null && dataSource.contains(".xlsx")) {
                Asset asset = request.getResourceResolver().getResource(dataSource).adaptTo(Asset.class);
                InputStream is = asset.getOriginal().getStream();
                Workbook workbook = new XSSFWorkbook(is);
                Sheet firstSheet = workbook.getSheetAt(0);
                calculateDistance(request, finalObject, objectsArr, radiusInMeters, firstSheet);
                is.close();
                workbook.close();
            } else {
                finalObject.put(STATUS, "ASSET_NOT_FOUND_OR_NOT_XLSX");
            }
            finalObject.put("result", objectsArr);
            finalObject.put("count", objectsArr.length());

            response.getWriter().println(finalObject);

        } catch (Exception e) {
            LOGGER.info("in exception", e);
        }
    }

    private void calculateDistance(SlingHttpServletRequest request, JSONObject finalObject, JSONArray objectsArr,
                                   double radiusInMeters, Sheet firstSheet) throws JSONException {
        Iterator<Row> rowIterator = firstSheet.iterator();
        ArrayList<String> destinationList = new ArrayList<>();
        ArrayList<Integer> rowIdList = new ArrayList<>();
        Row firstRow = rowIterator.hasNext() ? rowIterator.next() : null;
        while (rowIterator.hasNext()) {
            Row nextRow = rowIterator.next();
            Cell latCell = nextRow.getCell(7);
            Cell longCell = nextRow.getCell(8);
            if (latCell != null && longCell != null) {
                String excelDestination = (latCell.toString()).concat(",").concat(longCell.toString());
                destinationList.add(excelDestination);
                rowIdList.add(nextRow.getRowNum());
            }
        }
        int chunk = 100;
        for (int i = 0; i < destinationList.size(); i += chunk) {
            List<String> destListTemp = destinationList.subList(i, Math.min(destinationList.size(), i + chunk));
            List<Integer> rowIdListTemp = rowIdList.subList(i, Math.min(destinationList.size(), i + chunk));
            String userOrigin = request.getParameter("origin");
            String googleApiKey = request.getParameter("key");
            String units = request.getParameter("unitType");
            String destinations = null;
            for (String latlng : destListTemp) {
                destinations = destinations != null ? destinations.concat("%7C").concat(latlng) : latlng;
            }
            String url = "https://maps.googleapis.com/maps/api/distancematrix/json?units=" + units + "&origins="
                    + userOrigin + "&destinations=" + destinations + "&key=" + googleApiKey;
            requestApi(firstRow, radiusInMeters, rowIdListTemp, firstSheet, objectsArr, finalObject, url);

        }
    }

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        doGet(request, response);
    }

    private void requestApi(Row firstRow, double userRadius, List<Integer> rowId, Sheet sheet, JSONArray jsonArray,
                            JSONObject finalObject, String url) throws JSONException {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpGet request = new HttpGet(url);
            request.addHeader("accept", "application/json");
            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                String output = EntityUtils.toString(response.getEntity(), "UTF-8");
                JSONObject outputObject = new JSONObject(output);
                if (outputObject.getString(STATUS).equalsIgnoreCase("ok")) {
                    extractResponse(firstRow, userRadius, rowId, sheet, jsonArray, finalObject, outputObject);
                }
                finalObject.put(STATUS, outputObject.getString(STATUS));
            } else {
                LOGGER.debug("Some Error Occured with Request - Unsuccesfull --> Request status error {}", response.getStatusLine().getStatusCode());
                finalObject.put(STATUS, response.getStatusLine().getStatusCode());
            }

        } catch (ClientProtocolException e) {
            LOGGER.info("Some Error Occured with Request - Unsuccesfull --> ClientProtocolException", e);
            finalObject.put(STATUS, "ClientProtocolException");
        } catch (IOException e) {
            LOGGER.info("Some Error Occured with Request - Unsuccesfull --> IOException", e);
            finalObject.put(STATUS, "IOException");
        } catch (JSONException e) {
            LOGGER.info("Some Error Occured with Request - Unsuccesfull --> JSONException", e);
            finalObject.put(STATUS, "JSONException");
        }
    }

    private void extractResponse(Row firstRow, double userRadius, List<Integer> rowId, Sheet sheet, JSONArray jsonArray,
                                 JSONObject finalObject, JSONObject outputObject) throws JSONException {
        JSONArray resultArr = outputObject.getJSONArray("rows").getJSONObject(0).getJSONArray("elements");
        int i = 0;
        for (i = 0; i < resultArr.length(); i++) {
            if (resultArr.getJSONObject(i).getString(STATUS).equalsIgnoreCase("ok")) {
                int radValue = resultArr.getJSONObject(i).getJSONObject(DISTANCE).getInt("value");
                if (userRadius >= radValue) {
                    createJSON(firstRow, sheet.getRow(rowId.get(i)), jsonArray,
                            resultArr.getJSONObject(i).getJSONObject(DISTANCE).getString("text"), finalObject);
                }
            }

        }
    }

    private void createJSON(Row firstRow, Row row, JSONArray jsonArray, String distance, JSONObject finalObject) {
        JSONObject obj = new JSONObject();
        try {
            finalObject.put(STATUS, "BEFORE_CREATEJSON");
            obj.put("RecordNo", row.getRowNum());
            obj.put(firstRow.getCell(0).toString().replaceAll("\\s", "_"), row.getCell(0));
            obj.put(firstRow.getCell(1).toString().replaceAll("\\s", "_"), row.getCell(1));
            obj.put(firstRow.getCell(2).toString().replaceAll("\\s", "_"), row.getCell(2));
            obj.put(firstRow.getCell(3).toString().replaceAll("\\s", "_"), row.getCell(3));
            obj.put(firstRow.getCell(4).toString().replaceAll("\\s", "_"), row.getCell(4));
            obj.put(firstRow.getCell(5).toString().replaceAll("\\s", "_"),
                    row.getCell(5) != null ? row.getCell(5).toString().replace(".0", "") : null);
            obj.put(firstRow.getCell(6).toString().replaceAll("\\s", "_"), row.getCell(6));
            obj.put(firstRow.getCell(7).toString().replaceAll("\\s", "_"), row.getCell(7));
            obj.put(firstRow.getCell(8).toString().replaceAll("\\s", "_"), row.getCell(8));
            obj.put(firstRow.getCell(9).toString().replaceAll("\\s", "_"), row.getCell(9));
            obj.put(firstRow.getCell(10).toString().replaceAll("\\s", "_"), row.getCell(10));
            obj.put(firstRow.getCell(11).toString().replaceAll("\\s", "_"), row.getCell(11));
            obj.put(firstRow.getCell(12).toString().replaceAll("\\s", "_"), row.getCell(12));
            obj.put(firstRow.getCell(13).toString().replaceAll("\\s", "_"), row.getCell(13));
            obj.put(firstRow.getCell(14).toString().replaceAll("\\s", "_"), row.getCell(14));
            obj.put(firstRow.getCell(15).toString().replaceAll("\\s", "_"), row.getCell(15));
            obj.put(DISTANCE, distance);

            jsonArray.put(obj);
            finalObject.put(STATUS, "OK");
        } catch (JSONException e) {
            LOGGER.info("Some Error Occured with Request - Unsuccesfull --> JSONException Create JSOn", e);
        }
    }
    // http://localhost:4502/bin/fertilitylifelines/specialistfinder?origin=30.696349,-88.075965&unitType=imperial&radius=50&key=AIzaSyAuYoPsqEflRkjKf627CXlrKqQ9iuLMmUs&dataSource=/content/dam/servlets/aaa.xlsx
    // http://localhost:4502/bin/fertilitylifelines/specialistfinder?origin=37.480916,-122.182546&unitType=imperial&radius=50&key=AIzaSyDtJeTTgjEkoKN6iSxd7tKyNuTHWrUXKE8
    // http://localhost:4502/bin/fertilitylifelines/specialistfinder?origin=30.696349,-88.075965&unitType=imperial&radius=50&key=AIzaSyAuYoPsqEflRkjKf627CXlrKqQ9iuLMmUs
}