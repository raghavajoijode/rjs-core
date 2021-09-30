package com.subra.aem.rjs.core.constants;

import java.util.Calendar;

public enum JcrProperties {

    PN_TITLE("subra-title", String.class), PN_DESCRIPTION("subra-description", String.class),
    PN_HIDDEN("subra-hidden", Boolean.class),

    PN_WF_PENDING("subra-wfPending", String.class), // pending with user-id
    PN_WF_APPROVED("subra-wfApproved", String.class), // Approver user-id
    PN_WF_REJECTED("subra-wfRejected", String.class), // Rejector user-id
    PN_WF_STATUS("subra-wfStatus", String.class), // Pending - Approved - Rejected

    PN_LOCKED("subra-isLocked", Boolean.class), PN_TO_DELETE_ON("subra-toDeleteOn", Calendar.class),

    PN_CREATED_BY("subra-createdBy", String.class), PN_CREATED_ON("subra-createdOn", Calendar.class),
    PN_UPDATED_BY("subra-updatedBy", String.class), PN_UPDATED_ON("subra-updatedOn", Calendar.class);

    private String property;
    private Class<?> type;

    private JcrProperties(final String property, final Class<?> type) {
        this.property = property;
        this.type = type;
    }

    public String property() {
        return property;
    }

    public Class<?> type() {
        return type;
    }

}
