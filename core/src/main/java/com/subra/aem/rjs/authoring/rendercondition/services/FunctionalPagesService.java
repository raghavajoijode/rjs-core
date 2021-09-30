package com.subra.aem.rjs.authoring.rendercondition.services;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import java.util.List;

public interface FunctionalPagesService {

    List<String> getFunctionalPages();

    boolean isPageFunctional(final String pagePath);

    @ObjectClassDefinition(name = "RJS Functional Pages Configuration", description = "RJS Functional Pages Configuration")
    @interface Config {
        @AttributeDefinition(name = "Functional Page List", description = "Add list of functional pages")
        String[] functional_page_list() default {
                "/content/rjs/global/en/login",
                "/content/rjs/global/en/checkout"
        };
    }

}