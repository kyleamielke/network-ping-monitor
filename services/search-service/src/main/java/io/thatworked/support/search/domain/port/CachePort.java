package io.thatworked.support.search.domain.port;

import java.time.Duration;
import java.util.Optional;

/**
 * Port for caching operations.
 * Abstracts cache implementation from domain logic.
 */
public interface CachePort {
    
    /**
     * Gets a value from the cache.
     *
     * @param key The cache key
     * @param type The expected type of the cached value
     * @return The cached value, or empty if not found
     */
    <T> Optional<T> get(String key, Class<T> type);
    
    /**
     * Puts a value in the cache.
     *
     * @param key The cache key
     * @param value The value to cache
     * @param ttl Time to live for the cached value
     */
    void put(String key, Object value, Duration ttl);
    
    /**
     * Removes a value from the cache.
     *
     * @param key The cache key
     */
    void evict(String key);
    
    /**
     * Clears all cached values.
     */
    void evictAll();
    
    /**
     * Checks if a key exists in the cache.
     *
     * @param key The cache key
     * @return true if the key exists, false otherwise
     */
    boolean exists(String key);
}