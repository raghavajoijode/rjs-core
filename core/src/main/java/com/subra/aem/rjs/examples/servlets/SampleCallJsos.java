package com.subra.aem.rjs.examples.servlets;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
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

@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=SampleCallJsos Demo Servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET, "sling.servlet.paths=" + "/bin/subra/SampleCallJsos" })
public class SampleCallJsos extends SlingAllMethodsServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(SampleCallJsos.class);
	String distance = null;

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
		int i = 00;
		LOGGER.info("BP-1 :- Entered SampleCallJsos Servlet, doGet()  {}", i);
		response.setContentType("application/json");
		try {
			LOGGER.info("BP-3 :- Before servlet response::: {}", getDistanceJSON());
			response.getWriter().println(getDistanceJSON());

		} catch (IOException | JSONException e) {
			LOGGER.error(e.getMessage());
		}
	}

	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) {
		doGet(request, response);
	}

	private String getDistanceJSON() throws IOException, JSONException {
		String url = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=17.4281886,78.3231023&destinations=17.3680945,78.5613152&key=AIzaSyAuYoPsqEflRkjKf627CXlrKqQ9iuLMmUs";
		try {
			String returnDistance = "start";
			LOGGER.info("BP-2 :-Req URl::- {}", url);
			CloseableHttpClient client = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet(url);
			request.addHeader("accept", "application/json");
			HttpResponse response = client.execute(request);
			LOGGER.info("Status code :: {}", response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() == 200) {
				String output = EntityUtils.toString(response.getEntity(), "UTF-8");
				JSONObject outputObject = new JSONObject(output);
				if (outputObject.getString("status").equalsIgnoreCase("ok")) {
					JSONArray resultArr = outputObject.getJSONArray("rows").getJSONObject(0).getJSONArray("elements");
					int i = 0;
					for (i = 0; i < resultArr.length(); i++) {
						if (resultArr.getJSONObject(i).getString("status").equalsIgnoreCase("ok")) {
							returnDistance = resultArr.getJSONObject(i).getJSONObject("distance").getString("text");
						}
					}
					return returnDistance;
				}
			} else {
				LOGGER.debug("Some Error Occured with Request - Unsuccesfull --> Request status error {}",
						response.getStatusLine().getStatusCode());
			}
		} catch (ClientProtocolException e) {
			LOGGER.info("Some Error Occured with Request - Unsuccesfull --> ClientProtocolException", e);
			return "ClientProtocolException";
		} catch (IOException e) {
			LOGGER.info("Some Error Occured with Request - Unsuccesfull --> IOException", e);
			return "IOException";
		}
		return "null";
	}

}