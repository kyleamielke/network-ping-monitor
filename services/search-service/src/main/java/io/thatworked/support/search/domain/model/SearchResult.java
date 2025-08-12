package io.thatworked.support.search.domain.model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Domain model representing search results.
 * Immutable value object containing search items and metadata.
 */
public class SearchResult {
    
    private final List<SearchItem> items;
    private final String query;
    private final long searchTimeMs;
    private final int totalResults;
    
    private SearchResult(List<SearchItem> items, String query, long searchTimeMs) {
        this.items = Collections.unmodifiableList(items);
        this.query = query;
        this.searchTimeMs = searchTimeMs;
        this.totalResults = items.size();
    }
    
    /**
     * Creates a new search result.
     */
    public static SearchResult create(List<SearchItem> items, String query, long searchTimeMs) {
        Objects.requireNonNull(items, "Search items cannot be null");
        Objects.requireNonNull(query, "Query cannot be null");
        
        if (searchTimeMs < 0) {
            throw new IllegalArgumentException("Search time cannot be negative");
        }
        
        return new SearchResult(items, query, searchTimeMs);
    }
    
    /**
     * Creates an empty search result.
     */
    public static SearchResult empty(String query, long searchTimeMs) {
        return create(Collections.emptyList(), query, searchTimeMs);
    }
    
    /**
     * Gets the search items.
     */
    public List<SearchItem> getItems() {
        return items;
    }
    
    /**
     * Gets the original query.
     */
    public String getQuery() {
        return query;
    }
    
    /**
     * Gets the search time in milliseconds.
     */
    public long getSearchTimeMs() {
        return searchTimeMs;
    }
    
    /**
     * Gets the total number of results.
     */
    public int getTotalResults() {
        return totalResults;
    }
    
    /**
     * Checks if the result is empty.
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }
    
    /**
     * Merges this result with another, combining items.
     * The search time will be the maximum of both results.
     */
    public SearchResult merge(SearchResult other) {
        if (other == null || other.isEmpty()) {
            return this;
        }
        
        List<SearchItem> mergedItems = new java.util.ArrayList<>(this.items);
        mergedItems.addAll(other.items);
        
        return new SearchResult(
            mergedItems,
            this.query,
            Math.max(this.searchTimeMs, other.searchTimeMs)
        );
    }
    
    /**
     * Returns a limited view of this result.
     */
    public SearchResult limit(int maxItems) {
        if (maxItems < 0) {
            throw new IllegalArgumentException("Limit cannot be negative");
        }
        
        if (items.size() <= maxItems) {
            return this;
        }
        
        return new SearchResult(
            items.subList(0, maxItems),
            query,
            searchTimeMs
        );
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchResult that = (SearchResult) o;
        return searchTimeMs == that.searchTimeMs && 
               totalResults == that.totalResults && 
               Objects.equals(items, that.items) && 
               Objects.equals(query, that.query);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(items, query, searchTimeMs, totalResults);
    }
    
    @Override
    public String toString() {
        return String.format("SearchResult{query='%s', totalResults=%d, searchTimeMs=%d}",
            query, totalResults, searchTimeMs);
    }
}