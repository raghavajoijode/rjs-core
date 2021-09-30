package com.subra.aem.rjs.v.search.forms;

import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchResultsInfo {

    private List<String> results;
    private long hitCounts;
    //private Map<String, Map<String, Long>> facetsWithCount;
    private Map<String, ArrayList<String>> facts;
    private SearchResult searchResults;
    private List<Hit> hits;
    private List<String> regions;

    public SearchResultsInfo() {
        results = new ArrayList<>();
        //facetsWithCount = new HashMap<>();
        facts = new HashMap<>();
    }

    public List<String> getResults() {
        return results;
    }

    public void setResults(List<String> results) {
        this.results = results;
    }

    public long getHitCounts() {
        return hitCounts;
    }

    public void setHitCounts(long hitCounts) {
        this.hitCounts = hitCounts;
    }

    /*public Map<String, Map<String, Long>> getFacetsWithCount() {
        return facetsWithCount;
    }

    public void setFacetsWithCount(Map<String, Map<String, Long>> facetsWithCount) {
        this.facetsWithCount = facetsWithCount;
    }*/

    public Map<String, ArrayList<String>> getFacts() {
        return facts;
    }

    public void setFacts(Map<String, ArrayList<String>> facts) {
        this.facts = facts;
    }

    public SearchResult getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(SearchResult searchResults) {
        this.searchResults = searchResults;
    }

    public List<Hit> getHits() {
        return hits;
    }

    public void setHits(List<Hit> hits) {
        this.hits = hits;
    }

    public List<String> getRegions() {
        return regions;
    }

    public void setRegions(List<String> regions) {
        this.regions = regions;
    }
}
