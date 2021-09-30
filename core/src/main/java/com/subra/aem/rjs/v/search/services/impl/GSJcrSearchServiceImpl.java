package com.subra.aem.rjs.v.search.services.impl;

import com.subra.aem.rjs.v.search.services.GSJcrSearchService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@Component(service = GSJcrSearchService.class)
@ServiceDescription("GSJcr Search Service Service")
public class GSJcrSearchServiceImpl implements GSJcrSearchService {
    private static final String QUERY_LANGUAGE = "JCR-SQL2";
    private static final String EXPRESSION = "SELECT [jcr:score], [jcr:path], [jcr:primaryType] FROM [%s] AS s WHERE ISDESCENDANTNODE([%s]) and CONTAINS(s.*, '%s')";

    private static final Logger LOGGER = LoggerFactory.getLogger(GSJcrSearchServiceImpl.class);


    @Override
    public QueryResult search(SlingHttpServletRequest request, String path, String type, String searchText) {
        try {
            String decodedSearchText = URLDecoder.decode(searchText, "UTF-8");
            decodedSearchText = decodedSearchText.replace("'", "''");
            String query = String.format(EXPRESSION, type, path, decodedSearchText);
            QueryManager queryManager = getSession(request).getWorkspace().getQueryManager();
            Query sql2Query = queryManager.createQuery(query, QUERY_LANGUAGE);
            return sql2Query.execute();
        } catch (RepositoryException | UnsupportedEncodingException e) {
            LOGGER.error("Exception during search ", e);
        }
        return null;
    }

    @Override
    public QueryResult search(final SlingHttpServletRequest request, String query) {
        QueryResult result = null;
        try {
            QueryManager queryManager = getSession(request).getWorkspace().getQueryManager();
            Query sql2Query = queryManager.createQuery(query, QUERY_LANGUAGE);
            long startTime = System.nanoTime();
            result = sql2Query.execute();
            long endTime = System.nanoTime();
            double duration = (endTime - startTime) / 1000000;
            LOGGER.error("Execution of : {} took {} milliseconds", query, duration);
        } catch (RepositoryException e) {
            LOGGER.error("Exception during search ", e);
        }
        return result;
    }

    @Override
    public QueryResult searchWithOffset(final SlingHttpServletRequest request, String query, long limit, long offset) {
        try {
            QueryManager queryManager = getSession(request).getWorkspace().getQueryManager();
            Query sql2Query = queryManager.createQuery(query, QUERY_LANGUAGE);
            sql2Query.setLimit(limit);
            sql2Query.setOffset(offset);
            return sql2Query.execute();
        } catch (RepositoryException e) {
            LOGGER.error("Exception during search ", e);
        }
        return null;
    }

    private Session getSession(final SlingHttpServletRequest request) {
        return request.getResourceResolver().adaptTo(Session.class);
    }
}
