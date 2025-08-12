package io.thatworked.support.notification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration properties for event handling.
 */
@Configuration
@ConfigurationProperties(prefix = "notification-service.events")
public class EventConfig {
    
    private final Map<String, String> types = new HashMap<>();
    private final Map<String, String> subjects = new HashMap<>();
    private final Map<String, String> messages = new HashMap<>();
    private final Map<String, String> fieldNames = new HashMap<>();
    private final Map<String, String> channels = new HashMap<>();
    
    public Map<String, String> getTypes() {
        return types;
    }
    
    public Map<String, String> getSubjects() {
        return subjects;
    }
    
    public Map<String, String> getMessages() {
        return messages;
    }
    
    public Map<String, String> getFieldNames() {
        return fieldNames;
    }
    
    public Map<String, String> getChannels() {
        return channels;
    }
    
    public String getType(String key) {
        return types.get(key);
    }
    
    public String getSubject(String key) {
        return subjects.get(key);
    }
    
    public String getMessage(String key) {
        return messages.get(key);
    }
    
    public String getFieldName(String key) {
        return fieldNames.get(key);
    }
}