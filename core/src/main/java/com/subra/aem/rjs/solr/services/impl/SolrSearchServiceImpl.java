package com.subra.aem.rjs.solr.services.impl;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.subra.aem.rjs.core.utils.RJSResourceUtils;
import com.subra.aem.rjs.solr.services.SolrSearchService;
import com.subra.aem.rjs.solr.utils.SolrUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.*;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Raghava Joijode
 */
@Component(service = SolrSearchService.class)
public class SolrSearchServiceImpl implements SolrSearchService {

    private static final Logger LOG = LoggerFactory.getLogger(SolrSearchServiceImpl.class);

    @Reference
    private QueryBuilder queryBuilder;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public JSONArray crawlContent(String resourcePath, String resourceType) {
        Map<String, String> params = new HashMap<>();
        params.put("path", resourcePath);
        params.put("type", resourceType);
        params.put("p.offset", "0");
        params.put("p.limit", "10000");
        try (ResourceResolver resourceResolver = RJSResourceUtils.getAdminServiceResourceResolver(resourceResolverFactory)) {
            Session session = resourceResolver.adaptTo(Session.class);
            Query query = queryBuilder.createQuery(PredicateGroup.create(params), session);
            SearchResult searchResults = query.getResult();
            LOG.info("Found '{}' matches for query", searchResults.getTotalMatches());
            if (resourceType.equalsIgnoreCase("cq:PageContent"))
                return createPageMetadataArray(searchResults);
        } catch (RepositoryException | LoginException e) {
            LOG.error("Exception due to", e);
        }
        return null;
    }

    @Override
    public JSONArray createPageMetadataArray(SearchResult results) throws RepositoryException {
        JSONArray solrDocs = new JSONArray();
        for (Hit hit : results.getHits()) {
            Resource pageContent = hit.getResource();
            ValueMap properties = pageContent.adaptTo(ValueMap.class);
            String isPageIndexable = properties.get("notsolrindexable", String.class);
            if (null != isPageIndexable && isPageIndexable.equals("true"))
                continue;
            JSONObject propertiesMap = createPageMetadataObject(pageContent);
            solrDocs.put(propertiesMap);
        }

        return solrDocs;
    }

    @Override
    public JSONObject createPageMetadataObject(Resource pageContent) {
        Map<String, Object> propertiesMap = new HashMap<>();
        propertiesMap.put("id", pageContent.getParent().getPath());
        propertiesMap.put("url", pageContent.getParent().getPath() + ".html");
        ValueMap properties = pageContent.adaptTo(ValueMap.class);
        String pageTitle = properties.get("jcr:title", String.class);
        if (StringUtils.isEmpty(pageTitle))
            pageTitle = pageContent.getParent().getName();
        propertiesMap.put(SolrUtils.TITLE, pageTitle);
        propertiesMap.put(SolrUtils.DESCRIPTION, SolrUtils.checkNull(properties.get("jcr:description", String.class)));
        propertiesMap.put("publishDate", SolrUtils.checkNull(properties.get("publishdate", String.class)));
        propertiesMap.put("body", "");
        propertiesMap.put(SolrUtils.LAST_MODIFIED, SolrUtils.solrDate(properties.get("cq:lastModified", Calendar.class)));
        propertiesMap.put(SolrUtils.CONTENT_TYPE, "page");
        propertiesMap.put("tags", SolrUtils.getPageTags(pageContent));
        return new JSONObject(propertiesMap);
    }

    @Override
    public boolean indexPagesToSolr(JSONArray indexPageData, HttpSolrClient server)
            throws JSONException, SolrServerException, IOException {
        if (null != indexPageData) {
            for (int i = 0; i < indexPageData.length(); i++) {
                JSONObject pageJsonObject = indexPageData.getJSONObject(i);
                SolrInputDocument doc = createPageSolrDoc(pageJsonObject);
                server.add(doc);
            }
            server.commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean indexPageToSolr(JSONObject indexPageData, HttpSolrClient server)
            throws JSONException, SolrServerException, IOException {
        if (null != indexPageData) {
            SolrInputDocument doc = createPageSolrDoc(indexPageData);
            server.add(doc);
            server.commit();
            return true;
        }

        return false;
    }

    private SolrInputDocument createPageSolrDoc(JSONObject pageJsonObject) throws JSONException {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id", pageJsonObject.get("id"));
        doc.addField(SolrUtils.TITLE, pageJsonObject.get(SolrUtils.TITLE));
        doc.addField("body", pageJsonObject.get("body"));
        doc.addField("url", pageJsonObject.get("url"));
        doc.addField(SolrUtils.DESCRIPTION, pageJsonObject.get(SolrUtils.DESCRIPTION));
        doc.addField(SolrUtils.LAST_MODIFIED, pageJsonObject.get(SolrUtils.LAST_MODIFIED));
        doc.addField(SolrUtils.CONTENT_TYPE, pageJsonObject.get(SolrUtils.CONTENT_TYPE));
        doc.addField("tags", pageJsonObject.get("tags"));
        return doc;
    }

}
