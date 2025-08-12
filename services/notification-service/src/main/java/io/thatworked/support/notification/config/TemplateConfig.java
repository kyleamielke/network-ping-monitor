package io.thatworked.support.notification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for template mappings.
 * Maps notification types to template names.
 */
@Configuration
@ConfigurationProperties(prefix = "notification-service.templates")
public class TemplateConfig {
    
    private Map<String, String> mappings = new HashMap<>();
    
    public Map<String, String> getMappings() {
        return mappings;
    }
    
    public void setMappings(Map<String, String> mappings) {
        this.mappings = mappings;
    }
    
    public String getTemplateForType(String type) {
        return mappings.get(type.toLowerCase().replace("_", "-"));
    }
}