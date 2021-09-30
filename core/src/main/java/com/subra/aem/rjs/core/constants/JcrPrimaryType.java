package com.subra.aem.rjs.core.constants;

import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;

public enum JcrPrimaryType {

    UNSTRUCTURED(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_UNSTRUCTURED),
    FILE(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_FILE),
    FOLDER(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_FOLDER),
    SLING_FOLDER(JcrConstants.JCR_PRIMARYTYPE, JcrResourceConstants.NT_SLING_FOLDER),
    SLING_ORDERED_FOLDER(JcrConstants.JCR_PRIMARYTYPE, JcrResourceConstants.NT_SLING_ORDERED_FOLDER);

    private String property;
    private String value;

    private JcrPrimaryType(final String property, final String value) {
        this.property = property;
        this.value = value;
    }

    public String property() {
        return property;
    }

    public String value() {
        return value;
    }

}
