package com.subra.aem.rjs.v.search.services;


import org.apache.sling.api.SlingHttpServletRequest;

import javax.jcr.query.QueryResult;

public interface GSJcrSearchService {

    QueryResult search(final SlingHttpServletRequest request, final String path, final String type, final String searchText);
    QueryResult search(final SlingHttpServletRequest request, final String query);
    QueryResult searchWithOffset(final SlingHttpServletRequest request, final String query, final long limit, final long offset);

}
