package io.thatworked.support.search.application.usecase;

import io.thatworked.support.search.domain.model.SearchQuery;
import io.thatworked.support.search.domain.model.SearchResult;
import io.thatworked.support.search.domain.model.SearchType;
import io.thatworked.support.search.domain.port.DomainLogger;
import io.thatworked.support.search.domain.port.SearchProvider;
import io.thatworked.support.search.domain.service.SearchDomainService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Use case for performing searches within a specific entity type.
 */
@Service
public class TypedSearchUseCase {
    
    private final SearchDomainService searchDomainService;
    private final List<SearchProvider> searchProviders;
    private final DomainLogger logger;
    
    public TypedSearchUseCase(SearchDomainService searchDomainService,
                             List<SearchProvider> searchProviders,
                             DomainLogger logger) {
        this.searchDomainService = searchDomainService;
        this.searchProviders = searchProviders;
        this.logger = logger;
    }
    
    /**
     * Executes a search for a specific entity type.
     */
    public SearchResult execute(String queryString, String typeString, Integer limit) {
        long startTime = System.currentTimeMillis();
        
        // Parse and validate search type
        SearchType searchType = SearchType.fromValue(typeString);
        
        // Create and validate search query
        SearchQuery query = SearchQuery.create(queryString, searchType, validateLimit(limit));
        
        logger.logBusinessEvent("typedSearchStarted", Map.of(
            "query", query.getQuery(),
            "type", query.getType().getValue(),
            "limit", query.getLimit()
        ));
        
        // Check cache first
        Optional<SearchResult> cachedResult = searchDomainService.getCachedResult(query);
        if (cachedResult.isPresent()) {
            return cachedResult.get();
        }
        
        // Find appropriate provider and execute search
        SearchResult result = executeSearch(query);
        
        // Enhance relevance scoring based on query patterns
        if (!result.isEmpty()) {
            List<io.thatworked.support.search.domain.model.SearchItem> enhancedItems = 
                searchDomainService.enhanceRelevanceScoring(result.getItems(), query);
            result = SearchResult.create(enhancedItems, query.getQuery(), result.getSearchTimeMs());
        }
        
        // Cache the result
        searchDomainService.cacheResult(query, result);
        
        long totalTime = System.currentTimeMillis() - startTime;
        
        logger.logBusinessEvent("typedSearchCompleted", Map.of(
            "query", query.getQuery(),
            "type", query.getType().getValue(),
            "totalResults", result.getTotalResults(),
            "totalTimeMs", totalTime
        ));
        
        return result;
    }
    
    private SearchResult executeSearch(SearchQuery query) {
        // For typed searches, we typically use the first available provider
        // In a more complex scenario, we might have type-specific providers
        for (SearchProvider provider : searchProviders) {
            if (provider.isAvailable()) {
                try {
                    return provider.search(query);
                } catch (Exception e) {
                    logger.logBusinessWarning("typedSearchProviderFailed", Map.of(
                        "provider", provider.getProviderName(),
                        "query", query.getQuery(),
                        "type", query.getType().getValue(),
                        "error", e.getMessage()
                    ));
                }
            }
        }
        
        // No available providers or all failed
        logger.logBusinessWarning("noAvailableSearchProviders", Map.of(
            "query", query.getQuery(),
            "type", query.getType().getValue()
        ));
        
        return SearchResult.empty(query.getQuery(), 0);
    }
    
    private int validateLimit(Integer limit) {
        if (limit == null || limit < 1) {
            return 10; // Default limit
        }
        return Math.min(limit, 100); // Max limit
    }
}