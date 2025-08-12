package io.thatworked.support.search.infrastructure.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {
    
    @Value("${search.cache.ttl-seconds:300}")
    private int cacheTtlSeconds;
    
    @Value("${search.cache.max-entries:1000}")
    private int maxEntries;
    
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
            "deviceSearch",
            "alertSearch", 
            "reportSearch",
            "globalSearch"
        );
        
        cacheManager.setCaffeine(caffeineCacheBuilder());
        return cacheManager;
    }
    
    private Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
            .expireAfterWrite(cacheTtlSeconds, TimeUnit.SECONDS)
            .maximumSize(maxEntries)
            .recordStats(); // Enable cache statistics
    }
}