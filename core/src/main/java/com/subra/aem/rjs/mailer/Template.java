package com.subra.aem.rjs.mailer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.subra.aem.rjs.core.RJSResource;
import com.subra.aem.rjs.core.utils.RJSResourceUtils;
import com.subra.aem.rjs.mailer.internal.helpers.MailerHelper;
import org.apache.sling.api.resource.Resource;
import org.subra.commons.helpers.CommonHelper;

import java.util.List;


public class Template extends RJSResource {

    private boolean isDraft = true;
    private String message;
    private List<String> lookUps;
    private String id;

    public Template(Resource resource) {
        super(resource);
        setMessage(RJSResourceUtils.getFileJCRData(getResource()));
        setId(MailerHelper.getTemplateId(this));
    }

    public Template(String a) {
        super(null);
        this.message = a;
        setId(String.valueOf(a.hashCode()));
    }

    public boolean isDraft() {
        return isDraft;
    }

    public void setDraft(boolean isDraft) {
        this.isDraft = isDraft;
    }

    public List<String> getLookUps() {
        return lookUps;
    }

    public void setLookUps(List<String> lookUps) {
        this.lookUps = lookUps;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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