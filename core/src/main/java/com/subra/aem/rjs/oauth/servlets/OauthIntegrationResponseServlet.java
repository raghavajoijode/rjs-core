package com.subra.aem.rjs.oauth.servlets;

import com.subra.aem.rjs.oauth.dto.OauthResponse;
import com.subra.aem.rjs.oauth.services.OauthIntegrationService;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Configure to exclude authentication at http://localhost:4502/system/console/configMgr/org.apache.sling.engine.impl.auth.SlingAuthenticator
 * Or
 * in your code add Property(name = "sling.auth.requirements", value = "-{pathtoservlet}")
 */
@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "= Integration Test Servlet",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET, "sling.servlet.paths=" + "/bin/rjs/oauth/integration",
        "sling.auth.requirements=" + "-/bin/rjs/oauth/integration"})
public class OauthIntegrationResponseServlet extends SlingAllMethodsServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(OauthIntegrationResponseServlet.class);

    @Reference
    transient OauthIntegrationService integrationService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        LOGGER.info("Invoked OauthIntegrationServlet...");
        Map<String, Object> result = new HashMap<>();
        result.put("status", "OK");
        try {
            OauthResponse oauthResponse = integrationService.getOauthResponse();
            result.put("jwt_token", oauthResponse.getJwtToken());
            result.put("oauth_response", oauthResponse);
        } catch (Exception e) {
            LOGGER.error("Exception with OauthIntegrationServlet", e);
        }
        response.getWriter().write(CommonHelper.writeValueAsString(result));
        response.setStatus(200);
    }
}

