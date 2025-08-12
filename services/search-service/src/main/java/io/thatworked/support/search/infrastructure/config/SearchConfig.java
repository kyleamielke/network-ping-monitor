package io.thatworked.support.search.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for search-specific settings.
 */
@Configuration
@ConfigurationProperties(prefix = "search")
public class SearchConfig {
    
    private Cache cache = new Cache();
    private Result result = new Result();
    private Provider provider = new Provider();
    private Scoring scoring = new Scoring();
    
    public Cache getCache() {
        return cache;
    }
    
    public void setCache(Cache cache) {
        this.cache = cache;
    }
    
    public Result getResult() {
        return result;
    }
    
    public void setResult(Result result) {
        this.result = result;
    }
    
    public Provider getProvider() {
        return provider;
    }
    
    public void setProvider(Provider provider) {
        this.provider = provider;
    }
    
    public Scoring getScoring() {
        return scoring;
    }
    
    public void setScoring(Scoring scoring) {
        this.scoring = scoring;
    }
    
    public static class Cache {
        private boolean enabled = true;
        private int ttlSeconds = 300;
        private int maxSize = 1000;
        
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
        public int getTtlSeconds() {
            return ttlSeconds;
        }
        
        public void setTtlSeconds(int ttlSeconds) {
            this.ttlSeconds = ttlSeconds;
        }
        
        public int getMaxSize() {
            return maxSize;
        }
        
        public void setMaxSize(int maxSize) {
            this.maxSize = maxSize;
        }
    }
    
    public static class Result {
        private int defaultLimit = 10;
        private int maxLimit = 100;
        
        public int getDefaultLimit() {
            return defaultLimit;
        }
        
        public void setDefaultLimit(int defaultLimit) {
            this.defaultLimit = defaultLimit;
        }
        
        public int getMaxLimit() {
            return maxLimit;
        }
        
        public void setMaxLimit(int maxLimit) {
            this.maxLimit = maxLimit;
        }
    }
    
    public static class Provider {
        private int timeoutMs = 5000;
        private boolean parallelEnabled = true;
        
        public int getTimeoutMs() {
            return timeoutMs;
        }
        
        public void setTimeoutMs(int timeoutMs) {
            this.timeoutMs = timeoutMs;
        }
        
        public boolean isParallelEnabled() {
            return parallelEnabled;
        }
        
        public void setParallelEnabled(boolean parallelEnabled) {
            this.parallelEnabled = parallelEnabled;
        }
    }
    
    public static class Scoring {
        private double exactMatchBoost = 0.3;
        private double ipPatternBoost = 0.2;
        
        public double getExactMatchBoost() {
            return exactMatchBoost;
        }
        
        public void setExactMatchBoost(double exactMatchBoost) {
            this.exactMatchBoost = exactMatchBoost;
        }
        
        public double getIpPatternBoost() {
            return ipPatternBoost;
        }
        
        public void setIpPatternBoost(double ipPatternBoost) {
            this.ipPatternBoost = ipPatternBoost;
        }
    }
}