package io.thatworked.support.device.api.exception.base;

import lombok.Getter;

/**
 * Base exception for all device service exceptions.
 * Provides common fields for error handling.
 */
@Getter
public abstract class DeviceServiceException extends RuntimeException {
    
    private final String errorCode;
    private final int httpStatus;
    
    protected DeviceServiceException(String errorCode, String message, int httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
    
    protected DeviceServiceException(String errorCode, String message, int httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
}