package io.thatworked.support.search.api.mapper;

import io.thatworked.support.search.api.dto.response.SearchItemResponse;
import io.thatworked.support.search.api.dto.response.SearchResponse;
import io.thatworked.support.search.domain.model.SearchItem;
import io.thatworked.support.search.domain.model.SearchResult;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper for converting between domain models and API DTOs.
 */
@Component
public class SearchApiMapper {
    
    /**
     * Converts a domain SearchResult to an API SearchResponse.
     */
    public SearchResponse toSearchResponse(SearchResult result) {
        if (result == null) {
            return new SearchResponse();
        }
        
        var items = result.getItems().stream()
            .map(this::toSearchItemResponse)
            .collect(Collectors.toList());
        
        return new SearchResponse(items, result.getQuery(), result.getSearchTimeMs());
    }
    
    /**
     * Converts a domain SearchItem to an API SearchItemResponse.
     */
    private SearchItemResponse toSearchItemResponse(SearchItem item) {
        if (item == null) {
            return null;
        }
        
        SearchItemResponse response = new SearchItemResponse();
        response.setId(item.getId());
        response.setType(item.getType().getValue());
        response.setTitle(item.getTitle());
        response.setSummary(item.getSummary());
        response.setDescription(item.getDescription());
        response.setRelevanceScore(item.getRelevanceScore());
        response.setMetadata(item.getMetadata());
        response.setLastUpdated(item.getLastUpdated());
        
        return response;
    }
}