package io.thatworked.support.search.domain.port;

import io.thatworked.support.search.domain.model.SearchQuery;
import io.thatworked.support.search.domain.model.SearchResult;

/**
 * Port for search providers to implement.
 * Defines the contract for searching different types of entities.
 */
public interface SearchProvider {
    
    /**
     * Performs a search based on the given query.
     *
     * @param query The search query
     * @return The search results
     */
    SearchResult search(SearchQuery query);
    
    /**
     * Gets the name of this search provider.
     *
     * @return The provider name
     */
    String getProviderName();
    
    /**
     * Checks if this provider is available.
     *
     * @return true if the provider is available, false otherwise
     */
    boolean isAvailable();
}