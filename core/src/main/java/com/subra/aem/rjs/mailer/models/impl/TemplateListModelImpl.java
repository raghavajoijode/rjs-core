package com.subra.aem.rjs.mailer.models.impl;

import com.subra.aem.rjs.mailer.Template;
import com.subra.aem.rjs.mailer.models.TemplateListModel;
import com.subra.aem.rjs.mailer.services.TemplateService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.RequestAttribute;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subra.commons.helpers.CommonHelper;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author Raghava Joijode
 * <p>
 * Implementation of TemplateListModel, to list out all templates.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class}, adapters = {TemplateListModel.class})
public class TemplateListModelImpl implements TemplateListModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateListModelImpl.class);
    List<Template> templates;
    @Self
    private SlingHttpServletRequest request;
    @Reference
    private TemplateService templateService;
    @RequestAttribute
    private String templatePath;

    @PostConstruct
    protected void init() {
        templates = CommonHelper.getCacheData(request, "templates", () -> templateService.listTemplates());
        LOGGER.debug("TemplateListModel initialized...");
    }

    @Override
    public List<Template> getTemplates() {
        return templates;
    }

}
