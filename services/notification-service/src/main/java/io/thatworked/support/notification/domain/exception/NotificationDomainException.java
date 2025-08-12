package io.thatworked.support.notification.domain.exception;

/**
 * Base exception for all notification domain exceptions.
 * Pure domain exception with no framework dependencies.
 */
public abstract class NotificationDomainException extends RuntimeException {
    
    private final String errorCode;
    
    protected NotificationDomainException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    protected NotificationDomainException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}