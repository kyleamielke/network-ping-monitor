package io.thatworked.support.report.domain.port;

import java.util.Map;

/**
 * Domain port for business-oriented logging.
 * Pure interface with no framework dependencies.
 * Focused on business event logging, not technical logging.
 */
public interface DomainLogger {
    
    /**
     * Log a business event.
     * @param event the event description
     * @param context additional context data
     */
    void logBusinessEvent(String event, Map<String, Object> context);
    
    /**
     * Log a business warning.
     * @param warning the warning description
     * @param context additional context data
     */
    void logBusinessWarning(String warning, Map<String, Object> context);
    
    /**
     * Log a business decision.
     * @param decision the decision made
     * @param inputs the inputs to the decision
     * @param outcome the outcome of the decision
     */
    void logBusinessDecision(String decision, Map<String, Object> inputs, Object outcome);
    
    /**
     * Log a domain state change.
     * @param entity the entity type
     * @param entityId the entity ID
     * @param fromState the previous state
     * @param toState the new state
     * @param context additional context
     */
    void logDomainStateChange(String entity, String entityId, String fromState, String toState, Map<String, Object> context);
}