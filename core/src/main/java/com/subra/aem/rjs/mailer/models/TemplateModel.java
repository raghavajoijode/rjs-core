package com.subra.aem.rjs.mailer.models;

import com.subra.aem.rjs.mailer.Template;

import java.util.List;

public interface TemplateModel {

    default Template getTemplate() {
        throw new UnsupportedOperationException();
    }

    default String getMessage() {
        throw new UnsupportedOperationException();
    }

    default List<String> getLookUpKeys() {
        throw new UnsupportedOperationException();
    }

}
