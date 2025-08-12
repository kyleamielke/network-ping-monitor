package io.thatworked.support.search.domain.model;

import io.thatworked.support.search.domain.exception.InvalidSearchQueryException;

import java.util.Objects;

/**
 * Domain model representing a search query.
 * Encapsulates business rules for valid search queries.
 */
public class SearchQuery {
    
    private static final int MIN_QUERY_LENGTH = 2;
    private static final int MAX_QUERY_LENGTH = 500;
    
    private final String query;
    private final SearchType type;
    private final int limit;
    
    private SearchQuery(String query, SearchType type, int limit) {
        this.query = query;
        this.type = type;
        this.limit = limit;
    }
    
    /**
     * Factory method to create a search query with validation.
     */
    public static SearchQuery create(String query, SearchType type, int limit) {
        validate(query, limit);
        return new SearchQuery(query.trim(), type, limit);
    }
    
    /**
     * Factory method for global search across all types.
     */
    public static SearchQuery createGlobal(String query, int limit) {
        return create(query, SearchType.ALL, limit);
    }
    
    private static void validate(String query, int limit) {
        if (query == null || query.trim().isEmpty()) {
            throw new InvalidSearchQueryException("Search query cannot be empty");
        }
        
        String trimmed = query.trim();
        if (trimmed.length() < MIN_QUERY_LENGTH) {
            throw new InvalidSearchQueryException(
                String.format("Search query must be at least %d characters", MIN_QUERY_LENGTH)
            );
        }
        
        if (trimmed.length() > MAX_QUERY_LENGTH) {
            throw new InvalidSearchQueryException(
                String.format("Search query cannot exceed %d characters", MAX_QUERY_LENGTH)
            );
        }
        
        if (limit < 1) {
            throw new InvalidSearchQueryException("Search limit must be at least 1");
        }
    }
    
    /**
     * Checks if this query matches a specific pattern (e.g., IP address).
     */
    public boolean isIPAddressPattern() {
        return query.matches("^\\d{1,3}(\\.\\d{0,3}){0,3}$");
    }
    
    /**
     * Checks if this query matches a MAC address pattern.
     */
    public boolean isMACAddressPattern() {
        return query.matches("^([0-9A-Fa-f]{2}[:-]?){1,6}$");
    }
    
    /**
     * Checks if this query matches an asset tag pattern.
     */
    public boolean isAssetTagPattern() {
        return query.toUpperCase().matches("^[A-Z]{2,}-\\d+");
    }
    
    /**
     * Gets the normalized query string (trimmed).
     */
    public String getQuery() {
        return query;
    }
    
    /**
     * Gets the search type.
     */
    public SearchType getType() {
        return type;
    }
    
    /**
     * Gets the result limit.
     */
    public int getLimit() {
        return limit;
    }
    
    /**
     * Creates a copy with a different type.
     */
    public SearchQuery withType(SearchType newType) {
        return new SearchQuery(this.query, newType, this.limit);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchQuery that = (SearchQuery) o;
        return limit == that.limit && 
               Objects.equals(query, that.query) && 
               type == that.type;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(query, type, limit);
    }
    
    @Override
    public String toString() {
        return String.format("SearchQuery{query='%s', type=%s, limit=%d}", 
            query, type, limit);
    }
}