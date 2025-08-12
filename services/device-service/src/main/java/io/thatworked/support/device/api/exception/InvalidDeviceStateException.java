package io.thatworked.support.device.api.exception;

import io.thatworked.support.device.api.exception.base.DeviceServiceException;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a device operation is invalid for the current device state.
 */
public class InvalidDeviceStateException extends DeviceServiceException {
    
    private static final String ERROR_CODE = "INVALID_DEVICE_STATE";
    private static final int HTTP_STATUS = HttpStatus.UNPROCESSABLE_ENTITY.value();
    
    public InvalidDeviceStateException(String currentState, String operation) {
        super(ERROR_CODE, 
            String.format("Cannot perform operation '%s' on device in state '%s'", operation, currentState), 
            HTTP_STATUS);
    }
    
    public InvalidDeviceStateException(String message) {
        super(ERROR_CODE, message, HTTP_STATUS);
    }
}