package com.subra.aem.rjs.examples.workflow.beans;


public class WorkFlowReportBean {

	private long serialNo;
	private String startDate;
	private String endDate;
	private String initiator;
	private String wfTitle;
	private String status;
	private String payLoad;
	private String currentState;
	private String unit;
	private String currentAssignee;
	private String pdfPath;
	private String sitePath;

	public String getWfTitle() {
		return wfTitle;
	}

	public void setWfTitle(String wfTitle) {
		this.wfTitle = wfTitle;
	}

	public long getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(long l) {
		this.serialNo = l;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getInitiator() {
		return initiator;
	}

	public void setInitiator(String initiator) {
		this.initiator = initiator;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPayLoad() {
		return payLoad;
	}

	public void setPayLoad(String payLoad) {
		this.payLoad = payLoad;
	}

	public String getCurrentState() {
		return currentState;
	}

	public void setCurrentState(String currentState) {
		this.currentState = currentState;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getCurrentAssignee() {
		return currentAssignee;
	}

	public void setCurrentAssignee(String currentAssignee) {
		this.currentAssignee = currentAssignee;
	}

	public String getSitePath() {
		return sitePath;
	}

	public void setSitePath(String sitePath) {
		this.sitePath = sitePath;
	}

	public String getPdfPath() {
		return pdfPath;
	}

	public void setPdfPath(String pdfPath) {
		this.pdfPath = pdfPath;
	}

}
