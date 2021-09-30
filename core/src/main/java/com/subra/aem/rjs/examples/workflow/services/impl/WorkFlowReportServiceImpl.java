package com.subra.aem.rjs.examples.workflow.services.impl;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowService;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.Workflow;
import com.subra.aem.rjs.examples.workflow.beans.WorkFlowReportBean;
import com.subra.aem.rjs.examples.workflow.services.WorkFlowReportService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subra.commons.utils.RJSDateTimeUtils;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component(service = WorkFlowReportService.class, immediate = false)
@ServiceDescription("WorkFlowReportExportService Service Configuration")
public class WorkFlowReportServiceImpl implements WorkFlowReportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkFlowReportServiceImpl.class);
    private static final String ASSIGNEE = "assignee";
    private static final String META_DATA_TYPE = "/workItem/metaData";
    @SuppressWarnings("unchecked")
    List<WorkFlowReportBean> reportList = (List<WorkFlowReportBean>) new JSONArray();
    @Reference
    private ResourceResolverFactory resolverFactory;
    @Reference
    private QueryBuilder builder;
    @Reference
    private WorkflowService workFlowService;
    private ResourceResolver resourceResolver;

    @Override
    public JSONArray queryBuilder(Session session, String status, String startDate, String endDate)
            throws JSONException {
        JSONArray reportListArray = new JSONArray();
        String path = "/etc/workflow/instances/";
        String modelId = "/etc/workflow/models/demo/demo-workflow/jcr:content/model";
        try {

            Map<String, Object> wfQmap = new HashMap<>();
            wfQmap.put("path", path);
            wfQmap.put("1_property", "modelId");
            wfQmap.put("1_property.value", modelId);
            wfQmap.put("1_property.operation", "like");
            if (!("ALL".equals(status))) {
                wfQmap.put("2_property", "status");
                wfQmap.put("2_property.value", status);
                wfQmap.put("2_property.operation", "like");
            }

            wfQmap.put("rangeproperty.property", "startTime");
            if (null != startDate) {
                wfQmap.put("rangeproperty.lowerBound", "\"" + startDate + "\"");
                wfQmap.put("rangeproperty.lowerOperation", ">=");
            }

            if (null != endDate) {
                wfQmap.put("rangeproperty.upperBound", "\"" + endDate + "\"");
                wfQmap.put("rangeproperty.upperOperation", "<=");
            }

            wfQmap.put("p.limit", "-1");
            wfQmap.put("orderby", "path");
            Query query = builder.createQuery(PredicateGroup.create(wfQmap), session);
            SearchResult result = query.getResult();

            reportList.clear();

            for (Hit hit : result.getHits()) {
                LOGGER.info("Entered FOR");
                WorkFlowReportBean report = getWorkflowBeanData(hit.getPath(), hit.getIndex(), session);
                if (report != null) {
                    reportListArray.put(getNexusWorkFlowData(report));
                }
            }

        } catch (RepositoryException e) {
            LOGGER.error(e.getMessage());
        }

        return reportListArray;

    }

    @Override
    public List<WorkFlowReportBean> getReportList() {
        return reportList;
    }

    private JSONObject getNexusWorkFlowData(WorkFlowReportBean report) throws JSONException {
        JSONObject wfObj = new JSONObject();

        wfObj.put("serialNo", report.getSerialNo());
        wfObj.put("startDate",
                report.getStartDate() != null ? report.getStartDate().replace('T', ' ') : report.getStartDate());
        wfObj.put("endDate", report.getEndDate() != null ? report.getEndDate().replace('T', ' ') : report.getEndDate());
        wfObj.put("initiator", report.getInitiator());
        wfObj.put("wfTitle", report.getWfTitle());
        wfObj.put("state", report.getStatus());
        wfObj.put("payLoad", report.getPayLoad());
        wfObj.put("unit", report.getUnit());
        wfObj.put("currentState", report.getCurrentState());
        if (!(report.getStatus().equals("COMPLETED"))) {
            wfObj.put("currentAssignee", report.getCurrentAssignee());
        } else {
            wfObj.put("currentAssignee", "----");
        }

        wfObj.put("pdfPath", report.getPdfPath());
        wfObj.put("sitePath", report.getSitePath());
        reportList.add(report);
        return wfObj;
    }

    private WorkFlowReportBean getWorkflowBeanData(String workFlowId, long index, Session session) {
        WorkFlowReportBean report = new WorkFlowReportBean();

        WorkflowSession wfSession = workFlowService.getWorkflowSession(session);

        Workflow workFlow = null;
        try {
            workFlow = wfSession.getWorkflow(workFlowId);
            String payload = workFlow.getWorkflowData().getPayload().toString();
            if (!(payload.contains("/subassets/"))) {
                report.setSerialNo(index + 1);
                report.setStartDate(
                        workFlow.getTimeStarted() != null ? RJSDateTimeUtils.dateToString(workFlow.getTimeStarted())
                                : null);
                report.setEndDate(
                        workFlow.getTimeEnded() != null ? RJSDateTimeUtils.dateToString(workFlow.getTimeEnded())
                                : null);
                report.setInitiator(workFlow.getInitiator());
                report.setWfTitle(workFlow.getWorkflowData().getMetaDataMap().get("workflowTitle", String.class));
                report.setStatus(workFlow.getState());
                report.setPayLoad(payload);
                String unit = workFlow.getWorkflowData().getMetaDataMap().get("KEY_BV_SL_PREPENDER") != null
                        ? workFlow.getWorkflowData().getMetaDataMap().get("KEY_BV_SL_PREPENDER").toString()
                        : null;
                report.setUnit(unit);
                String currentState = workFlow.getMetaDataMap().get("content_wf_state") != null
                        ? workFlow.getWorkflowData().getMetaDataMap().get("content_wf_state").toString()
                        : null;
                report.setCurrentState(currentState);
                Map<String, Object> param = new HashMap<>();
                param.put(ResourceResolverFactory.SUBSERVICE, "ResolveService");
                resourceResolver = resolverFactory.getServiceResourceResolver(param);

                Resource res = resourceResolver.getResource(workFlowId + "/history");

                String currentAssignee = getCurrentAssignee(res);
                report.setCurrentAssignee(currentAssignee);

                String pdfPath = getPDFPath(res, resourceResolver);
                LOGGER.info("+++++++++++++++++++++++++++++++++++++PDF Value::::: {}", pdfPath);
                String sitePath = getSitePath(res, resourceResolver);
                report.setPdfPath(pdfPath);

                report.setSitePath(sitePath);

            } else {
                report = null;

            }

        } catch (WorkflowException | LoginException e) {
            LOGGER.error(e.getMessage());
        }

        return report;
    }

    private String getPDFPath(Resource res, ResourceResolver resourceResolver) {
        String pdfPath = null;
        LOGGER.info("_______________PDF ENTRY________________");
        for (Resource history : res.getChildren()) {
            LOGGER.info("Inside resource loop for pdf");
            if (null != history && null != resourceResolver.getResource(history.getPath() + META_DATA_TYPE)) {
                LOGGER.info("Inside resource IFFF for pdf");
                Resource historyMetadata = resourceResolver.getResource(history.getPath() + META_DATA_TYPE);
                try {
                    pdfPath = historyMetadata.adaptTo(Node.class).hasProperty("pdf-path")
                            ? historyMetadata.adaptTo(Node.class).getProperty("pdf-path").getString()
                            : "In Try";
                } catch (RepositoryException e) {
                    LOGGER.error(e.getMessage());
                }
            }
            LOGGER.info("Inside resource END for PDF {}", pdfPath);
        }

        LOGGER.info("_______________PDF EXIT________________________");
        return pdfPath;

    }

    private String getSitePath(Resource res, ResourceResolver resourceResolver) {
        String sitePath = "";
        LOGGER.info("______________SITE_SSTART____________________");
        for (Resource history : res.getChildren()) {
            LOGGER.info("Inside resource loop for site");
            if (null != history && null != resourceResolver.getResource(history.getPath() + META_DATA_TYPE)
                    && sitePath.equals("")) {
                LOGGER.info("Inside resource IFFF for site PDF");
                Resource historyMetadata = resourceResolver.getResource(history.getPath() + META_DATA_TYPE);

                try {
                    sitePath = historyMetadata.adaptTo(Node.class).hasProperty("site-path")
                            ? historyMetadata.adaptTo(Node.class).getProperty("site-path").getString()
                            : "In Try";
                } catch (RepositoryException e) {
                    LOGGER.error(e.getMessage());
                }
            }
            LOGGER.info("Inside resource END for SITE");
        }
        LOGGER.info("______________SITE_END____________________");
        return sitePath;

    }

    private String getCurrentAssignee(Resource res) {
        String currentAssignee = null;
        LOGGER.info("Inside Current Assifnee Method");
        try {
            for (Resource history : res.getChildren()) {
                LOGGER.info("Inside Current Assifnee Method FOR LOOP");
                if (null != history && null != resourceResolver.getResource(history.getPath() + "/workItem")) {
                    LOGGER.info("InsideCurrent Assifnee Method FOR LOOP IFFF for site");
                    Resource historyMetadata = resourceResolver.getResource(history.getPath() + "/workItem");
                    LOGGER.info("History META DAATA VALUE");
                    LOGGER.info(historyMetadata.getPath());
                    LOGGER.info(currentAssignee, historyMetadata.adaptTo(Node.class).hasProperty(ASSIGNEE));

                    currentAssignee = getCurrentAssignee(currentAssignee, history, historyMetadata);
                }
                LOGGER.info("___________ASSIGNEE END____________--");
            }
        } catch (RepositoryException e) {
            LOGGER.error("Exception occured", e);
        }

        return currentAssignee;
    }

    private String getCurrentAssignee(String currentAssignee, Resource history, Resource historyMetadata)
            throws RepositoryException {
        String currentAssigneeTemp = historyMetadata.adaptTo(Node.class).hasProperty(ASSIGNEE)
                ? historyMetadata.adaptTo(Node.class).getProperty(ASSIGNEE).getString()
                : null;
        LOGGER.info("*******curren assignee****** {}", currentAssignee);
        if (currentAssigneeTemp != null
                && !(currentAssigneeTemp.equals("admin") || currentAssigneeTemp.equals("system"))) {
            currentAssignee = currentAssigneeTemp;
        }

        if (currentAssignee == null || currentAssignee.equals(StringUtils.EMPTY)) {
            currentAssignee = history.adaptTo(Node.class).hasProperty("user")
                    ? history.adaptTo(Node.class).getProperty("user").getString()
                    : null;

        }
        return currentAssignee;
    }
}
