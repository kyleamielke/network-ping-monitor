package io.thatworked.support.search.api.dto.response;

import java.util.List;

/**
 * Response DTO for search operations.
 */
public class SearchResponse {
    
    private List<SearchItemResponse> items;
    private String query;
    private long searchTimeMs;
    private int totalResults;
    
    public SearchResponse() {}
    
    public SearchResponse(List<SearchItemResponse> items, String query, long searchTimeMs) {
        this.items = items;
        this.query = query;
        this.searchTimeMs = searchTimeMs;
        this.totalResults = items != null ? items.size() : 0;
    }
    
    // Getters and Setters
    public List<SearchItemResponse> getItems() {
        return items;
    }
    
    public void setItems(List<SearchItemResponse> items) {
        this.items = items;
        this.totalResults = items != null ? items.size() : 0;
    }
    
    public String getQuery() {
        return query;
    }
    
    public void setQuery(String query) {
        this.query = query;
    }
    
    public long getSearchTimeMs() {
        return searchTimeMs;
    }
    
    public void setSearchTimeMs(long searchTimeMs) {
        this.searchTimeMs = searchTimeMs;
    }
    
    public int getTotalResults() {
        return totalResults;
    }
    
    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }
}