package io.thatworked.support.device.infrastructure.exception;

import io.thatworked.support.device.api.exception.base.DeviceServiceException;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when publishing device events fails.
 * Note: This should typically not fail the main operation.
 */
public class DeviceEventPublishException extends DeviceServiceException {
    
    private static final String ERROR_CODE = "DEVICE_EVENT_PUBLISH_FAILED";
    private static final int HTTP_STATUS = HttpStatus.INTERNAL_SERVER_ERROR.value();
    
    public DeviceEventPublishException(String eventType, Throwable cause) {
        super(ERROR_CODE, 
            String.format("Failed to publish device event of type '%s'", eventType), 
            HTTP_STATUS, 
            cause);
    }
}