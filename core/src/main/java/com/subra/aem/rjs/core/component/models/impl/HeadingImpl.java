package com.subra.aem.rjs.core.component.models.impl;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.designer.Style;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.subra.aem.rjs.core.component.ComponentUtils;
import com.subra.aem.rjs.core.component.models.Heading;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class}, adapters = Heading.class, resourceType = {
        HeadingImpl.RESOURCE_TYPE})
public class HeadingImpl implements Heading {

    protected static final String RESOURCE_TYPE = "foundation/components/content/heading";

    private boolean linkDisabled = false;

    @Self
    private SlingHttpServletRequest request;

    @ScriptVariable
    private Resource resource;

    @ScriptVariable
    private PageManager pageManager;

    @ScriptVariable
    private Page currentPage;

    @ScriptVariable(injectionStrategy = InjectionStrategy.OPTIONAL)
    @JsonIgnore
    private Style currentStyle;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String heading;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String type;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String linkURL;

    /**
     * The {@link com.adobe.cq.wcm.core.components.internal.Utils.Heading} object
     * for the type of this title.
     */
    private ComponentUtils.HeadingTypes headingType;

    @PostConstruct
    private void init() {
        // Authored Title | Page Title | JCR Title | Page Name.
        if (StringUtils.isBlank(heading)) {
            heading = StringUtils.defaultIfEmpty(currentPage.getPageTitle(), currentPage.getTitle());
        }

        // Valid type of heading tag, if not valid sets null
        if (headingType == null) {
            headingType = ComponentUtils.HeadingTypes.getHeading(type);
            if (headingType == null && currentStyle != null) {
                headingType = ComponentUtils.HeadingTypes
                        .getHeading(currentStyle.get(PN_DESIGN_DEFAULT_TYPE, String.class));
            }
        }

        // Valid page url or null
        if (StringUtils.isNotEmpty(linkURL)) {
            linkURL = ComponentUtils.getURL(request, pageManager, linkURL);
        } else {
            linkURL = null;
        }

        // Checks if link is disabled
        if (currentStyle != null) {
            linkDisabled = currentStyle.get("linkDisabled", linkDisabled);
        }
    }

    @Override
    public String getHeading() {
        return heading;
    }

    @Override
    public String getType() {
        return headingType != null ? headingType.getElement() : heading;
    }

    @Override
    public String getLinkURL() {
        return linkURL;
    }

    @Override
    public boolean isLinkDisabled() {
        return linkDisabled;
    }

}
