package com.subra.aem.rjs.authoring.rendercondition.models;

import com.adobe.granite.ui.components.rendercondition.RenderCondition;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SimpleRenderCondition {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleRenderCondition.class);

    @Self
    private SlingHttpServletRequest request;

    @SlingObject
    private ResourceResolver resourceResolver;

    @ValueMapValue
    private boolean expression;

    @PostConstruct
    public void init() {
        LOGGER.info("In SimpleRenderCondition group {}", expression);
        request.setAttribute(RenderCondition.class.getName(), new com.adobe.granite.ui.components.rendercondition.SimpleRenderCondition(expression));
    }

}