package io.thatworked.support.search.application.usecase;

import io.thatworked.support.search.domain.port.CachePort;
import io.thatworked.support.search.domain.port.DomainLogger;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Use case for clearing the search cache.
 */
@Service
public class ClearCacheUseCase {
    
    private final CachePort cachePort;
    private final DomainLogger logger;
    
    public ClearCacheUseCase(CachePort cachePort, DomainLogger logger) {
        this.cachePort = cachePort;
        this.logger = logger;
    }
    
    /**
     * Clears all cached search results.
     */
    public void execute() {
        logger.logBusinessEvent("cacheClearStarted", Map.of(
            "timestamp", System.currentTimeMillis()
        ));
        
        try {
            cachePort.evictAll();
            
            logger.logBusinessEvent("cacheClearCompleted", Map.of(
                "timestamp", System.currentTimeMillis(),
                "status", "success"
            ));
        } catch (Exception e) {
            logger.logBusinessWarning("cacheClearFailed", Map.of(
                "error", e.getMessage(),
                "errorType", e.getClass().getSimpleName()
            ));
            throw new RuntimeException("Failed to clear cache", e);
        }
    }
    
    /**
     * Clears cached results for a specific search key.
     */
    public void execute(String cacheKey) {
        logger.logBusinessEvent("cacheEvictStarted", Map.of(
            "cacheKey", cacheKey,
            "timestamp", System.currentTimeMillis()
        ));
        
        try {
            cachePort.evict(cacheKey);
            
            logger.logBusinessEvent("cacheEvictCompleted", Map.of(
                "cacheKey", cacheKey,
                "timestamp", System.currentTimeMillis(),
                "status", "success"
            ));
        } catch (Exception e) {
            logger.logBusinessWarning("cacheEvictFailed", Map.of(
                "cacheKey", cacheKey,
                "error", e.getMessage(),
                "errorType", e.getClass().getSimpleName()
            ));
            throw new RuntimeException("Failed to evict cache key: " + cacheKey, e);
        }
    }
}