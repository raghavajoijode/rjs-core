package com.subra.aem.rjs.v.search.forms.services.impl;

import com.day.cq.search.QueryBuilder;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.subra.aem.rjs.v.search.forms.FacetsInfo;
import com.subra.aem.rjs.v.search.forms.services.FacetBuilder;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Component(service = FacetBuilder.class)
public class FacetBuilderImpl implements FacetBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(FacetBuilderImpl.class);

    public Map<String, List<FacetsInfo>> getFacets(SlingHttpServletRequest slingRequest, QueryBuilder queryBuilder, String facetsPath) {
        ResourceResolver resourceResolver = slingRequest.getResourceResolver();
        LOGGER.debug("Building Facets ");
        TagManager tagMgr = resourceResolver.adaptTo(TagManager.class);
        Resource tagResource = resourceResolver.getResource(facetsPath);
        if (tagResource == null || tagMgr == null) {
            LOGGER.error("The repository requires {} to function properly to support tagging.", facetsPath);
            return null;
        }
        Iterator<Resource> resources = tagResource.listChildren();
        Map<String, List<FacetsInfo>> facets = new HashMap<>();
        while (resources.hasNext()) {
            Resource resource = resources.next();
            Tag tag = tagMgr.resolve(resource.getPath());
            List<FacetsInfo> tagItems = new ArrayList<>();
            TreeSet<String> tagTree = new TreeSet<>();
            Iterator<Resource> childFacets = resourceResolver.listChildren(resource);
            while (childFacets.hasNext()) {
                Tag cTag = tagMgr.resolve(childFacets.next().getPath());
                if (!tagTree.contains(cTag.getTagID())) {
                    tagItems.add(new FacetsInfo(cTag.getTitle(), cTag.getTagID(), false, 0L));
                    tagTree.add(cTag.getTagID());
                }
            }
            facets.put(tag.getName(), tagItems);
        }
        return facets;
    }

    public FacetsInfo getFacet(SlingHttpServletRequest slingRequest, String path) {
        ResourceResolver resourceResolver = slingRequest.getResourceResolver();
        TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
        Tag tag;
        if (tagManager != null && (tag = tagManager.resolve(path)) != null) {
            return new FacetsInfo(tag.getTitle(), tag.getTagID(), false, 0L);
        } else {
            return null;
        }
    }
}
