package io.thatworked.support.device.api.exception;

import io.thatworked.support.device.api.exception.base.DeviceServiceException;
import org.springframework.http.HttpStatus;

import java.util.Map;

/**
 * Exception thrown when device validation fails.
 */
public class DeviceValidationException extends DeviceServiceException {
    
    private static final String ERROR_CODE = "DEVICE_VALIDATION_ERROR";
    private static final int HTTP_STATUS = HttpStatus.BAD_REQUEST.value();
    
    private final Map<String, String> fieldErrors;
    
    public DeviceValidationException(String message) {
        super(ERROR_CODE, message, HTTP_STATUS);
        this.fieldErrors = null;
    }
    
    public DeviceValidationException(String field, String message) {
        super(ERROR_CODE, String.format("Validation failed for field '%s': %s", field, message), HTTP_STATUS);
        this.fieldErrors = Map.of(field, message);
    }
    
    public DeviceValidationException(Map<String, String> fieldErrors) {
        super(ERROR_CODE, "Validation failed for multiple fields", HTTP_STATUS);
        this.fieldErrors = fieldErrors;
    }
    
    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }
}