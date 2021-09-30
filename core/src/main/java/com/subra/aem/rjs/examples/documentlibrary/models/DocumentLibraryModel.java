package com.subra.aem.rjs.examples.documentlibrary.models;

import com.subra.aem.rjs.examples.documentlibrary.services.DocumentLibraryService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

@Model(adaptables = SlingHttpServletRequest.class)
public class DocumentLibraryModel {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private ResourceResolver resourceResolver = null;

    @Self
    private SlingHttpServletRequest request;

    @OSGiService
    private DocumentLibraryService documentLibraryService;

    @ValueMapValue
    @Default(values = "/content/dam")
    private String path;

    @PostConstruct
    public void activate() {
        log.info("entered DocumentLibraryModel class");
        resourceResolver = request.getResourceResolver();
    }

    public String getListing() throws JSONException {
        log.info("entered listing method");
        Resource imageFolder = resourceResolver.getResource(path);
        return imageFolder == null ? null : documentLibraryService.extractAssetList(imageFolder).toString();
    }

}