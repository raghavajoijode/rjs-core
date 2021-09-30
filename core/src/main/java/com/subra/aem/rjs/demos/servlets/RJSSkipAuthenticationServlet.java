package com.subra.aem.rjs.demos.servlets;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.Workflow;
import com.subra.aem.rjs.core.services.GetResourceResolver;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;

/**
 * Configure to exclude authentication at http://localhost:4502/system/console/configMgr/org.apache.sling.engine.impl.auth.SlingAuthenticator
 * Or
 * in your code add Property(name = "sling.auth.requirements", value = "-{pathtoservlet}")
 */
@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "= Test Servlet",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET, "sling.servlet.paths=" + "/bin/rjs/wftest",
        "sling.auth.requirements=" + "-/bin/rjs/wftest"})
public class RJSSkipAuthenticationServlet extends SlingAllMethodsServlet {

    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RJSSkipAuthenticationServlet.class);

    @Reference
    private transient GetResourceResolver getResourceResolver;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        LOGGER.info("Invoked RJSSkipAuthenticationServlet...");
        response.getWriter().write("OK");
        String id = request.getParameter("id");
        String type = request.getParameter("type");


        try {
            ResourceResolver workFlowResourceResolver = getResourceResolver.getWorkflowResourceResolver();
            WorkflowSession workflowSession = workFlowResourceResolver.adaptTo(WorkflowSession.class);
            // Based on History
            if (type.equalsIgnoreCase("1")) {
                Workflow workflow = workflowSession.getWorkflow(id);
                List<WorkItem> workItems = workflow.getWorkItems();
                WorkItem workItem = workItems.stream().reduce((first, second) -> second).orElse(null);
                if (workItem != null) {
                    workflowSession.complete(workItem, workflowSession.getRoutes(workItem, false).get(0));
                }
            }
            if (type.equalsIgnoreCase("2")) {
                for (WorkItem wi : workflowSession.getActiveWorkItems()) {
                    Workflow wf = wi.getWorkflow();
                    if (id.equalsIgnoreCase(wf.getId())) {
                        workflowSession.complete(wi, workflowSession.getRoutes(wi, false).get(0));
                    }
                }
            }

        } catch (WorkflowException | LoginException workflowException) {
            workflowException.printStackTrace();
        }
        response.setStatus(200);
    }
}

