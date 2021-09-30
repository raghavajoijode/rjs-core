package com.subra.aem.rjs.oauth.servlets;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.Workflow;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.model.WorkflowModel;
import com.subra.aem.rjs.core.services.GetResourceResolver;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
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

@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "= Integration Profile Test Servlet",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET, "sling.servlet.paths=" + "/bin/rjs/workflow/create"})
public class OauthIntegrationCreateWorkflow extends SlingAllMethodsServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(OauthIntegrationCreateWorkflow.class);

    @Reference
    transient GetResourceResolver getResourceResolver;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        LOGGER.info("Invoked OauthIntegrationCreateWorkflow...");
        final String workflowModel = StringUtils.defaultIfBlank(request.getParameter("model"), StringUtils.EMPTY);
        final String workflowTitle = StringUtils.defaultIfBlank(request.getParameter("workflowTitle"), StringUtils.EMPTY);
        final String payloadType = StringUtils.defaultIfBlank(request.getParameter("payloadType"), "JCR_PATH");
        final String payload = StringUtils.defaultIfBlank(request.getParameter("payload"), StringUtils.EMPTY);
        Map<String, Object> result = new HashMap<>();
        result.put("status", "NOT OK");
        try {
            if (StringUtils.isNoneBlank(workflowModel, payload)) {
                ResourceResolver workflowResourceResolve = getResourceResolver.getWorkflowResourceResolver();
                WorkflowSession workflowSession = workflowResourceResolve.adaptTo(WorkflowSession.class);
                WorkflowModel wfModel = workflowSession.getModel(workflowModel);
                WorkflowData wfData = workflowSession.newWorkflowData(payloadType, payload);
                wfData.getMetaDataMap().put("title", workflowTitle);
                Workflow workflow = workflowSession.startWorkflow(wfModel, wfData);
                result.put("status", "OK");
                result.put("instance", workflow.getId());
                result.put("message", "Workflow started successfully");
            } else {
                result.put("message", "Either payload or modelid is blank");
            }
        } catch (Exception e) {
            LOGGER.error("Exception with OauthIntegrationCreateWorkflow", e);
        }
        response.getWriter().write(CommonHelper.writeValueAsString(result));
        response.setStatus(200);
    }
}

