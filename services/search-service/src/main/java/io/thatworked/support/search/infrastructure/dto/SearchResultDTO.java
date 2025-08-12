package io.thatworked.support.search.infrastructure.dto;

import java.util.List;

public class SearchResultDTO {
    private List<SearchItemDTO> results;
    private int totalResults;
    private String query;
    private long searchTimeMs;

    public SearchResultDTO() {}

    public SearchResultDTO(List<SearchItemDTO> results, String query, long searchTimeMs) {
        this.results = results;
        this.totalResults = results.size();
        this.query = query;
        this.searchTimeMs = searchTimeMs;
    }

    // Getters and Setters
    public List<SearchItemDTO> getResults() {
        return results;
    }

    public void setResults(List<SearchItemDTO> results) {
        this.results = results;
        this.totalResults = results != null ? results.size() : 0;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
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
}