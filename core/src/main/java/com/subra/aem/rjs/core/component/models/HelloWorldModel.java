package com.subra.aem.rjs.core.component.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.*;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import java.util.Optional;

import static org.apache.sling.api.resource.ResourceResolver.PROPERTY_RESOURCE_TYPE;

@Model(adaptables = SlingHttpServletRequest.class)
public class HelloWorldModel {

    @ValueMapValue(name = PROPERTY_RESOURCE_TYPE, injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "No resourceType")
    protected String resourceType;

    @OSGiService
    private SlingSettingsService settings;

    @Self
    private SlingHttpServletRequest request;

    @SlingObject
    private Resource currentResource;

    @SlingObject
    private ResourceResolver resourceResolver;

    private String message;
    private String message2;

    @PostConstruct
    protected void init() {
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        String currentPagePath = Optional.ofNullable(pageManager)
                .map(pm -> pm.getContainingPage(currentResource))
                .map(Page::getPath).orElse("");

        String a = request != null ? "request context path" + request.getContextPath() + "Success" : "request is null";
        String c = currentResource != null ? "currentResource path" + currentResource.getPath() : "currentResource is null";

        message = "Hello World !\n"
                + "Resource type is : " + resourceType + "\n"
                + "Current page is:  " + currentPagePath + "\n"
                + "This is instance: " + settings.getSlingId() + "\n";

        message2 = "Hello World Customized!\n"
                + "a: " + a + "\n"
                + "c: " + c + "\n";
    }

    public String getMessage() {
        return message;
    }

    public String getMessage2() {
        return message2;
    }

}
