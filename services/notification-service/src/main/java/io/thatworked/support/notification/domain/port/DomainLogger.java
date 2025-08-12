package io.thatworked.support.notification.domain.port;

import java.util.Map;

/**
 * Domain port for business-oriented logging.
 * Pure interface with no framework dependencies.
 */
public interface DomainLogger {
    
    /**
     * Log a business event.
     * 
     * @param event The business event that occurred
     * @param context Additional context for the event
     */
    void logBusinessEvent(String event, Map<String, Object> context);
    
    /**
     * Log a business warning.
     * 
     * @param warning The business warning message
     * @param context Additional context for the warning
     */
    void logBusinessWarning(String warning, Map<String, Object> context);
    
    /**
     * Log a business decision.
     * 
     * @param decision The decision that was made
     * @param inputs The inputs that led to the decision
     * @param outcome The outcome of the decision
     */
    void logBusinessDecision(String decision, Map<String, Object> inputs, Object outcome);
    
    /**
     * Log a domain state change.
     * 
     * @param entity The entity type that changed
     * @param entityId The ID of the entity
     * @param fromState The previous state
     * @param toState The new state
     * @param context Additional context for the state change
     */
    void logDomainStateChange(String entity, String entityId, String fromState, String toState, Map<String, Object> context);
}