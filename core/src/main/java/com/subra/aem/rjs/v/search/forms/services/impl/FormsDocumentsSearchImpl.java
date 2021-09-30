package com.subra.aem.rjs.v.search.forms.services.impl;


import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.subra.aem.rjs.v.search.forms.DocHit;
import com.subra.aem.rjs.v.search.forms.FacetsInfo;
import com.subra.aem.rjs.v.search.forms.SearchResultsInfo;
import com.subra.aem.rjs.v.search.forms.services.FacetBuilder;
import com.subra.aem.rjs.v.search.forms.services.FormsDocumentsSearch;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import java.util.*;

@Component(service = FormsDocumentsSearch.class)
public class FormsDocumentsSearchImpl implements FormsDocumentsSearch {

    private static final Logger LOGGER = LoggerFactory.getLogger(FormsDocumentsSearchImpl.class);

    private static final String FACETS_PATH = "/etc/tags/girlscouts";
    private static final String COUNCIL_SPE_PATH = "/content/cq:tags/"; //""/etc/tags/"
    private static final String FORM_DOC_CATEGORY = "forms_documents";

    @Reference
    private FacetBuilder facetBuilder;

    @Reference
    private QueryBuilder queryBuilder;

    private Map<String, List<FacetsInfo>> facets;
    private SearchResultsInfo searchResultsInfo;

    public Map<String, List<FacetsInfo>> loadFacets(SlingHttpServletRequest slingRequest, String councilName) {
        Map<String, List<FacetsInfo>> fts = null;
        if (councilName != null && !councilName.isEmpty()) {
            String councilSpPath = COUNCIL_SPE_PATH + councilName; // /etc/tags/<cname>
            LOGGER.info("councilSpPath  [{}]", councilSpPath);
            fts = facetBuilder.getFacets(slingRequest, queryBuilder, councilSpPath);
            if (fts == null || !fts.containsKey(FORM_DOC_CATEGORY)) {
                LOGGER.error("Facets [{}] does not exists fall-back to default", councilSpPath); // /etc/tags/girlscouts/forms_documents
                fts = facetBuilder.getFacets(slingRequest, this.queryBuilder, FACETS_PATH);
            }
        } else {
            LOGGER.error("coucilName empty.");
        }
        return fts == null ? new HashMap<>() : fts;
    }

    public List<FacetsInfo> loadFacetsFromList(SlingHttpServletRequest slingRequest, String[] pathList) {
        List<FacetsInfo> facetList = new ArrayList<>();
        for (String path : pathList) {
            FacetsInfo facet = facetBuilder.getFacet(slingRequest, path);
            if (facet != null) {
                facetList.add(facet);
            }
        }
        return facetList;
    }

    public void executeSearch(ResourceResolver resourceResolver, String q, String path, String[] checkedTags, String formDocumentContentPath, Map<String, List<FacetsInfo>> facets) {
        try {
            documentsSearch(resourceResolver, path, q, checkedTags, formDocumentContentPath, facets);
        } catch (RepositoryException re) {
            LOGGER.error(re.getMessage());
        }
    }

    private void documentsSearch(ResourceResolver resourceResolver, String path, String q, String[] tags, String formDocumentContentPath, Map<String, List<FacetsInfo>> facets) throws RepositoryException {

        long startTime = new Date().getTime();
        LOGGER.info("Start Time: {}", startTime);

        searchResultsInfo = new SearchResultsInfo();
        Session session = resourceResolver.adaptTo(Session.class);
        List<Hit> searchTermHits = new ArrayList<>();
        searchTermHits.addAll(performContentSearch(session, getPredicateGroup(formDocumentContentPath, q, tags)));
        searchTermHits.addAll(performContentSearch(session, getPredicateGroup(path, q, tags)));

        List<Hit> titleHits = new ArrayList<>();
        List<Hit> descriptionHits = new ArrayList<>();
        List<Hit> contentHits = new ArrayList<>();

        for (Hit h : searchTermHits) {
            DocHit d = new DocHit(h);
            if (d.getTitle().toLowerCase().contains(q.toLowerCase())) {
                titleHits.add(h);
            } else if (d.getDescription().toLowerCase().contains(q.toLowerCase())) {
                descriptionHits.add(h);
            } else {
                contentHits.add(h);
            }
        }

        List<Hit> sortedList = new ArrayList<>();

        sortedList.addAll(titleHits);
        sortedList.addAll(descriptionHits);
        sortedList.addAll(contentHits);

        this.searchResultsInfo.setHits(sortedList);
        this.searchResultsInfo = combineSearchTagsCounts(resourceResolver, facets);

        long endTime = new Date().getTime();
        LOGGER.info("End Time: {}", endTime);
        LOGGER.info("Time elapsed: {}", (endTime - startTime));

    }

    private SearchResultsInfo combineSearchTagsCounts(ResourceResolver resourceResolver, Map<String, List<FacetsInfo>> facets) throws RepositoryException {

        List<FacetsInfo> facetsInfo = null;
        try {
            facetsInfo = facets.get(FORM_DOC_CATEGORY);
        } catch (Exception e) {
            LOGGER.error("No Forms and Documents Tags Found in the /etc/tags/", e);
        }
        List<Hit> searchTermHits = this.searchResultsInfo.getHits();

        /*
          Duplicate Documents containing the same path are return when performing a search due to different renditions.
          So put the hit in to the uni TreeMap to remove duplicates.
         */
        Map<String, DocHit> unq = new TreeMap<>();
        List<Hit> hits = new ArrayList<>();
        for (Hit hit : searchTermHits) {
            DocHit docHit = new DocHit(hit);
            if (!unq.containsKey(docHit.getURL())) {
                unq.put(docHit.getURL(), docHit);
                hits.add(hit);
            }
        }
        Iterator<String> uniIterator = unq.keySet().iterator();
        Map<String, Long> facetWithCount = new HashMap<>();
        while (uniIterator.hasNext()) {
            updateFacetWithCount(resourceResolver, unq, uniIterator, facetWithCount);
        }

        //Populating the count for the search results.
        for (FacetsInfo info : facetsInfo) {
            if (facetWithCount.containsKey(info.getFacetsTagId())) {
                info.setCounts(facetWithCount.get(info.getFacetsTagId()));
            }
        }
        this.searchResultsInfo.setHits(hits);
        return this.searchResultsInfo;
    }

    private void updateFacetWithCount(ResourceResolver resourceResolver, Map<String, DocHit> unq, Iterator<String> uniIterator, Map<String, Long> facetWithCount) {
        try {
            // Get the path of the hits
            String cPath = unq.get(uniIterator.next()).getURL();
            Node node = resourceResolver.getResource(cPath + "/jcr:content").adaptTo(Node.class);
            // This is specific to the PDF and other DOC types, Since HTML document has cq:tags on the JCR:CONTENT, but not pdf and docx
            if (node.hasNode("metadata")) {
                node = node.getNode("metadata");

            }
            if (node.hasProperty("cq:tags")) {
                // We need to check for the multiple properties.
                Property tagProps = node.getProperty("cq:tags");
                Value[] value;

                if (tagProps.isMultiple()) {
                    value = tagProps.getValues();
                } else {
                    value = new Value[]{tagProps.getValue()};
                }
                for (Value val : value) {
                    String valueString = val.getString();
                    if (facetWithCount.containsKey(valueString)) {
                        facetWithCount.put(valueString, facetWithCount.get(valueString) + 1L);
                    } else {
                        facetWithCount.put(valueString, 1L);
                    }
                }
            }

        } catch (Exception e) {
            LOGGER.info("No Metadata found on the content", e);
        }
    }

    private Map<String, String> addToDefaultQuery(String[] tags) {

        Map<String, String> tagSearch = new HashMap<>();
        tagSearch.put("1_property", "jcr:content/metadata/cq:tags");
        tagSearch.put("1_property.or", "true");
        tagSearch.put("2_property", "jcr:content/cq:tags");
        tagSearch.put("2_property.or", "true");
        int count = 0;
        for (String tagPath : tags) {
            count++;
            LOGGER.info("Tag :::   [{}]", tagPath);
            tagSearch.put("1_property." + count + "_value", tagPath);
            tagSearch.put("2_property." + count + "_value", tagPath);
        }
        return tagSearch;
    }

    public Map<String, List<FacetsInfo>> getFacets() {
        return this.facets;
    }

    public SearchResultsInfo getSearchResultsInfo() {
        return searchResultsInfo;
    }

    private List<Hit> performContentSearch(Session session, PredicateGroup master) {

        Query query = this.queryBuilder.createQuery(master, session);
        query.setExcerpt(true);
        LOGGER.info("***SQL:*******[{}]", master);
        SearchResult searchResults = null;
        try {
            searchResults = query.getResult();
        } catch (Exception e) {
            LOGGER.error("Error Generated performContentSearch", e);
        }
        this.searchResultsInfo.setSearchResults(searchResults);
        return searchResults != null ? searchResults.getHits() : Collections.emptyList();
    }

    private PredicateGroup getPredicateGroup(String path, String query, String[] tags) {

        Map<String, String> mapContentDoc = new HashMap<>();
        mapContentDoc.put("group.p.or", "true");
        mapContentDoc.put("group.1_group.type", "cq:Page");
        if ((query != null && !query.isEmpty()) || tags.length > 0) {
            mapContentDoc.put("group.2_group.type", "nt:hierarchyNode");
        } else {
            mapContentDoc.put("group.2_group.type", "dam:AssetContent");
        }
        PredicateGroup predicateDocs = PredicateGroup.create(mapContentDoc);

        Map<String, String> masterMap = new HashMap<>();
        masterMap.put("p.limit", "-1");
        masterMap.put("path", path);


        PredicateGroup master = PredicateGroup.create(masterMap);
        master.add(predicateDocs);

        if (query != null && !query.isEmpty()) {
            LOGGER.info("Search Query Term [{}]", query);
            Map<String, String> mapFullText = new HashMap<>();
            /*
            mapFullText.put("group.p.or","true" )
            mapFullText.put("group.1_fulltext", query)
            mapFullText.put("group.1_fulltext.relPath", "jcr:content/@jcr:title"); ->  search cq:tags
            mapFullText.put("group.2_fulltext", query)
            mapFullText.put("group.2_fulltext.relPath", "jcr:content/metadata/@dc:title"); -> search title
            mapFullText.put("group.3_fulltext", query)
            mapFullText.put("group.3_fulltext.relPath", "jcr:content/metadata/@dc:description"); -> search description
            mapFullText.put("group.4_fulltext", query); //search everything, including file contents via PDFBox
             */
            mapFullText.put("fulltext", query); //search everything, including file contents via PDFBox
            PredicateGroup predicateFullText = PredicateGroup.create(mapFullText);
            master.add(predicateFullText);

        }
        if (tags.length > 0) {
            Map<String, String> checkedTagMap = addToDefaultQuery(tags);
            PredicateGroup predicateCheckedTags = PredicateGroup.create(checkedTagMap);
            predicateCheckedTags.setAllRequired(false);
            master.add(predicateCheckedTags);
        }

        master.setAllRequired(true);
        return master;
    }

}

