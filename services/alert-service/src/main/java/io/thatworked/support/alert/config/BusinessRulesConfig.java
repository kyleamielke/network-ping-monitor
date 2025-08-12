package io.thatworked.support.alert.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for business rules.
 */
@Configuration
@ConfigurationProperties(prefix = "alert-service.rules")
public class BusinessRulesConfig {
    
    private final Lifecycle lifecycle = new Lifecycle();
    
    public Lifecycle getLifecycle() {
        return lifecycle;
    }
    
    public static class Lifecycle {
        private int maxActiveAlertsPerDevice = 10;
        private int retentionPeriodDays = 30;
        
        public int getMaxActiveAlertsPerDevice() {
            return maxActiveAlertsPerDevice;
        }
        
        public void setMaxActiveAlertsPerDevice(int maxActiveAlertsPerDevice) {
            this.maxActiveAlertsPerDevice = maxActiveAlertsPerDevice;
        }
        
        public int getRetentionPeriodDays() {
            return retentionPeriodDays;
        }
        
        public void setRetentionPeriodDays(int retentionPeriodDays) {
            this.retentionPeriodDays = retentionPeriodDays;
        }
    }
}