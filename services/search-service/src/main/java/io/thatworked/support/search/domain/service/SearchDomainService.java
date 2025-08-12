package io.thatworked.support.search.domain.service;

import io.thatworked.support.search.domain.exception.SearchProviderException;
import io.thatworked.support.search.domain.model.SearchItem;
import io.thatworked.support.search.domain.model.SearchQuery;
import io.thatworked.support.search.domain.model.SearchResult;
import io.thatworked.support.search.domain.port.CachePort;
import io.thatworked.support.search.domain.port.DomainLogger;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Domain service that orchestrates search operations.
 * Contains core business logic for searching and result aggregation.
 */
public class SearchDomainService {
    
    private static final Duration CACHE_TTL = Duration.ofMinutes(5);
    
    private final DomainLogger logger;
    private final CachePort cachePort;
    
    public SearchDomainService(DomainLogger logger, CachePort cachePort) {
        this.logger = logger;
        this.cachePort = cachePort;
    }
    
    /**
     * Aggregates results from multiple search results.
     * Applies business rules for result merging and relevance scoring.
     */
    public SearchResult aggregateResults(List<SearchResult> results, SearchQuery query) {
        if (results == null || results.isEmpty()) {
            return SearchResult.empty(query.getQuery(), 0);
        }
        
        long maxSearchTime = results.stream()
            .mapToLong(SearchResult::getSearchTimeMs)
            .max()
            .orElse(0);
        
        List<SearchItem> allItems = results.stream()
            .flatMap(result -> result.getItems().stream())
            .collect(Collectors.toList());
        
        // Apply business rules for result ordering
        List<SearchItem> sortedItems = sortAndLimitResults(allItems, query);
        
        SearchResult aggregatedResult = SearchResult.create(sortedItems, query.getQuery(), maxSearchTime);
        
        logger.logBusinessEvent("searchResultsAggregated", Map.of(
            "query", query.getQuery(),
            "resultCount", sortedItems.size(),
            "sourceCount", results.size(),
            "searchTimeMs", maxSearchTime
        ));
        
        return aggregatedResult;
    }
    
    /**
     * Sorts and limits search results based on relevance and business rules.
     */
    private List<SearchItem> sortAndLimitResults(List<SearchItem> items, SearchQuery query) {
        // Sort by relevance score (descending) and then by last updated (descending)
        Comparator<SearchItem> comparator = Comparator
            .comparing(SearchItem::getRelevanceScore).reversed()
            .thenComparing(item -> item.getLastUpdated() != null ? item.getLastUpdated() : java.time.Instant.MIN,
                Comparator.reverseOrder());
        
        return items.stream()
            .sorted(comparator)
            .limit(query.getLimit())
            .collect(Collectors.toList());
    }
    
    /**
     * Calculates a cache key for a search query.
     */
    public String calculateCacheKey(SearchQuery query) {
        return String.format("search:%s:%s:%d", 
            query.getType().getValue(), 
            query.getQuery().toLowerCase(), 
            query.getLimit()
        );
    }
    
    /**
     * Caches a search result.
     */
    public void cacheResult(SearchQuery query, SearchResult result) {
        if (result == null || result.isEmpty()) {
            return;
        }
        
        String cacheKey = calculateCacheKey(query);
        cachePort.put(cacheKey, result, CACHE_TTL);
        
        logger.logBusinessEvent("searchResultCached", Map.of(
            "query", query.getQuery(),
            "type", query.getType().getValue(),
            "resultCount", result.getTotalResults(),
            "cacheKey", cacheKey
        ));
    }
    
    /**
     * Retrieves a cached search result.
     */
    public Optional<SearchResult> getCachedResult(SearchQuery query) {
        String cacheKey = calculateCacheKey(query);
        Optional<SearchResult> cached = cachePort.get(cacheKey, SearchResult.class);
        
        if (cached.isPresent()) {
            logger.logBusinessEvent("searchCacheHit", Map.of(
                "query", query.getQuery(),
                "type", query.getType().getValue(),
                "cacheKey", cacheKey
            ));
        }
        
        return cached;
    }
    
    /**
     * Enhances search items with additional relevance scoring based on query patterns.
     */
    public List<SearchItem> enhanceRelevanceScoring(List<SearchItem> items, SearchQuery query) {
        return items.stream()
            .map(item -> enhanceItemRelevance(item, query))
            .collect(Collectors.toList());
    }
    
    private SearchItem enhanceItemRelevance(SearchItem item, SearchQuery query) {
        double baseScore = item.getRelevanceScore();
        double enhancedScore = baseScore;
        
        // Boost score for exact title matches
        if (item.getTitle() != null && item.getTitle().equalsIgnoreCase(query.getQuery())) {
            enhancedScore = Math.min(1.0, enhancedScore + 0.3);
        }
        
        // Boost score for items matching specific patterns
        if (query.isIPAddressPattern() && "device".equals(item.getType().getValue())) {
            String ipAddress = item.getMetadataValue("ipAddress", String.class);
            if (ipAddress != null && ipAddress.startsWith(query.getQuery())) {
                enhancedScore = Math.min(1.0, enhancedScore + 0.2);
            }
        }
        
        // Return a new item with enhanced score if changed
        if (enhancedScore != baseScore) {
            return SearchItem.builder()
                .id(item.getId())
                .type(item.getType())
                .title(item.getTitle())
                .summary(item.getSummary())
                .description(item.getDescription())
                .relevanceScore(enhancedScore)
                .metadata(item.getMetadata())
                .lastUpdated(item.getLastUpdated())
                .build();
        }
        
        return item;
    }
    
    /**
     * Validates that at least one search provider is available.
     */
    public void validateProvidersAvailable(Map<String, Boolean> providerAvailability) {
        boolean anyAvailable = providerAvailability.values().stream().anyMatch(Boolean::booleanValue);
        
        if (!anyAvailable) {
            logger.logBusinessWarning("noSearchProvidersAvailable", Map.of(
                "providers", providerAvailability
            ));
            throw new SearchProviderException("No search providers are currently available");
        }
    }
}