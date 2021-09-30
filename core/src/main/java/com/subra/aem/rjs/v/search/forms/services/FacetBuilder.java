package com.subra.aem.rjs.v.search.forms.services;

import com.day.cq.search.QueryBuilder;
import com.subra.aem.rjs.v.search.forms.FacetsInfo;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.List;
import java.util.Map;

public interface FacetBuilder {

    Map<String, List<FacetsInfo>> getFacets(SlingHttpServletRequest slingRequest, QueryBuilder queryBuilder, String facetsPath);

    FacetsInfo getFacet(SlingHttpServletRequest slingRequest, String path);
}
