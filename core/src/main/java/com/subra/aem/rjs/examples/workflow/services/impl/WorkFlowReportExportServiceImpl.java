package com.subra.aem.rjs.examples.workflow.services.impl;

import com.subra.aem.rjs.examples.workflow.beans.WorkFlowReportBean;
import com.subra.aem.rjs.examples.workflow.services.WorkFlowReportExportService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Component(service = WorkFlowReportExportService.class, immediate = false)
@ServiceDescription("WorkFlowReportExportService Service Configuration")
public class WorkFlowReportExportServiceImpl implements WorkFlowReportExportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkFlowReportExportServiceImpl.class);

    @Override
    public StringBuilder populateCSVContent(List<WorkFlowReportBean> reportList) {
        LOGGER.debug("Debugging WorkFlowReportExportServiceImpl");
        StringBuilder csvContent = new StringBuilder();
        csvContent.append("S No.");
        csvContent.append(',');
        csvContent.append("Started On");
        csvContent.append(',');
        csvContent.append("Ended On");
        csvContent.append(',');
        csvContent.append("Initiator");
        csvContent.append(',');
        csvContent.append("Unit");
        csvContent.append(',');
        csvContent.append("Current State");
        csvContent.append(',');
        csvContent.append("Current Assignee");
        csvContent.append(',');
        csvContent.append("WF Title");
        csvContent.append(',');
        csvContent.append("Status");
        csvContent.append(',');
        csvContent.append("Payload Path");
        csvContent.append(',');
        csvContent.append("Associated PDF");
        csvContent.append(',');
        csvContent.append("Associated Site");

        csvContent.append('\n');
        for (WorkFlowReportBean report : reportList) {
            if (report != null) {
                csvContent.append(report.getSerialNo());
                csvContent.append(',');
                csvContent.append(report.getStartDate().replace('T', ' '));
                csvContent.append(',');
                if (report.getEndDate() != null) {
                    csvContent.append(report.getEndDate().replace('T', ' '));
                }
                csvContent.append(',');
                csvContent.append(report.getInitiator());
                csvContent.append(',');
                csvContent.append(report.getUnit());
                csvContent.append(',');
                csvContent.append(report.getCurrentState());
                csvContent.append(',');
                csvContent.append(report.getCurrentAssignee());
                csvContent.append(',');
                csvContent.append(report.getWfTitle());
                csvContent.append(',');
                csvContent.append(report.getStatus());
                csvContent.append(',');
                csvContent.append(report.getPayLoad());
                csvContent.append(',');
                csvContent.append(report.getPdfPath());
                csvContent.append(',');
                csvContent.append(report.getSitePath());

                csvContent.append('\n');
            }
        }
        return csvContent;
    }

}
