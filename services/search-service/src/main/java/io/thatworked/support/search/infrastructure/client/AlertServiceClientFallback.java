package io.thatworked.support.search.infrastructure.client;

import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Fallback implementation for the alert service client.
 */
@Component
public class AlertServiceClientFallback implements AlertServiceClient {
    
    private final StructuredLogger logger;
    
    public AlertServiceClientFallback(StructuredLoggerFactory loggerFactory) {
        this.logger = loggerFactory.getLogger(AlertServiceClientFallback.class);
    }
    
    // Implement fallback methods when AlertServiceClient has methods
}