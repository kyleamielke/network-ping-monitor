package io.thatworked.support.ping.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for the ping executor system
 */
@Configuration
@ConfigurationProperties(prefix = "ping.executor")
public class PingExecutorConfig {
    
    /**
     * Maximum concurrent pings allowed (virtual threads)
     */
    private int maxConcurrentPings = 1000;
    
    /**
     * Ping timeout in milliseconds
     */
    private int timeoutMs = 1000;
    
    /**
     * Batch processing interval in milliseconds
     */
    private int batchIntervalMs = 100;
    
    /**
     * Default ping interval in seconds
     */
    private int pingInterval = 5;
    
    /**
     * Number of retries for failed pings
     */
    private int retryAttempts = 3;
    
    /**
     * Delay between retry attempts in milliseconds
     */
    private int retryDelayMs = 100;
    
    /**
     * Circuit breaker failure threshold
     */
    private int circuitBreakerFailureThreshold = 5;
    
    /**
     * Circuit breaker open duration in minutes
     */
    private int circuitBreakerOpenDurationMinutes = 5;
    
    /**
     * Circuit breaker half-open test interval in minutes
     */
    private int circuitBreakerHalfOpenIntervalMinutes = 1;
    
    /**
     * Enable/disable circuit breaker
     */
    private boolean circuitBreakerEnabled = true;
    
    /**
     * Enable/disable virtual threads (falls back to fixed thread pool)
     */
    private boolean virtualThreadsEnabled = true;
    
    /**
     * Fixed thread pool size (used when virtual threads disabled)
     */
    private int fixedThreadPoolSize = 50;
    
    /**
     * Enable metrics logging
     */
    private boolean metricsEnabled = true;
    
    /**
     * Metrics logging interval in seconds
     */
    private int metricsIntervalSeconds = 30;
    
    /**
     * Scheduler thread pool size
     */
    private int schedulerPoolSize = 2;

    public int getMaxConcurrentPings() {
        return maxConcurrentPings;
    }

    public void setMaxConcurrentPings(int maxConcurrentPings) {
        this.maxConcurrentPings = maxConcurrentPings;
    }

    public int getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(int timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    public int getBatchIntervalMs() {
        return batchIntervalMs;
    }

    public void setBatchIntervalMs(int batchIntervalMs) {
        this.batchIntervalMs = batchIntervalMs;
    }

    public int getPingInterval() {
        return pingInterval;
    }

    public void setPingInterval(int pingInterval) {
        this.pingInterval = pingInterval;
    }

    public int getRetryAttempts() {
        return retryAttempts;
    }

    public void setRetryAttempts(int retryAttempts) {
        this.retryAttempts = retryAttempts;
    }

    public int getRetryDelayMs() {
        return retryDelayMs;
    }

    public void setRetryDelayMs(int retryDelayMs) {
        this.retryDelayMs = retryDelayMs;
    }

    public int getCircuitBreakerFailureThreshold() {
        return circuitBreakerFailureThreshold;
    }

    public void setCircuitBreakerFailureThreshold(int circuitBreakerFailureThreshold) {
        this.circuitBreakerFailureThreshold = circuitBreakerFailureThreshold;
    }

    public int getCircuitBreakerOpenDurationMinutes() {
        return circuitBreakerOpenDurationMinutes;
    }

    public void setCircuitBreakerOpenDurationMinutes(int circuitBreakerOpenDurationMinutes) {
        this.circuitBreakerOpenDurationMinutes = circuitBreakerOpenDurationMinutes;
    }

    public int getCircuitBreakerHalfOpenIntervalMinutes() {
        return circuitBreakerHalfOpenIntervalMinutes;
    }

    public void setCircuitBreakerHalfOpenIntervalMinutes(int circuitBreakerHalfOpenIntervalMinutes) {
        this.circuitBreakerHalfOpenIntervalMinutes = circuitBreakerHalfOpenIntervalMinutes;
    }

    public boolean isCircuitBreakerEnabled() {
        return circuitBreakerEnabled;
    }

    public void setCircuitBreakerEnabled(boolean circuitBreakerEnabled) {
        this.circuitBreakerEnabled = circuitBreakerEnabled;
    }

    public boolean isVirtualThreadsEnabled() {
        return virtualThreadsEnabled;
    }

    public void setVirtualThreadsEnabled(boolean virtualThreadsEnabled) {
        this.virtualThreadsEnabled = virtualThreadsEnabled;
    }

    public int getFixedThreadPoolSize() {
        return fixedThreadPoolSize;
    }

    public void setFixedThreadPoolSize(int fixedThreadPoolSize) {
        this.fixedThreadPoolSize = fixedThreadPoolSize;
    }

    public boolean isMetricsEnabled() {
        return metricsEnabled;
    }

    public void setMetricsEnabled(boolean metricsEnabled) {
        this.metricsEnabled = metricsEnabled;
    }

    public int getMetricsIntervalSeconds() {
        return metricsIntervalSeconds;
    }

    public void setMetricsIntervalSeconds(int metricsIntervalSeconds) {
        this.metricsIntervalSeconds = metricsIntervalSeconds;
    }

    public int getSchedulerPoolSize() {
        return schedulerPoolSize;
    }

    public void setSchedulerPoolSize(int schedulerPoolSize) {
        this.schedulerPoolSize = schedulerPoolSize;
    }
}