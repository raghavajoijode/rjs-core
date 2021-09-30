package com.subra.aem.rjs.v.search;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.subra.aem.rjs.v.search.forms.FacetsInfo;
import com.subra.aem.rjs.v.search.forms.services.FormsDocumentsSearch;
import com.subra.aem.rjs.v.search.utils.GSSearchResultComparator;
import com.subra.aem.rjs.v.search.utils.GSSearchResultConstants;
import com.subra.aem.rjs.v.utils.datetime.GSDateTime;
import com.subra.aem.rjs.v.utils.datetime.GSDateTimeFormat;
import com.subra.aem.rjs.v.utils.datetime.GSDateTimeFormatter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.query.QueryResult;
import javax.jcr.query.RowIterator;
import java.util.*;
import java.util.stream.Collectors;


public class GSSearchManagerUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(GSSearchManagerUtils.class);

    private static final String[] resourceTypeFilters = new String[]{"gsusaredesign/components/contact-placeholder-page", "gsusaredesign/components/contact-page"};
    private static final String[] resourcePathFilters = new String[]{"/contacts/", "/right-rails/", "/resources/", "/email-templates/"};

    private GSSearchManagerUtils() {
        throw new IllegalStateException(GSSearchManagerUtils.class.getName());
    }

    public static String getQueryParam(final SlingHttpServletRequest request) {
        String query = StringUtils.defaultIfBlank(request.getParameter("q"), StringUtils.EMPTY);
        return StringUtils.trim(query);
    }

    public static List<FacetsInfo> retrieveFacets(SlingHttpServletRequest request, FormsDocumentsSearch formsDocumentsSearch, boolean useCustomTagList, String[] customTagList, String basePath) {
        List<FacetsInfo> facets = new ArrayList<>();
        if (useCustomTagList && customTagList != null) {
            facets = formsDocumentsSearch.loadFacetsFromList(request, customTagList);
        } else {
            Map<String, List<FacetsInfo>> facetsAndTags = formsDocumentsSearch.loadFacets(request, basePath);
            facets = facetsAndTags.getOrDefault("forms_documents", facets);
        }

        facets.forEach(facetsInfo -> {
            if (getTags(request).contains(facetsInfo.getFacetsTagId())) {
                facetsInfo.setChecked(true);
            }
        });
        return facets;
    }

    public static String getRequestParam(final SlingHttpServletRequest request, final String paramName) {
        String paramValue = StringUtils.defaultIfBlank(request.getParameter(paramName), StringUtils.EMPTY);
        return StringUtils.trim(paramValue);
    }

    public static List<String> getTags(final SlingHttpServletRequest request) {
        String[] tagsParam = request.getParameterValues("tags");
        List<String> tags = new ArrayList<>();
        if (tagsParam != null) {
            Collections.addAll(tags, tagsParam);
        }
        return tags;
    }

    public static List<String> getSelectedTagsOrAllTags(final SlingHttpServletRequest request, FormsDocumentsSearch formsDocumentsSearch) {
        boolean isFullResults = StringUtils.startsWith(getRequestParam(request, "sort"), "category") || StringUtils.equalsIgnoreCase(getRequestParam(request, "all"), "true");
        return isFullResults ? getAllFacetIDs(request, formsDocumentsSearch) : getTags(request);
    }

    public static List<FacetsInfo> getAllFacets(SlingHttpServletRequest request, FormsDocumentsSearch formsDocumentsSearch) {
        String a = getBaseSiteName(request);
        boolean useCustomTagList = request.getResource().getValueMap().get("useCustomTagList", false);
        String[] customTagList = request.getResource().getValueMap().get("tagList", String[].class);
        return retrieveFacets(request, formsDocumentsSearch, useCustomTagList, customTagList, a);
    }

    private static String getBaseSiteName(SlingHttpServletRequest request) {
        return StringUtils.substringBetween(request.getResource().getPath(), "/content/", "/");
    }

    private static List<String> getAllFacetIDs(SlingHttpServletRequest request, FormsDocumentsSearch formsDocumentsSearch) {
        return CollectionUtils.emptyIfNull(getAllFacets(request, formsDocumentsSearch)).stream().map(FacetsInfo::getFacetsTagId).collect(Collectors.toList());
    }

    public static List<String> getSelectedFacets(final SlingHttpServletRequest request) {
        ResourceResolver resourceResolver = request.getResourceResolver();
        TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
        return CollectionUtils.emptyIfNull(getTags(request)).stream()
                .map(tagId -> tagManager.resolve(tagId).getTitle()).collect(Collectors.toList());
    }

    public static void getSearchResultsFromQueryResult(Map<String, GSSearchResult> searchResults, QueryResult result) {
        if (result != null) {
            try {
                RowIterator rowIterator = result.getRows();
                while (rowIterator.hasNext()) {
                    GSSearchResult gsSearchResult = new GSSearchResult(rowIterator.nextRow());
                    updateSearchResults(searchResults, gsSearchResult);
                }
            } catch (RepositoryException e) {
                LOGGER.error("Exception retrieving result :: ", e);
            }
        }
    }

    public static void getSearchResultsFromQueryResult(Map<String, GSSearchResult> searchResults, QueryResult result, SlingHttpServletRequest request, FormsDocumentsSearch formsDocumentsSearch) {
        getSearchResultsFromQueryResult(searchResults, result);
        searchResults.forEach((key, value) -> {
            try {
                updateCategory(value, getSelectedTagsOrAllTags(request, formsDocumentsSearch), request.getResourceResolver());
            } catch (RepositoryException e) {
                LOGGER.error("Exception updating category to result :: ", e);
            }
        });
    }

    private static void updateSearchResults(Map<String, GSSearchResult> searchResults, GSSearchResult result) {
        if (result != null) {
            if (searchResults.containsKey(result.getPath())) {
                GSSearchResult existingResult = searchResults.get(result.getPath());
                if (result.getScore().compareTo(existingResult.getScore()) > 0) {
                    searchResults.replace(result.getPath(), result);
                }
            } else {
                searchResults.put(result.getPath(), result);
            }
        }
    }

    public static void filter(Map<String, GSSearchResult> searchResults) {
        Set<String> keys = new HashSet<>(searchResults.keySet());
        for (String key : keys) {
            try {
                GSSearchResult result = searchResults.get(key);
                Node resultNode = result.getResultNode();
                String primaryType = resultNode.getPrimaryNodeType().getName();
                if (primaryType.equals(GSSearchResultConstants.NODE_TYPE_CQ_PAGE)
                        && resultNode.hasNode(JcrConstants.JCR_CONTENT)) {
                    Node jcrContentNode = resultNode.getNode(JcrConstants.JCR_CONTENT);
                    if (!jcrContentNode.hasNodes() || isFilterByResourcePath(jcrContentNode)
                            || isFilterByResourceType(jcrContentNode) || isFilterByProperty(jcrContentNode)
                            || (jcrContentNode.hasProperty("hideInSearch")
                            && jcrContentNode.getProperty("hideInSearch").getBoolean())) {
                        searchResults.remove(key);
                    }
                }
            } catch (RepositoryException e) {
                LOGGER.error("Exception getting property:: ", e);
            }
        }
    }

    public static List<GSSearchResult> getResultsSortedBy(Map<String, GSSearchResult> searchResults, String orderBy) {
        List<GSSearchResult> results = new ArrayList<>(searchResults.values());
        results.sort(new GSSearchResultComparator(orderBy));
        Collections.reverse(results);
        return results;
    }

    public static List<GSSearchResult> getResultsSortedBy(Map<String, GSSearchResult> searchResults, String orderBy, boolean ascending) {
        List<GSSearchResult> results = new ArrayList<>(searchResults.values());
        results.sort(new GSSearchResultComparator(orderBy));
        if (ascending) {
            Collections.reverse(results);
        }
        return results;
    }

    private static boolean isFilterByResourceType(Node jcrContentNode) {
        try {
            if (jcrContentNode.hasProperty("sling:resourceType")) {
                String resourceType = jcrContentNode.getProperty("sling:resourceType").getString();
                for (String rtFilter : resourceTypeFilters) {
                    if (resourceType.equals(rtFilter)) {
                        return true;
                    }
                }
            }
        } catch (RepositoryException e) {
            LOGGER.error("Exception getting resourceType:: ", e);
        }
        return false;
    }

    private static boolean isFilterByResourcePath(Node jcrContentNode) {
        try {
            String path = jcrContentNode.getPath();
            for (String rpFilter : resourcePathFilters) {
                if (path.contains(rpFilter)) {
                    return true;
                }
            }
        } catch (RepositoryException e) {
            LOGGER.error("Exception filtering resource by path:: ", e);
        }
        return false;
    }

    private static boolean isFilterByProperty(Node jcrContentNode) {
        try {
            if (jcrContentNode.hasNode("data")) {
                Node dataNode = jcrContentNode.getNode("data");
                if (dataNode.hasProperty("visibleDate")) {
                    return isAfterDateFilter(dataNode, "visibleDate");
                }
                if (dataNode.hasProperty("end")) {
                    return isAfterDateFilter(dataNode, "end");
                }
            }
        } catch (RepositoryException e) {
            LOGGER.error("Exception :: ", e);
        }
        return false;
    }

    private static boolean isAfterDateFilter(Node dataNode, final String field) {
        GSDateTime today = new GSDateTime();
        GSDateTimeFormatter dtfIn = GSDateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        try {
            String visibleDate = dataNode.getProperty(field).getString();
            GSDateTime vis = GSDateTime.parse(visibleDate, dtfIn);
            if (vis.isAfter(today)) {
                return true;
            }
        } catch (RepositoryException e) {
            LOGGER.error("Exception filtering by property:: ", e);
        }
        return false;
    }

    private static void updateCategory(GSSearchResult gsSearchResult, List<String> tagIds, ResourceResolver resourceResolver) throws RepositoryException {
        Node node = gsSearchResult.getResultNode();
        Optional<Resource> resource = Optional.ofNullable(resourceResolver).map(resolver -> {
            try {
                return resolver.getResource(node.getPath());
            } catch (RepositoryException e) {
                LOGGER.error("Error converting node to resource", e);
            }
            return null;
        }).map(res -> res.getChild(JcrConstants.JCR_CONTENT));
        String[] tags = new String[0];
        if (resource.isPresent()) {
            Optional<ValueMap> valueMap = resource.map(Resource::getValueMap);
            if (isAsset(node)) {
                valueMap = resource.map(jcrRes -> jcrRes.getChild("metadata")).map(Resource::getValueMap);
            }
            tags = valueMap.map(vm -> vm.get("cq:tags", String[].class)).orElse(tags);
        }
        List<String> pageTags = new ArrayList<>(Arrays.asList(tags));
        for (String tagId : tagIds) {
            if ((pageTags.contains(tagId))) {
                gsSearchResult.setCategory(getTagTitle(resourceResolver, tagId));
                break;
            }
        }
    }

    private static String getTagTitle(ResourceResolver resourceResolver, String tagId) {
        return Optional.ofNullable(resourceResolver).map(resolver -> resolver.adaptTo(TagManager.class))
                .map(tm -> tm.resolve(tagId))
                .map(Tag::getTitle).orElse(tagId);
    }

    public static boolean isPageOrAsset(Node n) throws RepositoryException {
        return (isPage(n)) || (isAsset(n));
    }

    public static boolean isPage(Node n) throws RepositoryException {
        return (n.isNodeType(GSSearchResultConstants.NODE_TYPE_CQ_PAGE)) || (n.isNodeType(GSSearchResultConstants.NODE_TYPE_CQ_PSEUDO_PAGE));
    }

    public static boolean isAsset(Node n) throws RepositoryException {
        return n.isNodeType(DamConstants.NT_DAM_ASSET);
    }

}
