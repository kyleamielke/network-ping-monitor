package io.thatworked.support.device.api.exception;

import io.thatworked.support.device.api.exception.base.DeviceServiceException;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a device operation fails.
 */
public class DeviceOperationException extends DeviceServiceException {
    
    private static final String ERROR_CODE = "DEVICE_OPERATION_FAILED";
    private static final int HTTP_STATUS = HttpStatus.INTERNAL_SERVER_ERROR.value();
    
    public DeviceOperationException(String operation, String reason) {
        super(ERROR_CODE, 
            String.format("Device operation '%s' failed: %s", operation, reason), 
            HTTP_STATUS);
    }
    
    public DeviceOperationException(String operation, Throwable cause) {
        super(ERROR_CODE, 
            String.format("Device operation '%s' failed", operation), 
            HTTP_STATUS, 
            cause);
    }
}