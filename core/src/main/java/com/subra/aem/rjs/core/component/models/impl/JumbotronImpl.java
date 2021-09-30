package com.subra.aem.rjs.core.component.models.impl;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.subra.aem.rjs.core.component.base.AbstractComponentModel;
import com.subra.aem.rjs.core.component.models.Jumbotron;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;


@Model(adaptables = {SlingHttpServletRequest.class}, adapters = {Jumbotron.class,
        ComponentExporter.class}, resourceType = JumbotronImpl.RESOURCE_TYPE)

@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION, options = {
        @ExporterOption(name = "MapperFeature.SORT_PROPERTIES_ALPHABETICALLY", value = "true"),
        @ExporterOption(name = "SerializationFeature.WRITE_DATES_AS_TIMESTAMPS", value = "false")})
public class JumbotronImpl extends AbstractComponentModel implements Jumbotron {

    protected static final String RESOURCE_TYPE = "foundation/components/content/jumbotron";

    @ValueMapValue
    private String heading;

    @ValueMapValue
    private String text;

    @PostConstruct
    protected void init() {
        super.isEmpty = StringUtils.isAllBlank(heading, text);
    }

    @Override
    public String getHeading() {
        return heading;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public String getExportedType() {
        return RESOURCE_TYPE;
    }

}
