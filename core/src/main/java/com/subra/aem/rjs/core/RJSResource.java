package com.subra.aem.rjs.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.subra.aem.rjs.core.utils.RJSResourceUtils;
import org.apache.sling.api.resource.Resource;
import org.subra.commons.helpers.CommonHelper;

import java.util.Calendar;

public class RJSResource {

    private String title;

    private String description;

    private boolean isHidden;

    private boolean isLocked;

    private String wfPending;

    private String wfApproved;

    private String wfRejected;

    private String wfStatus;

    private Calendar toDeleteOn;

    private String createdBy;

    private Calendar createdOn;

    private String updatedBy;

    private Calendar updatedOn;

    private String path;

    private String name;

    @JsonIgnore
    private Resource resource;

    public RJSResource(Resource resource) {
        RJSResourceUtils.updateRJSResource(resource, this);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean isHidden) {
        this.isHidden = isHidden;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    public String getWfPending() {
        return wfPending;
    }

    public void setWfPending(String wfPending) {
        this.wfPending = wfPending;
    }

    public String getWfApproved() {
        return wfApproved;
    }

    public void setWfApproved(String wfApproved) {
        this.wfApproved = wfApproved;
    }

    public String getWfRejected() {
        return wfRejected;
    }

    public void setWfRejected(String wfRejected) {
        this.wfRejected = wfRejected;
    }

    public String getWfStatus() {
        return wfStatus;
    }

    public void setWfStatus(String wfStatus) {
        this.wfStatus = wfStatus;
    }

    public Calendar getToDeleteOn() {
        return toDeleteOn;
    }

    public void setToDeleteOn(Calendar toDeleteOn) {
        this.toDeleteOn = toDeleteOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Calendar getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Calendar createdOn) {
        this.createdOn = createdOn;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Calendar getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Calendar updatedOn) {
        this.updatedOn = updatedOn;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        try {
            return CommonHelper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return super.toString();
        }
    }

}
