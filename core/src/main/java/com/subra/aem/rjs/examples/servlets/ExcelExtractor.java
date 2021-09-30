package com.subra.aem.rjs.examples.servlets;

import com.day.cq.dam.api.Asset;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subra.commons.exceptions.RJSRuntimeException;
import org.w3c.dom.Document;

import javax.jcr.Node;
import javax.servlet.Servlet;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;

//This is a component so it can provide or consume services
@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=ExcelExtractor Demo Servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET, "sling.servlet.paths=" + "/bin/subra/excelfile" })
public class ExcelExtractor extends org.apache.sling.api.servlets.SlingAllMethodsServlet {
	private static final long serialVersionUID = 2598426539166789515L;

	protected static final Logger LOGGER = LoggerFactory.getLogger(ExcelExtractor.class);
	private static final String FILE_PATH = "/content/dam/merck/Test_Data.xlsx";

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
		LOGGER.debug("In do Get");
		try {
			response.setContentType("application/json");
			response.setCharacterEncoding(StandardCharsets.UTF_8.name());
			final boolean isMultipart = ServletFileUpload.isMultipartContent(request);
			LOGGER.debug("Is it multipart? - {}", isMultipart);
			PageManager pm = request.getResourceResolver().adaptTo(PageManager.class);
			LOGGER.info("GET THE EXCEL FROM REQUEST OBJECT");
			String excelPath = FILE_PATH;
			String pagePath = "";
			if (request.getRequestParameter("excelPath") != null) {
				excelPath = new String(
						request.getRequestParameter("excelPath").getString().getBytes(StandardCharsets.ISO_8859_1),
						StandardCharsets.UTF_8);
			}
			if (request.getRequestParameter("pagePath") != null) {
				pagePath = new String(
						request.getRequestParameter("pagePath").getString().getBytes(StandardCharsets.ISO_8859_1),
						StandardCharsets.UTF_8);
			}
			LOGGER.info("initializing resolver");
			ResourceResolver resourceResolver = request.getResourceResolver();
			LOGGER.info("the resource resolver {}", resourceResolver);
			Resource resource = resourceResolver.getResource(excelPath);
			LOGGER.info("the resource {}", resource);
			Asset asset = resource.adaptTo(Asset.class);
			InputStream stream = asset.getOriginal().getStream();
			LOGGER.info("GET THE STREAM22");
			String excelValue = injectSpreadSheet(stream);
			stream.close();
			if (pm != null) {
				LOGGER.info("Page Manager is not null");
				Page newPage1 = pm.getPage(pagePath + "/");
				if (newPage1 != null) {
					Node newNode1 = newPage1.adaptTo(Node.class);
					Node cont1 = newNode1.getNode("jcr:content");
					if (resourceResolver.getResource(newNode1.getPath()) != null) {
						cont1.setProperty("mapjson", excelValue);
						cont1.getSession().save();
					}
				}
			}
			LOGGER.info("Stream closed returning the json ExcelValue: {}", excelValue);
			response.getWriter().write(excelValue);
		}

		catch (Exception e) {
			LOGGER.info("in exception", e);
		}
	}

	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
		LOGGER.info("IN DO Post");
	}

	// Get data from the excel spreadsheet
	public String injectSpreadSheet(InputStream is) {

		try {
			LOGGER.info("GET THE STREAM33");
			Workbook workbook = new XSSFWorkbook(is);
			Sheet firstSheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = firstSheet.iterator();
			LOGGER.info("GET THE STREAM44");
			LOGGER.info("sheet rows count {}", firstSheet.getTopRow());
			ArrayList<String> dynamicKeys = new ArrayList<>();
			JSONObject jObject = new JSONObject();
			Integer rowCount = 0;
			while (iterator.hasNext()) {
				Row nextRow = iterator.next();
				Iterator<Cell> cellIterator = nextRow.cellIterator();
				LOGGER.info("In Row Iterator");
				Integer colCount = 0;
				JSONArray jArray = new JSONArray();
				while (cellIterator.hasNext()) {
					Cell nextCell = cellIterator.next();
					int columnIndex = nextCell.getColumnIndex();
					int rowIndex = nextCell.getRowIndex();
					LOGGER.info("In Column Iterator {} Row Index {}", columnIndex, rowIndex);
					JSONObject filterJSON = new JSONObject();
					if (rowIndex == 0) {
						dynamicKeys.add(nextCell.toString().replaceAll("\\s", ""));
					} else {
						if (dynamicKeys.size() >= colCount) {
							LOGGER.info("Dynamic keys condition match col index: {}", columnIndex);
							filterJSON.put(dynamicKeys.get(colCount), nextCell.toString());
						}
					}
					jArray.put(filterJSON);
					colCount++;
				}
				if (rowCount != 0)
					jObject.put("row" + (rowCount), jArray);

				rowCount++;
			}
			jObject.put("rowcount", rowCount);
			LOGGER.info("Returned objects");
			workbook.close();
			return jObject.toString();
		}

		catch (Exception e) {
			LOGGER.error("In Exception", e);
		}
		LOGGER.info("Returning null");
		return null;
	}

	public static String[] getLatLongPositions(String address) {
		try {
			if (StringUtils.isNotEmpty(address)) {
				int responseCode = 0;
				// Setting proxy for the connection
				System.setProperty("http.proxyHost", "10.87.21.34");
				System.setProperty("http.proxyPort", "80");
				System.setProperty("https.proxyHost", "10.87.21.34");
				System.setProperty("https.proxyPort", "80");
				String api = "http://maps.googleapis.com/maps/api/geocode/xml?address="
						+ URLEncoder.encode(address, "UTF-8") + "&sensor=true";
				LOGGER.info("URL : {}", api);
				URL url = new URL(api);
				HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
				LOGGER.info("connecting the url httpConnection {}", httpConnection);
				httpConnection.connect();
				LOGGER.info("connected");
				responseCode = httpConnection.getResponseCode();
				LOGGER.info("getting response responseCode {}", responseCode);
				if (responseCode == 200) {
					LOGGER.info("Response :success ");
					DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
					documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
					documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
					documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
					documentBuilderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
					DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
					Document document = builder.parse(httpConnection.getInputStream());
					XPathFactory xPathfactory = XPathFactory.newInstance();
					XPath xpath = xPathfactory.newXPath();
					XPathExpression expr = xpath.compile("/GeocodeResponse/status");
					String status = (String) expr.evaluate(document, XPathConstants.STRING);
					if (status.equals("OK")) {
						LOGGER.info("Status ok ");
						expr = xpath.compile("//geometry/location/lat");
						String latitude = (String) expr.evaluate(document, XPathConstants.STRING);
						expr = xpath.compile("//geometry/location/lng");
						String longitude = (String) expr.evaluate(document, XPathConstants.STRING);
						return new String[] { latitude, longitude };
					} else {
						LOGGER.info("Response Error : {}", status);
						throw new RJSRuntimeException("Error from the API - response status: " + status);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.info("this is a stupid exeption");
		}
		LOGGER.info("returning null");
		return new String[0];
	}

}