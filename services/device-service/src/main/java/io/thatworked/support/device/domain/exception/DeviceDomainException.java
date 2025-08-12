package io.thatworked.support.device.domain.exception;

/**
 * Base exception for all device domain exceptions.
 * Pure domain exception with no framework dependencies.
 */
public abstract class DeviceDomainException extends RuntimeException {
    
    private final String errorCode;
    
    protected DeviceDomainException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    protected DeviceDomainException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}