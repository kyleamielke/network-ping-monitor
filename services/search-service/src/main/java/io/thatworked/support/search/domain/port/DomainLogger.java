package io.thatworked.support.search.domain.port;

import java.util.Map;

/**
 * Port for domain-level logging.
 * Abstracts logging implementation from domain logic.
 */
public interface DomainLogger {
    
    /**
     * Logs a business event that occurred in the domain.
     */
    void logBusinessEvent(String eventName, Map<String, Object> context);
    
    /**
     * Logs a business warning.
     */
    void logBusinessWarning(String warning, Map<String, Object> context);
    
    /**
     * Logs a business decision made by the domain logic.
     */
    void logBusinessDecision(String decision, Map<String, Object> inputs, Object outcome);
    
    /**
     * Logs a domain state change.
     */
    void logDomainStateChange(String entityType, String entityId, String previousState, String newState, Map<String, Object> additionalContext);
}