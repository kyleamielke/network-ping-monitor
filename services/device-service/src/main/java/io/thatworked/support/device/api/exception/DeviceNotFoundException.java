package io.thatworked.support.device.api.exception;

import io.thatworked.support.device.api.exception.base.DeviceServiceException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

/**
 * Exception thrown when a requested device is not found.
 */
public class DeviceNotFoundException extends DeviceServiceException {
    
    private static final String ERROR_CODE = "DEVICE_NOT_FOUND";
    private static final int HTTP_STATUS = HttpStatus.NOT_FOUND.value();
    
    public DeviceNotFoundException(UUID deviceId) {
        super(ERROR_CODE, String.format("Device not found with id: %s", deviceId), HTTP_STATUS);
    }
    
    public DeviceNotFoundException(String field, String value) {
        super(ERROR_CODE, String.format("Device not found with %s: %s", field, value), HTTP_STATUS);
    }
    
    public DeviceNotFoundException(String message) {
        super(ERROR_CODE, message, HTTP_STATUS);
    }
}