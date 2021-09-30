/**
 *
 */
package com.subra.aem.rjs.examples.workflow.models;

import com.day.cq.tagging.Tag;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.jcr.Session;

@Model(adaptables = {SlingHttpServletRequest.class})
public class WorkFlowStartPage {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkFlowStartPage.class);
    private static final String VALUE = "value";
    @OSGiService
    ResourceResolverFactory resourceResolverFactory;
    Resource serviceRes;
    Resource verticalRes;
    Resource themesRes;
    Session session;

    @Self
    private SlingHttpServletRequest request;

    @PostConstruct
    public void activate() {
        ResourceResolver resourceResolver = request.getResourceResolver();
        session = resourceResolver.adaptTo(Session.class);
        LOGGER.info(" Check for resolver:: {} State :: {}", resourceResolver.getUserID(), resourceResolver.isLive());
        String serviceLine = "/etc/tags/demo/primary-service-line";
        String businessVertical = "/etc/tags/demo/primary-vertical";
        String themes = "/etc/tags/demo/themes";
        serviceRes = resourceResolver.getResource(serviceLine);
        verticalRes = resourceResolver.getResource(businessVertical);
        themesRes = resourceResolver.getResource(themes);
    }

    public JSONArray getItems() throws JSONException {
        JSONArray tagList = new JSONArray();
        for (Resource y : serviceRes.getChildren()) {
            JSONObject obj = new JSONObject();
            Tag serviceTag = y.adaptTo(Tag.class);
            String tagNameFull = serviceTag.getTagID();
            String tagName = tagNameFull.substring(tagNameFull.lastIndexOf('/') + 1, tagNameFull.length());
            String tagTitle = serviceTag.getTitle();
            obj.put(VALUE, tagName);
            obj.put("text", tagTitle);
            tagList.put(obj);

        }
        for (Resource y : verticalRes.getChildren()) {
            JSONObject obj = new JSONObject();
            Tag verticalTag = y.adaptTo(Tag.class);
            String tagNameFull = verticalTag.getTagID();
            String tagName = tagNameFull.substring(tagNameFull.lastIndexOf('/') + 1, tagNameFull.length());
            String tagTitle = verticalTag.getTitle();
            obj.put(VALUE, tagName);
            obj.put("text", tagTitle);
            tagList.put(obj);

        }
        return tagList;

    }

    public JSONArray getThemes() throws JSONException {
        JSONArray themeList = new JSONArray();
        for (Resource y : themesRes.getChildren()) {
            JSONObject obj = new JSONObject();
            Tag themeTag = y.adaptTo(Tag.class);
            String tagNameFull = themeTag.getTagID();
            String tagName = tagNameFull.substring(tagNameFull.lastIndexOf('/') + 1, tagNameFull.length());
            String tagTitle = themeTag.getTitle();
            obj.put(VALUE, tagName);
            obj.put("text", tagTitle);
            themeList.put(obj);

        }
        return themeList;
    }

    public String getUserId() {
        String id = session.getUserID();
        LOGGER.info("Enered Value {}", id);
        return id;
    }

}
