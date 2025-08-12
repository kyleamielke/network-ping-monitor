package io.thatworked.support.search.domain.service;

import io.thatworked.support.search.domain.exception.SearchProviderException;
import io.thatworked.support.search.domain.model.*;
import io.thatworked.support.search.domain.port.CachePort;
import io.thatworked.support.search.domain.port.DomainLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("SearchDomainService Tests")
class SearchDomainServiceTest {
    
    @Mock
    private DomainLogger logger;
    
    @Mock
    private CachePort cachePort;
    
    private SearchDomainService service;
    
    @BeforeEach
    void setUp() {
        service = new SearchDomainService(logger, cachePort);
    }
    
    @Test
    @DisplayName("Should aggregate multiple search results")
    void testAggregateSearchResults() {
        // Given
        SearchQuery query = SearchQuery.create("test-device", SearchType.ALL, 10);
        
        SearchItem item1 = SearchItem.builder()
            .id(UUID.randomUUID().toString())
            .type(SearchType.DEVICE)
            .title("test-device-1")
            .summary("Device 1")
            .relevanceScore(0.9)
            .lastUpdated(Instant.now())
            .build();
        
        SearchItem item2 = SearchItem.builder()
            .id(UUID.randomUUID().toString())
            .type(SearchType.DEVICE)
            .title("test-device-2")
            .summary("Device 2")
            .relevanceScore(0.8)
            .lastUpdated(Instant.now().minusSeconds(60))
            .build();
        
        SearchResult result1 = SearchResult.create(List.of(item1), "test-device", 100);
        SearchResult result2 = SearchResult.create(List.of(item2), "test-device", 150);
        
        // When
        SearchResult aggregated = service.aggregateResults(List.of(result1, result2), query);
        
        // Then
        assertThat(aggregated).isNotNull();
        assertThat(aggregated.getTotalResults()).isEqualTo(2);
        assertThat(aggregated.getItems()).hasSize(2);
        assertThat(aggregated.getItems().get(0).getRelevanceScore()).isEqualTo(0.9); // Higher score first
        assertThat(aggregated.getSearchTimeMs()).isEqualTo(150); // Max search time
        
        verify(logger).logBusinessEvent(eq("searchResultsAggregated"), any(Map.class));
    }
    
    @Test
    @DisplayName("Should handle empty results in aggregation")
    void testAggregateEmptyResults() {
        // Given
        SearchQuery query = SearchQuery.create("nonexistent", SearchType.ALL, 10);
        
        // When
        SearchResult aggregated = service.aggregateResults(Collections.emptyList(), query);
        
        // Then
        assertThat(aggregated).isNotNull();
        assertThat(aggregated.isEmpty()).isTrue();
        assertThat(aggregated.getTotalResults()).isEqualTo(0);
    }
    
    @Test
    @DisplayName("Should sort results by relevance and last updated")
    void testSortingByRelevanceAndLastUpdated() {
        // Given
        SearchQuery query = SearchQuery.create("device", SearchType.ALL, 3);
        
        Instant now = Instant.now();
        
        SearchItem item1 = SearchItem.builder()
            .id(UUID.randomUUID().toString())
            .type(SearchType.DEVICE)
            .title("Device 1")
            .relevanceScore(0.7)
            .lastUpdated(now.minusSeconds(100))
            .build();
        
        SearchItem item2 = SearchItem.builder()
            .id(UUID.randomUUID().toString())
            .type(SearchType.DEVICE)
            .title("Device 2")
            .relevanceScore(0.7) // Same score as item1
            .lastUpdated(now) // But more recent
            .build();
        
        SearchItem item3 = SearchItem.builder()
            .id(UUID.randomUUID().toString())
            .type(SearchType.DEVICE)
            .title("Device 3")
            .relevanceScore(0.9) // Highest score
            .lastUpdated(now.minusSeconds(200))
            .build();
        
        SearchItem item4 = SearchItem.builder()
            .id(UUID.randomUUID().toString())
            .type(SearchType.DEVICE)
            .title("Device 4")
            .relevanceScore(0.5) // Lowest score, should be filtered by limit
            .lastUpdated(now)
            .build();
        
        SearchResult result = SearchResult.create(List.of(item1, item2, item3, item4), "device", 100);
        
        // When
        SearchResult aggregated = service.aggregateResults(List.of(result), query);
        
        // Then
        assertThat(aggregated.getItems()).hasSize(3); // Limited to 3
        assertThat(aggregated.getItems().get(0).getTitle()).isEqualTo("Device 3"); // Highest relevance
        assertThat(aggregated.getItems().get(1).getTitle()).isEqualTo("Device 2"); // Same score but more recent than Device 1
        assertThat(aggregated.getItems().get(2).getTitle()).isEqualTo("Device 1");
        // Device 4 should be excluded due to limit
    }
    
    @Test
    @DisplayName("Should calculate cache key correctly")
    void testCalculateCacheKey() {
        // Given
        SearchQuery query = SearchQuery.create("Test Query", SearchType.DEVICE, 20);
        
        // When
        String cacheKey = service.calculateCacheKey(query);
        
        // Then
        assertThat(cacheKey).isEqualTo("search:device:test query:20");
    }
    
    @Test
    @DisplayName("Should cache search result")
    void testCacheSearchResult() {
        // Given
        SearchQuery query = SearchQuery.create("test", SearchType.ALL, 10);
        
        SearchItem item = SearchItem.builder()
            .id(UUID.randomUUID().toString())
            .type(SearchType.DEVICE)
            .title("Test Device")
            .relevanceScore(0.8)
            .build();
        
        SearchResult result = SearchResult.create(List.of(item), "test", 50);
        
        // When
        service.cacheResult(query, result);
        
        // Then
        verify(cachePort).put(eq("search:all:test:10"), eq(result), eq(Duration.ofMinutes(5)));
        verify(logger).logBusinessEvent(eq("searchResultCached"), any(Map.class));
    }
    
    @Test
    @DisplayName("Should not cache empty result")
    void testDoNotCacheEmptyResult() {
        // Given
        SearchQuery query = SearchQuery.create("test", SearchType.ALL, 10);
        
        SearchResult emptyResult = SearchResult.empty("test", 50);
        
        // When
        service.cacheResult(query, emptyResult);
        
        // Then
        verify(cachePort, never()).put(anyString(), any(), any());
    }
    
    @Test
    @DisplayName("Should retrieve cached result")
    void testGetCachedResult() {
        // Given
        SearchQuery query = SearchQuery.create("cached", SearchType.DEVICE, 10);
        
        SearchItem item = SearchItem.builder()
            .id(UUID.randomUUID().toString())
            .type(SearchType.DEVICE)
            .title("Cached Device")
            .relevanceScore(0.9)
            .build();
        
        SearchResult cachedResult = SearchResult.create(List.of(item), "cached", 30);
        
        when(cachePort.get("search:device:cached:10", SearchResult.class))
            .thenReturn(Optional.of(cachedResult));
        
        // When
        Optional<SearchResult> result = service.getCachedResult(query);
        
        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(cachedResult);
        verify(logger).logBusinessEvent(eq("searchCacheHit"), any(Map.class));
    }
    
    @Test
    @DisplayName("Should handle cache miss")
    void testCacheMiss() {
        // Given
        SearchQuery query = SearchQuery.create("not-cached", SearchType.DEVICE, 10);
        
        when(cachePort.get("search:device:not-cached:10", SearchResult.class))
            .thenReturn(Optional.empty());
        
        // When
        Optional<SearchResult> result = service.getCachedResult(query);
        
        // Then
        assertThat(result).isEmpty();
        verify(logger, never()).logBusinessEvent(eq("searchCacheHit"), any());
    }
    
    @Test
    @DisplayName("Should enhance relevance scoring for exact title match")
    void testEnhanceRelevanceForExactTitleMatch() {
        // Given
        SearchQuery query = SearchQuery.create("test-device", SearchType.DEVICE, 10);
        
        SearchItem item = SearchItem.builder()
            .id(UUID.randomUUID().toString())
            .type(SearchType.DEVICE)
            .title("test-device")
            .relevanceScore(0.6)
            .build();
        
        // When
        List<SearchItem> enhanced = service.enhanceRelevanceScoring(List.of(item), query);
        
        // Then
        assertThat(enhanced).hasSize(1);
        assertThat(enhanced.get(0).getRelevanceScore()).isCloseTo(0.9, within(0.0001)); // 0.6 + 0.3 boost
    }
    
    @Test
    @DisplayName("Should enhance relevance for IP address pattern match")
    void testEnhanceRelevanceForIPAddressPattern() {
        // Given
        SearchQuery ipQuery = SearchQuery.create("192.168", SearchType.DEVICE, 10);
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("ipAddress", "192.168.1.100");
        
        SearchItem item = SearchItem.builder()
            .id(UUID.randomUUID().toString())
            .type(SearchType.DEVICE)
            .title("Server 1")
            .relevanceScore(0.5)
            .metadata(metadata)
            .build();
        
        // When
        List<SearchItem> enhanced = service.enhanceRelevanceScoring(List.of(item), ipQuery);
        
        // Then
        assertThat(enhanced).hasSize(1);
        assertThat(enhanced.get(0).getRelevanceScore()).isEqualTo(0.7); // 0.5 + 0.2 boost
    }
    
    @Test
    @DisplayName("Should not exceed max relevance score")
    void testRelevanceScoreCappedAtOne() {
        // Given
        SearchQuery query = SearchQuery.create("test-device", SearchType.DEVICE, 10);
        
        SearchItem item = SearchItem.builder()
            .id(UUID.randomUUID().toString())
            .type(SearchType.DEVICE)
            .title("test-device")
            .relevanceScore(0.9) // Already high score
            .build();
        
        // When
        List<SearchItem> enhanced = service.enhanceRelevanceScoring(List.of(item), query);
        
        // Then
        assertThat(enhanced).hasSize(1);
        assertThat(enhanced.get(0).getRelevanceScore()).isEqualTo(1.0); // Capped at 1.0
    }
    
    @Test
    @DisplayName("Should validate at least one provider is available")
    void testValidateProvidersAvailable() {
        // Given
        Map<String, Boolean> providerAvailability = new HashMap<>();
        providerAvailability.put("elasticsearch", true);
        providerAvailability.put("database", false);
        
        // When - should not throw
        service.validateProvidersAvailable(providerAvailability);
        
        // Then
        verify(logger, never()).logBusinessWarning(anyString(), any());
    }
    
    @Test
    @DisplayName("Should throw exception when no providers available")
    void testThrowExceptionWhenNoProvidersAvailable() {
        // Given
        Map<String, Boolean> providerAvailability = new HashMap<>();
        providerAvailability.put("elasticsearch", false);
        providerAvailability.put("database", false);
        
        // When & Then
        assertThatThrownBy(() -> service.validateProvidersAvailable(providerAvailability))
            .isInstanceOf(SearchProviderException.class)
            .hasMessage("No search providers are currently available");
        
        verify(logger).logBusinessWarning(eq("noSearchProvidersAvailable"), any(Map.class));
    }
    
    @Test
    @DisplayName("Should handle null metadata in items")
    void testHandleNullMetadata() {
        // Given
        SearchQuery ipQuery = SearchQuery.create("192.168", SearchType.DEVICE, 10);
        
        SearchItem item = SearchItem.builder()
            .id(UUID.randomUUID().toString())
            .type(SearchType.DEVICE)
            .title("Device")
            .relevanceScore(0.5)
            .metadata(null) // No metadata
            .build();
        
        // When - should not throw
        List<SearchItem> enhanced = service.enhanceRelevanceScoring(List.of(item), ipQuery);
        
        // Then
        assertThat(enhanced).hasSize(1);
        assertThat(enhanced.get(0).getRelevanceScore()).isEqualTo(0.5); // No enhancement
    }
    
    @Test
    @DisplayName("Should handle null last updated in sorting")
    void testHandleNullLastUpdatedInSorting() {
        // Given
        SearchQuery query = SearchQuery.create("device", SearchType.ALL, 3);
        
        SearchItem item1 = SearchItem.builder()
            .id(UUID.randomUUID().toString())
            .type(SearchType.DEVICE)
            .title("Device 1")
            .relevanceScore(0.7)
            .lastUpdated(null) // Null lastUpdated
            .build();
        
        SearchItem item2 = SearchItem.builder()
            .id(UUID.randomUUID().toString())
            .type(SearchType.DEVICE)
            .title("Device 2")
            .relevanceScore(0.7)
            .lastUpdated(Instant.now())
            .build();
        
        SearchResult result = SearchResult.create(List.of(item1, item2), "device", 100);
        
        // When
        SearchResult aggregated = service.aggregateResults(List.of(result), query);
        
        // Then
        assertThat(aggregated.getItems()).hasSize(2);
        assertThat(aggregated.getItems().get(0).getTitle()).isEqualTo("Device 2"); // With timestamp comes first
        assertThat(aggregated.getItems().get(1).getTitle()).isEqualTo("Device 1"); // Null timestamp comes last
    }
}