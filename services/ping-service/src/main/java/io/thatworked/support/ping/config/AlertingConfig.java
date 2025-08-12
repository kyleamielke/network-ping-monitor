package io.thatworked.support.ping.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for alerting behavior.
 */
@Configuration
@ConfigurationProperties(prefix = "ping.alerting")
public class AlertingConfig {
    
    private boolean enabled = true;
    private int failureThreshold = 3;
    private int recoveryThreshold = 2;
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public int getFailureThreshold() {
        return failureThreshold;
    }
    
    public void setFailureThreshold(int failureThreshold) {
        this.failureThreshold = failureThreshold;
    }
    
    public int getRecoveryThreshold() {
        return recoveryThreshold;
    }
    
    public void setRecoveryThreshold(int recoveryThreshold) {
        this.recoveryThreshold = recoveryThreshold;
    }
}