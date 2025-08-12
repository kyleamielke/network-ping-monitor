package io.thatworked.support.notification.infrastructure.adapter;

import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.notification.domain.port.DomainLogger;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Implementation of DomainLogger using StructuredLogger from common module.
 * This adapter bridges the domain logging interface with the infrastructure logging framework.
 * 
 * This follows the Hexagonal Architecture pattern where infrastructure adapters
 * implement domain ports.
 */
@Component
public class StructuredDomainLogger implements DomainLogger {
    
    private final StructuredLogger logger;
    
    public StructuredDomainLogger(StructuredLoggerFactory loggerFactory) {
        this.logger = loggerFactory.getLogger(StructuredDomainLogger.class);
    }
    
    @Override
    public void logBusinessEvent(String event, Map<String, Object> context) {
        logger.with("businessEvent", event)
              .with("eventType", "business-event")
              .with("layer", "domain")
              .with("context", context)
              .info("Business event occurred");
    }
    
    @Override
    public void logBusinessWarning(String warning, Map<String, Object> context) {
        logger.with("businessWarning", warning)
              .with("eventType", "business-warning")
              .with("layer", "domain")
              .with("context", context)
              .warn("Business warning detected");
    }
    
    @Override
    public void logBusinessDecision(String decision, Map<String, Object> inputs, Object outcome) {
        logger.with("businessDecision", decision)
              .with("eventType", "business-decision")
              .with("layer", "domain")
              .with("inputs", inputs)
              .with("outcome", outcome)
              .info("Business decision made");
    }
    
    @Override
    public void logDomainStateChange(String entity, String entityId, String fromState, String toState, Map<String, Object> context) {
        logger.with("entity", entity)
              .with("entityId", entityId)
              .with("eventType", "state-change")
              .with("layer", "domain")
              .with("fromState", fromState)
              .with("toState", toState)
              .with("context", context)
              .info("Domain entity state changed");
    }
}