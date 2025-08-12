package io.thatworked.support.search.infrastructure.adapter;

import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.search.domain.port.DomainLogger;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Adapter that implements the domain DomainLogger port using StructuredLogger.
 * Uses dependency injection for logging following Clean Architecture principles.
 */
@Component
public class DomainLoggerAdapter implements DomainLogger {
    
    private final StructuredLogger logger;
    
    public DomainLoggerAdapter(StructuredLoggerFactory loggerFactory) {
        this.logger = loggerFactory.getLogger(DomainLoggerAdapter.class);
    }
    
    @Override
    public void logBusinessEvent(String eventName, Map<String, Object> context) {
        logger.with("businessEvent", eventName)
              .with("context", context)
              .info("Business event occurred");
    }
    
    @Override
    public void logBusinessWarning(String warning, Map<String, Object> context) {
        logger.with("businessWarning", warning)
              .with("context", context)
              .warn("Business warning occurred");
    }
    
    @Override
    public void logBusinessDecision(String decision, Map<String, Object> inputs, Object outcome) {
        logger.with("businessDecision", decision)
              .with("inputs", inputs)
              .with("outcome", outcome)
              .info("Business decision made");
    }
    
    @Override
    public void logDomainStateChange(String entityType, String entityId, String previousState, String newState, Map<String, Object> additionalContext) {
        logger.with("entityType", entityType)
              .with("entityId", entityId)
              .with("previousState", previousState)
              .with("newState", newState)
              .with("context", additionalContext)
              .info("Domain state changed");
    }
}