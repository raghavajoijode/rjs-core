package com.subra.aem.rjs.core;

public enum UserMapperService {

    EMAIL_SERVICE("rjs-email-service"),
    ADMIN_SERVICE("rjs-admin-service"),
    WORKFLOW("workflow");

    private final String value;

    UserMapperService(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

}
