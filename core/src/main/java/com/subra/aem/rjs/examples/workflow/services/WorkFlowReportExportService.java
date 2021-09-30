package com.subra.aem.rjs.examples.workflow.services;


import com.subra.aem.rjs.examples.workflow.beans.WorkFlowReportBean;

import java.util.List;


public interface WorkFlowReportExportService {

    StringBuilder populateCSVContent(List<WorkFlowReportBean> reportList);

}
