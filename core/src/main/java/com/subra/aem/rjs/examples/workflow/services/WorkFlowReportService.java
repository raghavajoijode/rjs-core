package com.subra.aem.rjs.examples.workflow.services;

import com.subra.aem.rjs.examples.workflow.beans.WorkFlowReportBean;
import org.json.JSONArray;
import org.json.JSONException;

import javax.jcr.Session;
import java.util.List;


public interface WorkFlowReportService {

    List<WorkFlowReportBean> getReportList();

    JSONArray queryBuilder(Session session, String status, String startDate, String endDate) throws JSONException;

}
