package io.thatworked.support.common.logging.factory;

import io.thatworked.support.common.logging.StructuredLogger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Factory for creating structured loggers
 */
@Component
public class StructuredLoggerFactory {
    
    @Value("${spring.application.name:unknown-service}")
    private String serviceName;
    
    public StructuredLogger getLogger(Class<?> clazz) {
        return StructuredLogger.getLogger(clazz, serviceName);
    }
}