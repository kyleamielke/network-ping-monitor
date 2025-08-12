package io.thatworked.support.alert.domain.exception;

/**
 * Base exception for all alert domain exceptions.
 */
public class AlertDomainException extends RuntimeException {
    
    public AlertDomainException(String message) {
        super(message);
    }
    
    public AlertDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}