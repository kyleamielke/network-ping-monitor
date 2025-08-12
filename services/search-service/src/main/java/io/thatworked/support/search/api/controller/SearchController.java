package io.thatworked.support.search.api.controller;

import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.search.api.dto.request.SearchRequest;
import io.thatworked.support.search.api.dto.response.SearchResponse;
import io.thatworked.support.search.api.mapper.SearchApiMapper;
import io.thatworked.support.search.application.usecase.GlobalSearchUseCase;
import io.thatworked.support.search.application.usecase.TypedSearchUseCase;
import io.thatworked.support.search.domain.model.SearchResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * REST controller for search operations.
 * Provides endpoints for global and typed searches.
 */
@RestController
@RequestMapping("${search-service.service.api.base-path:/api/v1/search}")
@CrossOrigin
public class SearchController {
    
    private final StructuredLogger logger;
    private final GlobalSearchUseCase globalSearchUseCase;
    private final TypedSearchUseCase typedSearchUseCase;
    private final SearchApiMapper searchApiMapper;
    
    public SearchController(StructuredLoggerFactory loggerFactory,
                           GlobalSearchUseCase globalSearchUseCase,
                           TypedSearchUseCase typedSearchUseCase,
                           SearchApiMapper searchApiMapper) {
        this.logger = loggerFactory.getLogger(SearchController.class);
        this.globalSearchUseCase = globalSearchUseCase;
        this.typedSearchUseCase = typedSearchUseCase;
        this.searchApiMapper = searchApiMapper;
    }
    
    /**
     * Performs a global search across all entity types.
     */
    @GetMapping
    public ResponseEntity<SearchResponse> globalSearch(
            @RequestParam(required = true) @NotBlank @Size(min = 2, max = 500) String q,
            @RequestParam(required = false, defaultValue = "10") @Min(1) Integer limit) {
        
        logger.with("query", q)
              .with("limit", limit)
              .with("endpoint", "globalSearch")
              .info("Global search request received");
        
        try {
            SearchResult result = globalSearchUseCase.execute(q, limit);
            SearchResponse response = searchApiMapper.toSearchResponse(result);
            
            logger.with("query", q)
                  .with("resultCount", result.getTotalResults())
                  .with("searchTime", result.getSearchTimeMs())
                  .info("Global search completed");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.with("query", q)
                  .with("error", e.getMessage())
                  .error("Global search failed", e);
            throw e;
        }
    }
    
    /**
     * Performs a search within a specific entity type.
     */
    @GetMapping("/{type}")
    public ResponseEntity<SearchResponse> searchByType(
            @PathVariable String type,
            @RequestParam(required = true) @NotBlank @Size(min = 2, max = 500) String q,
            @RequestParam(required = false, defaultValue = "10") @Min(1) Integer limit) {
        
        logger.with("type", type)
              .with("query", q)
              .with("limit", limit)
              .with("endpoint", "searchByType")
              .info("Typed search request received");
        
        try {
            SearchResult result = typedSearchUseCase.execute(q, type, limit);
            SearchResponse response = searchApiMapper.toSearchResponse(result);
            
            logger.with("type", type)
                  .with("query", q)
                  .with("resultCount", result.getTotalResults())
                  .with("searchTime", result.getSearchTimeMs())
                  .info("Typed search completed");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.with("type", type)
                  .with("query", q)
                  .with("error", e.getMessage())
                  .error("Typed search failed", e);
            throw e;
        }
    }
}