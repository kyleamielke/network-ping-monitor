package io.thatworked.support.gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Base configuration for the API Gateway.
 * Provides common beans used throughout the application.
 */
@Configuration
public class BaseConfig {
    
    @Bean
    public StructuredLoggerFactory structuredLoggerFactory() {
        return new StructuredLoggerFactory();
    }
    
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}