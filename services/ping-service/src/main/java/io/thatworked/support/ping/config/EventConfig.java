package io.thatworked.support.ping.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration properties for event handling.
 */
@Configuration
@ConfigurationProperties(prefix = "ping-service.events")
public class EventConfig {
    
    private final Map<String, String> types = new HashMap<>();
    private final Map<String, String> fieldNames = new HashMap<>();
    
    public Map<String, String> getTypes() {
        return types;
    }
    
    public Map<String, String> getFieldNames() {
        return fieldNames;
    }
    
    public String getType(String key) {
        return types.get(key);
    }
    
    public String getFieldName(String key) {
        return fieldNames.get(key);
    }
}