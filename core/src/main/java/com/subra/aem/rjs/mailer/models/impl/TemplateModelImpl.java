package com.subra.aem.rjs.mailer.models.impl;

import com.subra.aem.rjs.mailer.Template;
import com.subra.aem.rjs.mailer.models.TemplateModel;
import com.subra.aem.rjs.mailer.services.TemplateService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.RequestAttribute;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subra.commons.exceptions.RJSCustomException;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;


@Model(adaptables = {SlingHttpServletRequest.class, Resource.class}, adapters = {TemplateModel.class})
public class TemplateModelImpl implements TemplateModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateModelImpl.class);

    @Self
    private SlingHttpServletRequest request;

    @RequestAttribute
    private String templatePath;

    @Reference
    private TemplateService templateService;

    @RequestAttribute(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String id;

    private Template template;

    @PostConstruct
    protected void init() {
        try {
            template = templateService.getTemplate(id);
        } catch (RJSCustomException e) {
            LOGGER.error("Error reading template...", e);
        }
    }

    @Override
    public Template getTemplate() {
        return template;
    }

    @Override
    public String getMessage() {
        return template != null ? templateService.readTemplate(template) : StringUtils.EMPTY;
    }

    @Override
    public List<String> getLookUpKeys() {
        return template != null ? templateService.getLookUpKeys(template) : Collections.emptyList();
    }

}
