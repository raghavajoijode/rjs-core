package com.subra.aem.rjs.examples.servlets;

import com.day.cq.dam.api.Asset;
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
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "=specialistfinder Demo Servlet",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET,
        "sling.servlet.paths=" + "/bin/subra/specialistfinderhaversine"})
public class SpecialistFinderHaversine extends SlingAllMethodsServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(SpecialistFinderHaversine.class);
    private static final String STATUS = "status";
    private static final String DISTANCE_CONSTANT = "distance";
    String distance = null;
    String unit = null;

    private static Double toRad(Double value) {
        return value * Math.PI / 180;
    }

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        try {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            JSONObject finalObject = new JSONObject();
            JSONArray objectsArr = new JSONArray();
            String userLat = request.getParameter("lat");
            String userLng = request.getParameter("lng");
            unit = request.getParameter("unit");
            String radius = request.getParameter("radius");
            String dataSource = request.getParameter("dataSource");
            if (request.getResourceResolver().getResource(dataSource) != null && dataSource.contains(".xlsx")) {
                Asset asset = request.getResourceResolver().getResource(dataSource).adaptTo(Asset.class);
                InputStream is = asset.getOriginal().getStream();
                Workbook workbook = new XSSFWorkbook(is);
                Sheet firstSheet = workbook.getSheetAt(0);
                Iterator<Row> rowIterator = firstSheet.iterator();
                Row firstRow = rowIterator.hasNext() ? rowIterator.next() : null;
                while (rowIterator.hasNext()) {
                    Row nextRow = rowIterator.next();
                    Cell latCell = nextRow.getCell(7);
                    Cell longCell = nextRow.getCell(8);
                    if (firstRow != null && latCell != null && longCell != null
                            && isValid(userLat, userLng, latCell.toString(), longCell.toString(), radius)) {
                        createJSON(firstRow, nextRow, objectsArr, distance, finalObject);
                    }
                }
                is.close();
                workbook.close();
            } else {
                finalObject.put(STATUS, "ASSET_NOT_FOUND_OR_NOT_XLSX");
            }
            finalObject.put("result", getSortedArray(objectsArr));
            finalObject.put("count", objectsArr.length());
            response.getWriter().println(finalObject);
        } catch (IOException e) {
            LOGGER.debug("IN SPECIALIST FINDER SERVLET -- IOException -->", e);
        } catch (JSONException e) {
            LOGGER.debug("IN SPECIALIST FINDER SERVLET -- JSONException -->", e);
        }
    }

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        doGet(request, response);
    }

    private boolean isValid(String userLat, String userLng, String excelLat, String excelLng, String radius) {
        double distanceInMiles = getHaversineDistanc(userLat, userLng, excelLat, excelLng);
        if (Integer.parseInt(radius) >= distanceInMiles) {
            DecimalFormat df = new DecimalFormat("#.#");
            df.setRoundingMode(RoundingMode.HALF_UP);
            distance = df.format(distanceInMiles);
            return true;
        }
        return false;
    }

    private double getHaversineDistanc(String lat1, String lng1, String lat2, String lng2) {
        final int R = "km".equalsIgnoreCase(unit) ? 6371 : 3959;
        Double userLat = Double.parseDouble(lat1);
        Double userLng = Double.parseDouble(lng1);
        Double excelLat = Double.parseDouble(lat2);
        Double excelLng = Double.parseDouble(lng2);
        Double latDistance = toRad(excelLat - userLat);
        Double lonDistance = toRad(excelLng - userLng);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(toRad(userLat))
                * Math.cos(toRad(excelLat)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
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
            obj.put(DISTANCE_CONSTANT, distance);
            jsonArray.put(obj);
            finalObject.put(STATUS, "OK");
        } catch (JSONException e) {
            LOGGER.info("Some Error Occured with Request - Unsuccesfull --> JSONException Create JSOn", e);
        }
    }

    private JSONArray getSortedArray(JSONArray jsonArray) {
        List<JSONObject> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                list.add(jsonArray.getJSONObject(i));
            } catch (JSONException e) {
                LOGGER.error(e.getMessage());
            }
        }

        Collections.sort(list, (JSONObject o1, JSONObject o2) -> {
                    try {
                        Double first = Double.parseDouble(o1.getString(DISTANCE_CONSTANT));
                        Double second = Double.parseDouble(o2.getString(DISTANCE_CONSTANT));
                        return first.compareTo(second);
                    } catch (JSONException e) {
                        LOGGER.error(e.getMessage());
                    }
                    return 0;
                }
        );
        return new JSONArray(list);
    }
    // http://localhost:4702/bin/fertilitylifelines/specialistfinder?lat=42.8140012&lng=-73.98145779999999&unit=mi&radius=500&dataSource=/content/dam/servlets/aaa.xlsx
}