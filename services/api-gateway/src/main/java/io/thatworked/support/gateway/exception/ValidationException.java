package io.thatworked.support.gateway.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * Exception thrown when input validation fails.
 * Provides field-specific error details for GraphQL responses.
 */
public class ValidationException extends RuntimeException {
    
    private final Map<String, String> fieldErrors;
    
    public ValidationException(String message) {
        super(message);
        this.fieldErrors = new HashMap<>();
    }
    
    public ValidationException(String message, Map<String, String> fieldErrors) {
        super(message);
        this.fieldErrors = fieldErrors != null ? fieldErrors : new HashMap<>();
    }
    
    public ValidationException(String field, String error) {
        super(String.format("Validation failed for field '%s': %s", field, error));
        this.fieldErrors = new HashMap<>();
        this.fieldErrors.put(field, error);
    }
    
    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }
    
    public void addFieldError(String field, String error) {
        fieldErrors.put(field, error);
    }
    
    public boolean hasFieldErrors() {
        return !fieldErrors.isEmpty();
    }
}