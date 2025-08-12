package io.thatworked.support.notification.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;

/**
 * Configuration for date/time formatting.
 * Provides beans for consistent formatting across the service.
 */
@Configuration
public class FormattingConfig {
    
    @Value("${notification-service.formatting.datetime.timestamp-pattern:yyyy-MM-dd HH:mm:ss}")
    private String timestampPattern;
    
    @Bean
    public DateTimeFormatter dateTimeFormatter() {
        return DateTimeFormatter.ofPattern(timestampPattern);
    }
}