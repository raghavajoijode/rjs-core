package com.subra.aem.rjs.v.search.forms.services;

import com.subra.aem.rjs.v.search.forms.FacetsInfo;
import com.subra.aem.rjs.v.search.forms.SearchResultsInfo;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.List;
import java.util.Map;

public interface FormsDocumentsSearch {

    Map<String, List<FacetsInfo>> loadFacets(SlingHttpServletRequest slingRequest, String councilSpPath);

    List<FacetsInfo> loadFacetsFromList(SlingHttpServletRequest slingRequest, String[] pathList);

    SearchResultsInfo getSearchResultsInfo();

    void executeSearch(ResourceResolver resourceResolver, String q, String path, String[] tags, String formDocumentContentPath, Map<String, List<FacetsInfo>> facetsAndTags);

}
