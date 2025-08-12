package io.thatworked.support.common.exception;

import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

/**
 * Common exception for validation failures.
 * Supports both single field validation errors and multiple field validation errors.
 */
public class ValidationException extends BaseServiceException {
    
    private static final String DEFAULT_ERROR_CODE = "VALIDATION_FAILED";
    private static final int DEFAULT_HTTP_STATUS = HttpStatus.BAD_REQUEST.value();
    
    private final String field;
    private final Map<String, String> fieldErrors;
    
    public ValidationException(String message) {
        super(DEFAULT_ERROR_CODE, message, DEFAULT_HTTP_STATUS);
        this.field = null;
        this.fieldErrors = null;
    }
    
    public ValidationException(String field, String message) {
        super(DEFAULT_ERROR_CODE, 
              String.format("Validation failed for field '%s': %s", field, message), 
              DEFAULT_HTTP_STATUS);
        this.field = field;
        this.fieldErrors = null;
    }
    
    public ValidationException(Map<String, String> fieldErrors) {
        super(DEFAULT_ERROR_CODE, 
              String.format("Validation failed for %d field(s)", fieldErrors.size()), 
              DEFAULT_HTTP_STATUS);
        this.field = null;
        this.fieldErrors = fieldErrors;
    }
    
    public ValidationException(String message, Throwable cause) {
        super(DEFAULT_ERROR_CODE, message, DEFAULT_HTTP_STATUS, cause);
        this.field = null;
        this.fieldErrors = null;
    }
    
    public String getField() {
        return field;
    }
    
    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }
    
    public boolean hasFieldErrors() {
        return fieldErrors != null && !fieldErrors.isEmpty();
    }
    
    public boolean hasSingleFieldError() {
        return field != null;
    }
}