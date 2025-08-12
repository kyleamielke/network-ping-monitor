package io.thatworked.support.ping.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for ping business rules.
 */
@Configuration
@ConfigurationProperties(prefix = "ping.business-rules")
public class BusinessRulesConfig {
    
    private int defaultPingIntervalSeconds = 5;
    private int minPingIntervalSeconds = 1;
    private int maxPingIntervalSeconds = 3600;
    private int defaultTimeoutMs = 5000;
    private int maxConcurrentTargets = 1000;
    private int cleanupRetentionDays = 30;

    public int getDefaultPingIntervalSeconds() {
        return defaultPingIntervalSeconds;
    }

    public void setDefaultPingIntervalSeconds(int defaultPingIntervalSeconds) {
        this.defaultPingIntervalSeconds = defaultPingIntervalSeconds;
    }

    public int getMinPingIntervalSeconds() {
        return minPingIntervalSeconds;
    }

    public void setMinPingIntervalSeconds(int minPingIntervalSeconds) {
        this.minPingIntervalSeconds = minPingIntervalSeconds;
    }

    public int getMaxPingIntervalSeconds() {
        return maxPingIntervalSeconds;
    }

    public void setMaxPingIntervalSeconds(int maxPingIntervalSeconds) {
        this.maxPingIntervalSeconds = maxPingIntervalSeconds;
    }

    public int getDefaultTimeoutMs() {
        return defaultTimeoutMs;
    }

    public void setDefaultTimeoutMs(int defaultTimeoutMs) {
        this.defaultTimeoutMs = defaultTimeoutMs;
    }

    public int getMaxConcurrentTargets() {
        return maxConcurrentTargets;
    }

    public void setMaxConcurrentTargets(int maxConcurrentTargets) {
        this.maxConcurrentTargets = maxConcurrentTargets;
    }

    public int getCleanupRetentionDays() {
        return cleanupRetentionDays;
    }

    public void setCleanupRetentionDays(int cleanupRetentionDays) {
        this.cleanupRetentionDays = cleanupRetentionDays;
    }
}