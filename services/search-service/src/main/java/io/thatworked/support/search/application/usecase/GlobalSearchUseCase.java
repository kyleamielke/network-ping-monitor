package io.thatworked.support.search.application.usecase;

import io.thatworked.support.search.domain.model.SearchQuery;
import io.thatworked.support.search.domain.model.SearchResult;
import io.thatworked.support.search.domain.model.SearchType;
import io.thatworked.support.search.domain.port.DomainLogger;
import io.thatworked.support.search.domain.port.SearchProvider;
import io.thatworked.support.search.domain.service.SearchDomainService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Use case for performing global searches across all entity types.
 */
@Service
public class GlobalSearchUseCase {
    
    private static final long SEARCH_TIMEOUT_MS = 5000;
    
    private final SearchDomainService searchDomainService;
    private final List<SearchProvider> searchProviders;
    private final DomainLogger logger;
    
    public GlobalSearchUseCase(SearchDomainService searchDomainService,
                              List<SearchProvider> searchProviders,
                              DomainLogger logger) {
        this.searchDomainService = searchDomainService;
        this.searchProviders = searchProviders;
        this.logger = logger;
    }
    
    /**
     * Executes a global search across all available providers.
     */
    public SearchResult execute(String queryString, Integer limit) {
        long startTime = System.currentTimeMillis();
        
        // Create and validate search query
        SearchQuery query = SearchQuery.createGlobal(queryString, validateLimit(limit));
        
        logger.logBusinessEvent("globalSearchStarted", Map.of(
            "query", query.getQuery(),
            "limit", query.getLimit()
        ));
        
        // Check cache first
        Optional<SearchResult> cachedResult = searchDomainService.getCachedResult(query);
        if (cachedResult.isPresent()) {
            return cachedResult.get();
        }
        
        // Execute parallel searches
        List<SearchResult> results = executeParallelSearches(query);
        
        // Aggregate results
        SearchResult aggregatedResult = searchDomainService.aggregateResults(results, query);
        
        // Cache the result
        searchDomainService.cacheResult(query, aggregatedResult);
        
        long totalTime = System.currentTimeMillis() - startTime;
        
        logger.logBusinessEvent("globalSearchCompleted", Map.of(
            "query", query.getQuery(),
            "totalResults", aggregatedResult.getTotalResults(),
            "totalTimeMs", totalTime
        ));
        
        return aggregatedResult;
    }
    
    private List<SearchResult> executeParallelSearches(SearchQuery query) {
        List<CompletableFuture<SearchResult>> futures = new ArrayList<>();
        
        for (SearchProvider provider : searchProviders) {
            if (provider.isAvailable()) {
                CompletableFuture<SearchResult> future = CompletableFuture
                    .supplyAsync(() -> executeProviderSearch(provider, query))
                    .orTimeout(SEARCH_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                    .exceptionally(throwable -> handleSearchError(provider, query, throwable));
                
                futures.add(future);
            }
        }
        
        // Wait for all searches to complete
        CompletableFuture<Void> allSearches = CompletableFuture.allOf(
            futures.toArray(new CompletableFuture[0])
        );
        
        try {
            allSearches.get(SEARCH_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            logger.logBusinessWarning("globalSearchTimeout", Map.of(
                "query", query.getQuery(),
                "error", e.getMessage()
            ));
        }
        
        // Collect results
        List<SearchResult> results = new ArrayList<>();
        for (CompletableFuture<SearchResult> future : futures) {
            if (future.isDone() && !future.isCompletedExceptionally()) {
                try {
                    SearchResult result = future.get();
                    if (result != null && !result.isEmpty()) {
                        results.add(result);
                    }
                } catch (Exception e) {
                    // Individual result error already logged
                }
            }
        }
        
        return results;
    }
    
    private SearchResult executeProviderSearch(SearchProvider provider, SearchQuery query) {
        long startTime = System.currentTimeMillis();
        
        try {
            SearchResult result = provider.search(query);
            
            long searchTime = System.currentTimeMillis() - startTime;
            
            logger.logBusinessEvent("providerSearchCompleted", Map.of(
                "provider", provider.getProviderName(),
                "query", query.getQuery(),
                "resultCount", result.getTotalResults(),
                "searchTimeMs", searchTime
            ));
            
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Provider search failed: " + provider.getProviderName(), e);
        }
    }
    
    private SearchResult handleSearchError(SearchProvider provider, SearchQuery query, Throwable throwable) {
        logger.logBusinessWarning("providerSearchFailed", Map.of(
            "provider", provider.getProviderName(),
            "query", query.getQuery(),
            "error", throwable.getMessage()
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