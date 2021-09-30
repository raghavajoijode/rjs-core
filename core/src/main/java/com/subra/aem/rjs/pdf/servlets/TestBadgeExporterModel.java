package com.subra.aem.rjs.pdf.servlets;

import com.subra.aem.rjs.pdf.BadgeExporterModel;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.subra.commons.helpers.CommonHelper;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;


@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "=ContactUsFormServlet Demo Servlet",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET, "sling.servlet.paths=" + "/bin/test/badges"})
@ServiceDescription("Simple Demo Servlet")

public class TestBadgeExporterModel extends SlingAllMethodsServlet {
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        BadgeExporterModel model = request.getResource().adaptTo(BadgeExporterModel.class);
        response.getWriter().write(CommonHelper.writeValueAsString(model.getBadges()));
    }
}
