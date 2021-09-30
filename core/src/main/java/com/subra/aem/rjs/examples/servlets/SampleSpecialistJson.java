package com.subra.aem.rjs.examples.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;

@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=SampleSpecialistJson Demo Servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET,
		"sling.servlet.paths=" + "/bin/subra/SampleSpecialistJson" })
public class SampleSpecialistJson extends SlingAllMethodsServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(SampleSpecialistJson.class);
	String distance = null;

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
		LOGGER.debug("BP-1 :- Entered SampleSpecialistJson Servlet, doGet()");
		try {
			response.setContentType("application/json");
			JSONObject finalObject = new JSONObject();
			JSONArray objectsArr = new JSONArray();
			JSONObject obj = new JSONObject();
			JSONObject obj2 = new JSONObject();
			obj.put("Record", 870);
			obj.put("name", "THE CENTER FOR REPRODUCTIVE MEDICINE - MOBILE");
			obj.put("address", "THREE MOBILE INFIRMARY CIRCLE, SUITE 213");
			obj.put("city", "MOBILE");
			obj.put("state", "AL");
			obj.put("zip", "36607");
			obj.put("lat", "30.697286");
			obj.put("lng", "-88.080326");
			obj.put("phone", "251-438-4200");
			obj.put("fax", "251-438-4211");
			obj.put("url", "WWW.INFERTILITYALABAMA.COM");
			obj.put("Distance", "1.1 mi");
			objectsArr.put(obj);
			obj2.put("Record", 879);
			obj2.put("name", "USA CENTER FOR WOMEN'S HEALTH");
			obj2.put("address", "1610 CENTER STREET");
			obj2.put("city", "MOBILE");
			obj2.put("state", "AL");
			obj2.put("zip", "36604");
			obj2.put("lat", "30.696349");
			obj2.put("lng", "-88.075965");
			obj2.put("phone", "251-415-1496");
			obj2.put("fax", "251-415-8601");
			obj2.put("url", "N/A");
			obj2.put("Distance", "1 ft");
			objectsArr.put(obj2);

			finalObject.put("result", objectsArr);
			finalObject.put("count", objectsArr.length());
			LOGGER.info("BP-5 :- Servet Result - Total Elements count is {}", finalObject.getInt("count"));
			response.getWriter().println(finalObject);

		} catch (

		Exception e) {
			LOGGER.info("in exception", e);
		}
	}

	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) {
		doGet(request, response);
	}

}