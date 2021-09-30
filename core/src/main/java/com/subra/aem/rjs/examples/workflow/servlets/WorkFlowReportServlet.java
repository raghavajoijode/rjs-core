package com.subra.aem.rjs.examples.workflow.servlets;

import com.subra.aem.rjs.examples.workflow.services.WorkFlowReportExportService;
import com.subra.aem.rjs.examples.workflow.services.WorkFlowReportService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "=Simple Demo Servlet",
        "sling.servlet.methods=" + HttpConstants.METHOD_POST, "sling.servlet.paths=" + "/bin/workflowreport"})
public class WorkFlowReportServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkFlowReportServlet.class);
    private static final String GET_DETAILS = "GET";
    private static final String EXPORT_DETAILS = "export";
    @Reference
    transient WorkFlowReportService workFlowReportService;
    @Reference
    transient WorkFlowReportExportService workFlowReportExportService;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);

    }

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        String operation = request.getParameter("operation");
        JSONObject responseObject = new JSONObject();

        ResourceResolver resolver = request.getResourceResolver();
        Session session = resolver.adaptTo(Session.class);

        if (GET_DETAILS.equals(operation)) {
            response.setContentType("text/html");
            response.setCharacterEncoding("UTF-8");

            String startDate = request.getParameter("startdate");
            String endDate = request.getParameter("enddate");
            startDate = startDate.length() > 0 ? startDate + "T00:00:00.000Z" : null;
            endDate = endDate.length() > 0 ? endDate + "T23:59:59.999Z" : null;
            String status = request.getParameter("status");

            JSONArray reportArray = null;
            try {
                reportArray = workFlowReportService.queryBuilder(session, status, startDate, endDate);
            } catch (JSONException e) {
                LOGGER.error(e.getMessage());
            }
            try {
                responseObject.put("reportData", reportArray);
                responseObject.put("status_message", "SUCCESS");
            } catch (JSONException e) {
                LOGGER.error(e.getMessage());
            }

            response.getWriter().println(responseObject.toString());
        } else if (EXPORT_DETAILS.equals(operation)) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            Date today = new Date();
            String todayDateStr = formatter.format(today);
            response.reset();
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; filename=WorkFlowReport_" + todayDateStr + ".csv");
            StringBuilder csvContent = workFlowReportExportService
                    .populateCSVContent(workFlowReportService.getReportList());
            response.getWriter().append(csvContent.toString());

        }
    }

}
