package com.subra.aem.rjs.v.search.models;

import com.day.cq.dam.api.DamConstants;
import com.day.cq.wcm.api.Page;
import com.subra.aem.rjs.v.search.GSSearchManagerUtils;
import com.subra.aem.rjs.v.search.GSSearchResult;
import com.subra.aem.rjs.v.search.Paging;
import com.subra.aem.rjs.v.search.services.GSJcrSearchService;
import com.subra.aem.rjs.v.search.utils.GSSearchResultComparator;
import com.subra.aem.rjs.v.search.utils.GSSearchResultConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class GSSearchResultModel {

    public static final int PAGES_SIZE = 10;
    private static final Logger LOGGER = LoggerFactory.getLogger(GSSearchResultModel.class);

    @Self
    private SlingHttpServletRequest request;

    @ScriptVariable
    private Page currentPage;

    @ValueMapValue
    private String searchIn;

    @ValueMapValue
    private String documentSearchPath;

    @ValueMapValue
    private String noResultsText;

    @OSGiService
    private GSJcrSearchService jcrSearchService;

    private Map<String, GSSearchResult> searchResults;

    @PostConstruct
    protected void init() {
        searchResults = new LinkedHashMap<>();
        updateSearchPaths();
        String queryString;
        try {
            queryString = getQueryString();
        } catch (UnsupportedEncodingException e) {
            queryString = getSearchString();
        }
        //add(jcrSearchService.search(request, searchIn, GSSearchResultConstants.NODE_TYPE_CQ_PAGE, queryString))
        GSSearchManagerUtils.getSearchResultsFromQueryResult(searchResults,
                jcrSearchService.search(request, searchIn, GSSearchResultConstants.NODE_TYPE_CQ_PAGE, queryString));
        //add(jcrSearchService.search(request, documentSearchPath, DamConstants.NT_DAM_ASSET, queryString))
        GSSearchManagerUtils.getSearchResultsFromQueryResult(searchResults,
                jcrSearchService.search(request, documentSearchPath, DamConstants.NT_DAM_ASSET, queryString));
        GSSearchManagerUtils.filter(searchResults);
    }

    public int size() {
        return getSearchResults().size();
    }

    public List<GSSearchResult> getSearchResults() {
        return searchResults.values().stream().limit(((long)999) * PAGES_SIZE).collect(Collectors.toList());
    }

    public List<GSSearchResult> getResults() {
        return new ArrayList<>(getSearchResults().subList(getStartIndex(), getEndIndex()));
    }

    public List<GSSearchResult> getResultsSortedByScore() {
        return getSearchResults().stream().sorted(new GSSearchResultComparator()).sorted(Collections.reverseOrder()).collect(Collectors.toList());
    }

    public int getCurrentPageNumber() {
        return getStartIndex() / PAGES_SIZE;
    }

    public double getTotalPages() {
        return Math.ceil((double) size() / PAGES_SIZE);
    }

    public Paging getPagination() {
        Paging paging = createPaging();
        paging.setList(buildList(paging.getCurrentPageNumber(), paging.getTotalPages(), 2, 2));
        return paging;
    }

    public Paging getPaginationForMobile() {
        Paging pagingForMobile = createPaging();
        pagingForMobile.setList(buildList(pagingForMobile.getCurrentPageNumber(), pagingForMobile.getTotalPages(), 2, 1));
        return pagingForMobile;
    }

    private Paging createPaging() {
        final int currentPageNumber = getCurrentPageNumber() + 1;
        final int totalRecordsSize = size();
        int totalPages = Math.max(totalRecordsSize / PAGES_SIZE + (totalRecordsSize % PAGES_SIZE == 0 ? 0 : 1), 1);
        Paging paging = new Paging();
        paging.setCurrentPageNumber(currentPageNumber);
        paging.setPageSize(PAGES_SIZE);
        paging.setTotalPages(totalPages);
        paging.setTotalRecordsSize(totalRecordsSize);
        paging.setPrevPageOffset(currentPageNumber > 1 ? (currentPageNumber - 2) * PAGES_SIZE : 0);
        paging.setNextPageOffset(currentPageNumber * PAGES_SIZE);
        paging.setFirstPage(currentPageNumber == 1);
        paging.setLastPage(currentPageNumber == totalPages || totalRecordsSize == 0);
        return paging;
    }

    private List<Paging.PageItem> buildList(final int currentPageNumber, final int totalPages, final int blocksBeforeEllipse, final int blocksAfterEllipse) {
        List<Paging.PageItem> pagingList = new LinkedList<>();
        int totalBlocks = blocksBeforeEllipse + 1 + blocksAfterEllipse;
        if (totalPages <= totalBlocks) {
            addPageItems(currentPageNumber, pagingList, 1, totalPages);
        } else {
            if (currentPageNumber < (totalPages - blocksAfterEllipse + 1)) {
                int endIndex = Math.max(currentPageNumber, blocksBeforeEllipse);
                addPageItems(currentPageNumber, pagingList, endIndex - blocksBeforeEllipse + 1, endIndex);
            } else {
                addPageItems(currentPageNumber, pagingList, totalPages - blocksAfterEllipse + 1 - blocksBeforeEllipse, totalPages - blocksAfterEllipse);
            }
            addPageItem(pagingList, 0, "...", currentPageNumber);
            addPageItems(currentPageNumber, pagingList, totalPages - blocksAfterEllipse + 1, totalPages);
        }
        return pagingList;
    }

    private void addPageItems(int currentPageNumber, List<Paging.PageItem> pagingList, int startIndex, int endIndex) {
        for (int i = startIndex; i <= endIndex; i++) {
            addPageItem(pagingList, i, null, currentPageNumber);
        }
    }


    private void addPageItem(final List<Paging.PageItem> list, final int pageNumber, final String ellipses, final int currerntPageNumber) {
        Paging.PageItem pageItem = new Paging.PageItem();
        final boolean isNotEllipse = StringUtils.isBlank(ellipses);
        pageItem.setDisplayNumber(isNotEllipse ? String.valueOf(pageNumber) : ellipses);
        pageItem.setOffset(isNotEllipse ? (pageNumber - 1) * PAGES_SIZE : 0);
        pageItem.setCurrentPage(currerntPageNumber == pageNumber);
        pageItem.setEllipse(!isNotEllipse);
        list.add(pageItem);
    }

    private int getStartIndex() {
        String start = StringUtils.defaultIfBlank(request.getParameter("start"), "0");
        int startIdx = 0;
        try {
            if ((startIdx = Integer.parseInt(start)) < 0) {
                startIdx = 0;
            }
        } catch (NumberFormatException e) {
            LOGGER.error("NumberFormatException parsing start", e);
        }
        return startIdx;
    }

    private int getEndIndex() {
        return Math.min(getStartIndex() + PAGES_SIZE, size());
    }

    private void updateSearchPaths() {
        if (StringUtils.isBlank(documentSearchPath)) {
            Pattern pattern = Pattern.compile("/(content)/([^/]*)/(en)$", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(currentPage.getAbsoluteParent(2).getPath());
            if (matcher.find()) {
                documentSearchPath = Arrays.asList("gssjc", "gateway", "gssem").contains(matcher.group(2)) ?
                       GSSearchResultConstants.SLASH + matcher.group(1) + "/dam/" + matcher.group(2) + "/documents" :
                        GSSearchResultConstants.SLASH + matcher.group(1) + "/dam/girlscouts-" + matcher.group(2) + "/documents";
            }
        }
        if (StringUtils.isBlank(searchIn)) {
            searchIn = currentPage.getAbsoluteParent(2).getPath();
        }
    }

    public String getSearchString() {
        String query = StringUtils.defaultIfBlank(StringUtils.trim(request.getParameter("q")), "[[empty search criteria]]");
        if (query.length() <= 2) {
            query = "[[too short search criteria]]";
        }
        return query;
    }

    private String getQueryString() throws UnsupportedEncodingException {
        return URLEncoder.encode(getSearchString().replaceAll("[^a-zA-Z0-9'.,]", " ").replaceAll("\\s+", " "), "UTF-8");
    }

}
