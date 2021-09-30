package com.subra.aem.rjs.demos.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subra.commons.utils.RJSDateTimeUtils;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 *
 * Configure to exclude authentication at http://localhost:4502/system/console/configMgr/org.apache.sling.engine.impl.auth.SlingAuthenticator
 * Or
 * in your code add Property(name = "sling.auth.requirements", value = "-{pathtoservlet}")
 */
@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "= Test Servlet",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET, "sling.servlet.paths=" + "/bin/rjs/dates",
        "sling.auth.requirements=" + "-/bin/rjs/dates"})
public class RJSDateTestServlet extends SlingAllMethodsServlet {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(RJSDateTestServlet.class);
    public static final String H_3 = "</h3>";

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {

        LOGGER.info("In Do Get Request");
        response.setContentType("text/html");

        String s = "<h3>" + "LDT: " + RJSDateTimeUtils.localDateTimeString() + H_3;
        s = s + "<h3>" + "LDT getZonedDateTime at India: " + RJSDateTimeUtils.localDateTimeStringAtZone(RJSDateTimeUtils.ZoneIdUtil.IST.zone()) + H_3;
        s = s + "<h3>" + "LDT getLocalDateTime at Tokyo: " + RJSDateTimeUtils.localDateTimeAtZone(RJSDateTimeUtils.ZoneIdUtil.JST.zone()) + H_3;
        s = s + "<h3>" + "LDT getZonedDateTime at Tokyo: " + RJSDateTimeUtils.zonedDateTime(RJSDateTimeUtils.ZoneIdUtil.JST.zone()) + H_3;
        s = s + "<h3>" + "LDT getLocalDateTime at UTC: " + RJSDateTimeUtils.localDateTimeAtUTC() + H_3;
        s = s + "<h3>" + "LDT getZonedDateTime at UTC: " + RJSDateTimeUtils.zonedDateTimeAtUTC() + H_3;

        response.getWriter().write(s);
    }

}
