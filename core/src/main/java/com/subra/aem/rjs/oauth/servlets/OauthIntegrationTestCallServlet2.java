package com.subra.aem.rjs.oauth.servlets;

import com.subra.aem.rjs.oauth.services.OauthIntegrationService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subra.commons.helpers.CommonHelper;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Configure to exclude authentication at http://localhost:4502/system/console/configMgr/org.apache.sling.engine.impl.auth.SlingAuthenticator
 * Or
 * in your code add Property(name = "sling.auth.requirements", value = "-{pathtoservlet}")
 */
@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "= Integration Profile Test Servlet",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET, "sling.servlet.paths=" + "/bin/rjs/oauth/info"})
public class OauthIntegrationTestCallServlet2 extends SlingAllMethodsServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(OauthIntegrationTestCallServlet2.class);

    private static final String CONTENT_TYPE = "content-type";
    private static final String CONTENT_TYPE_URL_ENCODED = "application/x-www-form-urlencoded";

    @Reference
    transient OauthIntegrationService integrationService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        LOGGER.info("Invoked OauthIntegrationServlet...");
        Map<String, Object> result = new HashMap<>();
        result.put("status", "NOT OK");
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpGet get = new HttpGet("http://localhost:4502/bin/rjs/env/info");
            get.addHeader(CONTENT_TYPE, CONTENT_TYPE_URL_ENCODED);
            if (!Boolean.parseBoolean(request.getParameter("noheader"))) {
                integrationService.updateAuthorizationHeader(get);
            }
            HttpResponse httpResponse = client.execute(get);
            if (httpResponse.getStatusLine().getStatusCode() != 200 && !StringUtils.containsIgnoreCase(httpResponse.getFirstHeader(CONTENT_TYPE).getValue(), "json")) {
                LOGGER.error("response code {} ", httpResponse.getStatusLine().getStatusCode());
            } else {
                result.put("status", "OK");
                result.put("response-date", IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            LOGGER.error("Exception with OauthIntegrationServlet", e);
        }
        response.getWriter().write(CommonHelper.writeValueAsString(result));
        response.setStatus(200);
    }
}

