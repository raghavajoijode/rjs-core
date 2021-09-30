package com.subra.aem.rjs.demos.servlets;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

public class ServletConfig {

    @ObjectClassDefinition(name = "RJ CQ Redirect Servlet")
    public @interface Config {
        @AttributeDefinition(name = "Excluded resource types", description = "List of resource types which should be ignored by this redirect servlet.")
        String[] excluded_resource_types();
    }
}
