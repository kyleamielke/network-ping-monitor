package io.thatworked.support.ping.domain.port;

import java.util.Map;

/**
 * Domain port for structured logging.
 */
public interface DomainLogger {
    
    void logBusinessEvent(String eventName, Map<String, Object> context);
    
    void logBusinessWarning(String warning, Map<String, Object> context);
    
    void logBusinessDecision(String decision, Map<String, Object> inputs, Object outcome);
    
    void logDomainStateChange(String entityType, String entityId, String previousState, String newState, Map<String, Object> additionalContext);
}