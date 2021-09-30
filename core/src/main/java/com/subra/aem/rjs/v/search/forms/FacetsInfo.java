package com.subra.aem.rjs.v.search.forms;


public class FacetsInfo {

    private String facetsTitle;
    private String facetsTagId;
    private boolean isChecked;
    private Long counts;

    public FacetsInfo(String facetsTitle, String facetsTagId, boolean isChecked, Long counts) {
        this.facetsTitle = facetsTitle;
        this.facetsTagId = facetsTagId;
        this.isChecked = isChecked;
        this.counts = counts;
    }

    public String getFacetsTitle() {
        return facetsTitle;
    }

    public void setFacetsTitle(String facetsTitle) {
        this.facetsTitle = facetsTitle;
    }

    public String getFacetsTagId() {
        return facetsTagId;
    }

    public void setFacetsTagId(String facetsTagId) {
        this.facetsTagId = facetsTagId;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean flag) {
        this.isChecked = flag;
    }

    public Long getCounts() {
        return counts;
    }

    public void setCounts(Long counts) {
        this.counts = counts;
    }

}
