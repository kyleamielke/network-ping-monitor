package io.thatworked.support.search.api.controller;

import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.search.application.usecase.ClearCacheUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for cache management operations.
 */
@RestController
@RequestMapping("${search-service.service.api.cache-base-path:/api/v1/cache}")
@CrossOrigin
public class CacheController {
    
    private final StructuredLogger logger;
    private final ClearCacheUseCase clearCacheUseCase;
    
    public CacheController(StructuredLoggerFactory loggerFactory,
                          ClearCacheUseCase clearCacheUseCase) {
        this.logger = loggerFactory.getLogger(CacheController.class);
        this.clearCacheUseCase = clearCacheUseCase;
    }
    
    /**
     * Clears all cached search results.
     */
    @DeleteMapping
    public ResponseEntity<Void> clearAllCache() {
        logger.with("endpoint", "clearAllCache")
              .info("Clear cache request received");
        
        try {
            clearCacheUseCase.execute();
            
            logger.with("operation", "clearAllCache")
                  .with("status", "success")
                  .info("Cache cleared successfully");
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.with("error", e.getMessage())
                  .error("Failed to clear cache", e);
            throw e;
        }
    }
    
    /**
     * Clears a specific cached entry.
     */
    @DeleteMapping("/{key}")
    public ResponseEntity<Void> clearCacheKey(@PathVariable String key) {
        logger.with("cacheKey", key)
              .with("endpoint", "clearCacheKey")
              .info("Clear cache key request received");
        
        try {
            clearCacheUseCase.execute(key);
            
            logger.with("cacheKey", key)
                  .info("Cache key cleared successfully");
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.with("cacheKey", key)
                  .with("error", e.getMessage())
                  .error("Failed to clear cache key", e);
            throw e;
        }
    }
}