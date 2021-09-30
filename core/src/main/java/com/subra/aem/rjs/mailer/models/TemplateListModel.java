package com.subra.aem.rjs.mailer.models;

import com.subra.aem.rjs.mailer.Template;

import java.util.List;


public interface TemplateListModel {

    default List<Template> getTemplates() {
        throw new UnsupportedOperationException();
    }

}
