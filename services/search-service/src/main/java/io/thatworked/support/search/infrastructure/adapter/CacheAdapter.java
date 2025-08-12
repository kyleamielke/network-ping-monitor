package io.thatworked.support.search.infrastructure.adapter;

import io.thatworked.support.search.domain.port.CachePort;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

/**
 * Adapter that implements the domain CachePort using Spring Cache.
 */
@Component
public class CacheAdapter implements CachePort {
    
    private static final String SEARCH_CACHE_NAME = "searchCache";
    
    private final CacheManager cacheManager;
    
    public CacheAdapter(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String key, Class<T> type) {
        Cache cache = getCache();
        if (cache == null) {
            return Optional.empty();
        }
        
        Cache.ValueWrapper wrapper = cache.get(key);
        if (wrapper == null || wrapper.get() == null) {
            return Optional.empty();
        }
        
        Object value = wrapper.get();
        if (!type.isInstance(value)) {
            cache.evict(key); // Remove invalid cached value
            return Optional.empty();
        }
        
        return Optional.of((T) value);
    }
    
    @Override
    public void put(String key, Object value, Duration ttl) {
        Cache cache = getCache();
        if (cache != null && value != null) {
            // Note: Spring Cache doesn't support per-entry TTL out of the box
            // TTL is configured at cache level in CacheConfig
            cache.put(key, value);
        }
    }
    
    @Override
    public void evict(String key) {
        Cache cache = getCache();
        if (cache != null) {
            cache.evict(key);
        }
    }
    
    @Override
    public void evictAll() {
        Cache cache = getCache();
        if (cache != null) {
            cache.clear();
        }
    }
    
    @Override
    public boolean exists(String key) {
        Cache cache = getCache();
        if (cache == null) {
            return false;
        }
        
        Cache.ValueWrapper wrapper = cache.get(key);
        return wrapper != null && wrapper.get() != null;
    }
    
    private Cache getCache() {
        return cacheManager.getCache(SEARCH_CACHE_NAME);
    }
}