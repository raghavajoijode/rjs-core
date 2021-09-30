package com.subra.aem.rjs.v.search.forms.servlets;


import com.subra.aem.rjs.v.search.GSSearchManagerUtils;
import com.subra.aem.rjs.v.search.GSSearchResult;
import com.subra.aem.rjs.v.search.forms.FacetsInfo;
import com.subra.aem.rjs.v.search.forms.services.FormsDocumentsSearch;
import com.subra.aem.rjs.v.search.services.GSJcrSearchService;
import com.subra.aem.rjs.v.search.utils.GSSearchResultConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * property = {
 * Constants.SERVICE_DESCRIPTION + "= RJ CQ Redirect Servlet",
 * "sling.servlet.extensions=" + "json",
 * "sling.servlet.selectors=" + "more",
 * "sling.servlet.resourceTypes=" + "girlscouts/components/forms-documents-search",
 * }
 */
@Component(service = Servlet.class)
@SlingServletResourceTypes(
        selectors = "more",
        extensions = "json",
        resourceTypes = "rjs/core/components/content/search"
)
@ServiceDescription("GSUSA Forms & Documents Search Results Servlet")
public class FormsDocumentsSearchServlet extends SlingSafeMethodsServlet {

    public static final String NAME_A_Z = "name-a-z";
    public static final String CATEGORY = "category";
    private static final Logger LOGGER = LoggerFactory.getLogger(FormsDocumentsSearchServlet.class);
    private static final String PAGES_EXPRESSION = "SELECT [jcr:score], [jcr:path], [jcr:primaryType] FROM [cq:Page] as s WHERE ISDESCENDANTNODE([%s])";
    private static final String ASSETS_EXPRESSION = "SELECT [jcr:score], [jcr:path], [jcr:primaryType] FROM [dam:Asset] as s WHERE ISDESCENDANTNODE([%s])";
    // private static final String SHARED_ASSETS_EXPRESSION = 	"SELECT [jcr:score], [jcr:path], [jcr:primaryType] FROM [dam:Asset] as s WHERE ISDESCENDANTNODE([/content/dam/girlscouts-shared/documents])"
    @Reference
    private transient GSJcrSearchService jcrSearchService;

    @Reference
    private transient FormsDocumentsSearch formsDocumentsSearch;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {

        Map<String, GSSearchResult> searchResults = new HashMap<>();
        int resultCount = 0;
        int offset = getOffset(request);
        boolean isCategoryBasedResult = isCategoryBasedResult(request);

        ValueMap properties = request.getResource().getValueMap();
        List<GSSearchResult> queryResults = getQueryResults(request, searchResults, properties, isCategoryBasedResult);
        List<JSONObject> resultSet = new ArrayList<>();
        long resultsPerPage = getLimit(request, queryResults.size());
        if (isCategoryBasedResult) {
            createCategoryBasedResultSet(request, queryResults, resultSet);
        } else if (offset <= queryResults.size()) {
            for (int i = offset; i < queryResults.size(); i++) {
                GSSearchResult qResult = queryResults.get(i);
                offset++;
                resultSet.add(createResult(qResult));
                resultCount++;
                if (resultCount == resultsPerPage) {
                    break;
                }
            }
        }
        updateResponse(request, response, resultSet, resultCount, offset, queryResults.size());

    }

    private boolean isCategoryBasedResult(SlingHttpServletRequest request) {
        return StringUtils.equalsIgnoreCase(getSortByKey(request), CATEGORY) || Boolean.parseBoolean(GSSearchManagerUtils.getRequestParam(request, "all"));
    }

    private List<GSSearchResult> getQueryResults(SlingHttpServletRequest request, Map<String, GSSearchResult> searchResults, ValueMap properties, boolean isCategoryBasedResult) {
        final String query = GSSearchManagerUtils.getQueryParam(request);
        final List<String> tags = GSSearchManagerUtils.getTags(request);
        final String pagePath = properties.get("form-document-path", getCurrentPagePath(request));
        String damPath = properties.get("srchLocation", "");
        damPath = getDamPath(request, damPath);
        final String startDate = GSSearchManagerUtils.getRequestParam(request, "startDate");
        final String endDate = GSSearchManagerUtils.getRequestParam(request, "endDate");
        final String formType = GSSearchManagerUtils.getRequestParam(request, "formType");
        if (StringUtils.isAllBlank(query) || (!tags.isEmpty())) {
            String pagesQuery = String.format(PAGES_EXPRESSION, pagePath);
            String assetsQuery = String.format(ASSETS_EXPRESSION, damPath);
            //String sharedAssetsQuery = String.format(SHARED_ASSETS_EXPRESSION, damPath, query)
            //sharedAssetsQuery = addTagsClause(sharedAssetsQuery, tags, false)
            //sharedAssetsQuery = addContainsClause(sharedAssetsQuery, query)
            if (!StringUtils.equalsAnyIgnoreCase(formType, "pdf", "doc") && StringUtils.isNotBlank(pagePath)) {
                pagesQuery = addTagsFilter(pagesQuery, tags, true, request);
                pagesQuery = addQueryFilter(pagesQuery, query);
                pagesQuery = addStartDateFilter(pagesQuery, startDate);
                pagesQuery = addEndDateFilter(pagesQuery, endDate);
                GSSearchManagerUtils.getSearchResultsFromQueryResult(searchResults, jcrSearchService.search(request, pagesQuery), request, formsDocumentsSearch);
            }
            if (!StringUtils.equalsAnyIgnoreCase(formType, "online") && StringUtils.isNotBlank(damPath)) {
                assetsQuery = addTagsFilter(assetsQuery, tags, false, request);
                assetsQuery = addQueryFilter(assetsQuery, query);
                assetsQuery = addStartDateFilter(assetsQuery, startDate);
                assetsQuery = addEndDateFilter(assetsQuery, endDate);
                assetsQuery = addTypeFilter(assetsQuery, formType);
                GSSearchManagerUtils.getSearchResultsFromQueryResult(searchResults, jcrSearchService.search(request, assetsQuery), request, formsDocumentsSearch);
            }
            //gsResultManager.add(searchProvider.search(sharedAssetsQuery))
            GSSearchManagerUtils.filter(searchResults);
        }

        //GSWP-1049- Sort by title if searchtext is empty and categories are enabled -> terenary operator to set soryBy option
        //return GSSearchManagerUtils.getResultsSortedBy(searchResults, query.isEmpty() && (!tags.isEmpty()) ? "title" : "score")
        return isCategoryBasedResult ? new ArrayList<>(searchResults.values()) : GSSearchManagerUtils.getResultsSortedBy(searchResults, getSortByKey(request), isSortAscending(request));
    }

    private JSONObject createResult(GSSearchResult qResult) {
        JSONObject result = new JSONObject();
        try {
            Node resultNode = qResult.getResultNode();
            String path = resultNode.getPath();
            result.put("title", qResult.getTitle());
            result.put("score", qResult.getScore());
            String url = path;
            if ("cq:Page".equals(resultNode.getPrimaryNodeType().getName())) {
                url += ".html";
            }
            result.put("url", url);
            result.put("type", resultNode.getPrimaryNodeType());
            int idx = path.lastIndexOf('.');
            String extension = idx >= 0 ? path.substring(idx + 1) : "";
            result.put("extension", extension);
            result.put("excerpt", qResult.getExcerpt());
            result.put("description", qResult.getDescription());
        } catch (JSONException | RepositoryException e) {
            LOGGER.error("Exception updating result:", e);
        }
        return result;
    }

    private void createCategoryBasedResultSet(SlingHttpServletRequest request, List<GSSearchResult> queryResults, List<JSONObject> formsAndDocs) {
        List<String> selectedFacets = GSSearchManagerUtils.getSelectedFacets(request);
        // Getting selected facets.. Or All Facets
        List<String> facets = selectedFacets.isEmpty() ? getAllFacetTitles(request) : selectedFacets;

        // Sorting Facets
        facets = facets.stream().sorted().collect(Collectors.toList());
        if (!isSortAscending(request)) {
            Collections.reverse(facets);
        }

        // Creating category based result
        facets.forEach(facetTitle -> {
            List<JSONObject> sortedFilterResults = queryResults.stream().filter(res -> StringUtils.equalsAnyIgnoreCase(facetTitle, res.getCategory()))
                    .sorted(Comparator.comparing(GSSearchResult::getTitle)).map(this::createResult).collect(Collectors.toList());
            if (!sortedFilterResults.isEmpty()) {
                JSONObject result = new JSONObject();
                try {
                    result.put(CATEGORY, facetTitle);
                    result.put("items", sortedFilterResults);
                } catch (JSONException e) {
                    LOGGER.error("Error updating category base result for tag {}", facetTitle);
                }
                formsAndDocs.add(result);
            }
        });
    }

    private void updateResponse(SlingHttpServletRequest request, SlingHttpServletResponse response, List<JSONObject> formsAndDocs, int resultCount, int newOffset, int totalResults) throws IOException {
        JSONObject responseObject = new JSONObject();
        try {
            responseObject.put("results", formsAndDocs);
            responseObject.put("newOffset", newOffset);
            responseObject.put("resultCount", resultCount);
            responseObject.put("totalResults", totalResults);
            responseObject.put("limit", getLimit(request, totalResults));
            responseObject.put("status", 200);
            responseObject.write(response.getWriter());
            response.setStatus(200);
        } catch (JSONException e) {
            response.setStatus(500);
            response.getWriter().print("{\"status\" : 500, \"message\": \"Exception Occurred\"}");
        }
    }

    private String getDamPath(SlingHttpServletRequest request, String damPath) {
        if ("".equals(damPath)) {
            HashMap<String, String> specialCouncils = new HashMap<>();
            specialCouncils.put("gateway", "gateway");
            specialCouncils.put("girlscoutcsa", "southern-appalachian");
            specialCouncils.put("girlscouts-future", "girlscouts-future");
            specialCouncils.put("girlscoutsaz", "girlscoutsaz");
            specialCouncils.put("girlscoutsnccp", "nc-coastal-pines-images-");
            specialCouncils.put("girlscoutsnv", "gssnv");
            specialCouncils.put("girlscoutsoc", "girlscoutsoc");
            specialCouncils.put("girlscoutsofcolorado", "girlscoutsofcolorado");
            specialCouncils.put("girlscoutsosw", "oregon-sw-washington-");
            specialCouncils.put("girlscoutstoday", "girlscoutstoday");
            specialCouncils.put("gsbadgerland", "gsbadgerland");
            specialCouncils.put("gscsnj", "gscsnj");
            specialCouncils.put("gskentuckiana", "gskentuckiana");
            specialCouncils.put("gsneo", "gsneo");
            specialCouncils.put("gsnetx", "NE_Texas");
            specialCouncils.put("gssem", "gssem");
            specialCouncils.put("gssjc", "gssjc");
            specialCouncils.put("gssn", "gssn");
            specialCouncils.put("gswcf", "wcf-images");
            specialCouncils.put("gswestok", "gswestok");
            specialCouncils.put("gswo", "gswo");
            specialCouncils.put("kansasgirlscouts", "kansasgirlscouts");
            specialCouncils.put("usagso", "usagso");
            specialCouncils.put("girlscouts-dxp", "dxp");
            damPath = "/content/dam/";
            String[] pageTree = StringUtils.split(request.getRequestPathInfo().getResourcePath(), "/");
            String councilName = pageTree[2];
            if (specialCouncils.containsKey(councilName)) {
                damPath = damPath + specialCouncils.get(councilName) + "/documents";
            } else {
                damPath = damPath + "girlscouts-" + councilName + "/documents";
            }
        }
        return damPath;
    }

    private int getOffset(SlingHttpServletRequest request) {
        try {
            return Integer.parseInt(StringUtils.defaultIfBlank(request.getParameter("offset"), "0"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private long getLimit(SlingHttpServletRequest request, long maxSize) {
        final String limit = StringUtils.defaultIfBlank(request.getParameter("limit"), "10");
        try {
            return StringUtils.equalsIgnoreCase(limit, "all") ? maxSize : Integer.parseInt(limit);
        } catch (NumberFormatException e) {
            return 10;
        }
    }

    private String getSortParam(SlingHttpServletRequest request) {
        try {
            return StringUtils.defaultIfBlank(request.getParameter("sort"), NAME_A_Z);
        } catch (NumberFormatException e) {
            return NAME_A_Z;
        }
    }

    private String getSortByKey(SlingHttpServletRequest request) {
        final String sortByParam = getSortParam(request);
        switch (sortByParam) {
            case "date":
            case "type":
                return sortByParam;
            case "category-a-z":
            case "category-z-a":
                return CATEGORY;
            case NAME_A_Z:
            case "name-z-a":
                return "title";
            default:
                return "score";
        }
    }

    private boolean isSortAscending(SlingHttpServletRequest request) {
        final String sortByParam = getSortParam(request);
        switch (sortByParam) {
            case "name-z-a":
            case "category-z-a":
                return false;
            case NAME_A_Z:
            case "category-a-z":
            case "date":
            case "type":
            default:
                return true;
        }
    }

    private String addTagsFilter(String query, List<String> tags, boolean isPage, SlingHttpServletRequest request) {
        StringBuilder sb = new StringBuilder(query);
        tags = tags.isEmpty() && isCategoryBasedResult(request) ? getAllFacetIDs(request) : tags;
        if (!tags.isEmpty()) {
            sb.append(" AND (");
            for (int i = 0; i < tags.size(); i++) {
                String tag = tags.get(i);
                if (i > 0) {
                    sb.append(" OR");
                }
                sb.append(" s.[jcr:content");
                if (!isPage) {
                    sb.append("/metadata");
                }
                sb.append("/cq:tags]='").append(tag).append("'");
            }
            sb.append(")");
        }
        return sb.toString();
    }

    private String addQueryFilter(String query, String searchText) {
        if (StringUtils.isNotBlank(searchText)) {
            query += " AND CONTAINS(s.*, '" + searchText + "')";
        }
        return query;
    }

    private String addStartDateFilter(String query, String startDate) {
        if (StringUtils.isNotBlank(startDate)) {
            query += " AND (s.[jcr:created] >= CAST('" + getFormattedDate(startDate, false) + "' AS DATE))";
        }
        return query;
    }

    private String addEndDateFilter(String query, String endDate) {
        if (StringUtils.isNotBlank(endDate)) {
            query += " AND (s.[jcr:created] < CAST('" + getFormattedDate(endDate, true) + "' AS DATE))";
        }
        return query;
    }

    private String addTypeFilter(String query, String type) {
        final List<String> mimeTypes = getFormat(type);
        StringBuilder sb = new StringBuilder(query);
        if (!mimeTypes.isEmpty()) {
            sb.append(" AND (");
            for (int i = 0; i < mimeTypes.size(); i++) {
                String mimeType = mimeTypes.get(i);
                if (i > 0) {
                    sb.append(" OR ");
                }
                sb.append("s.[jcr:content/metadata/dc:format]='").append(mimeType).append("'");
            }
            sb.append(")");
        }
        return sb.toString();
    }

    private List<String> getFormat(String type) {
        List<String> mimeTypes = new ArrayList<>();
        if (StringUtils.equalsAnyIgnoreCase(type, "pdf")) {
            mimeTypes.add(GSSearchResultConstants.PDF_FILE);
        } else if (StringUtils.equalsAnyIgnoreCase(type, "doc")) {
            mimeTypes.add(GSSearchResultConstants.APPL_VND_FILE);
            mimeTypes.add(GSSearchResultConstants.MSWORD_FILE);
        }
        return mimeTypes;
    }

    private String getFormattedDate(final String date, boolean getNextDate) {
        LocalDate inputDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate updatedDate = getNextDate ? inputDate.plusDays(1) : inputDate;
        return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(updatedDate.atStartOfDay()); // 2016-01-07T23:59:59.999Z
    }

    private List<String> getAllFacetTitles(SlingHttpServletRequest request) {
        return CollectionUtils.emptyIfNull(GSSearchManagerUtils.getAllFacets(request, formsDocumentsSearch)).stream().map(FacetsInfo::getFacetsTitle).collect(Collectors.toList());
    }

    private List<String> getAllFacetIDs(SlingHttpServletRequest request) {
        return CollectionUtils.emptyIfNull(GSSearchManagerUtils.getAllFacets(request, formsDocumentsSearch)).stream().map(FacetsInfo::getFacetsTagId).collect(Collectors.toList());
    }

    private String getCurrentPagePath(SlingHttpServletRequest request) {
        return StringUtils.substringBefore(request.getResource().getPath(), "/jcr:content");
    }

}