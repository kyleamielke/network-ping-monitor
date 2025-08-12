package io.thatworked.support.ping.infrastructure.logger;

import io.thatworked.support.ping.domain.port.DomainLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Infrastructure adapter for domain logging.
 */
@Component
public class DomainLoggerAdapter implements DomainLogger {
    
    private final io.thatworked.support.common.logging.StructuredLogger logger;
    
    public DomainLoggerAdapter(StructuredLoggerFactory structuredLoggerFactory) {
        this.logger = structuredLoggerFactory.getLogger(DomainLoggerAdapter.class);
    }
    
    @Override
    public void logBusinessEvent(String eventName, Map<String, Object> context) {
        logger.with("businessEvent", eventName)
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
              .warn("Business warning occurred");
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
    public void logDomainStateChange(String entityType, String entityId, String previousState, 
                                   String newState, Map<String, Object> additionalContext) {
        logger.with("entityType", entityType)
              .with("entityId", entityId)
              .with("previousState", previousState)
              .with("newState", newState)
              .with("eventType", "domain-state-change")
              .with("layer", "domain")
              .with("additionalContext", additionalContext)
              .info("Domain state change occurred");
    }
}