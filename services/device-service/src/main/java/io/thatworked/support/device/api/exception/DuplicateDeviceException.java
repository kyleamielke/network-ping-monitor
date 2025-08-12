package io.thatworked.support.device.api.exception;

import io.thatworked.support.device.api.exception.base.DeviceServiceException;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when attempting to create a device that violates uniqueness constraints.
 */
public class DuplicateDeviceException extends DeviceServiceException {
    
    private static final String ERROR_CODE = "DUPLICATE_DEVICE";
    private static final int HTTP_STATUS = HttpStatus.CONFLICT.value();
    
    public DuplicateDeviceException(String field, String value) {
        super(ERROR_CODE, 
            String.format("Device with %s '%s' already exists", field, value), 
            HTTP_STATUS);
    }
    
    public DuplicateDeviceException(String message) {
        super(ERROR_CODE, message, HTTP_STATUS);
    }
}