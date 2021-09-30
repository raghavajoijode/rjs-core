package com.subra.aem.rjs.examples.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.settings.SlingSettingsService;
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
import java.util.stream.Collectors;

@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "= System Info Servlet",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET, "sling.servlet.paths=" + "/bin/rjs/env/info"})
public class SystemInformationServlet extends SlingAllMethodsServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemInformationServlet.class);

    @Reference
    transient SlingSettingsService slingSettingsService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        LOGGER.info("Invoked OauthIntegrationServlet...");
        Map<String, Object> result = new HashMap<>();
        result.put("status", "OK");
        try {
            result.put("sling_id", slingSettingsService.getSlingId());
            result.put("run_modes", slingSettingsService.getRunModes().stream().collect(Collectors.joining(",")));
            result.put("home_path", slingSettingsService.getSlingHomePath());
        } catch (Exception e) {
            LOGGER.error("Exception with OauthIntegrationServlet", e);
        }
        response.getWriter().write(CommonHelper.writeValueAsString(result));
        response.setStatus(200);
    }
}

