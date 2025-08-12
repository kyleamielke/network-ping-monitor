package io.thatworked.support.search.infrastructure.client;

import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Fallback implementation for the report service client.
 */
@Component
public class ReportServiceClientFallback implements ReportServiceClient {
    
    private final StructuredLogger logger;
    
    public ReportServiceClientFallback(StructuredLoggerFactory loggerFactory) {
        this.logger = loggerFactory.getLogger(ReportServiceClientFallback.class);
    }
    
    // Implement fallback methods when ReportServiceClient has methods
}