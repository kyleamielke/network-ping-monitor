package io.thatworked.support.common.logging.correlation;

/**
 * Constants for correlation ID handling across services
 */
public class CorrelationIdConstants {
    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    public static final String CORRELATION_ID_MDC_KEY = "correlationId";
    
    private CorrelationIdConstants() {
        // Utility class
    }
}