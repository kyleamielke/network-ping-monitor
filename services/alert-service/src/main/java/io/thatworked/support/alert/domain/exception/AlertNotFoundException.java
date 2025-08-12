package io.thatworked.support.alert.domain.exception;

/**
 * Domain exception thrown when an alert is not found.
 */
public class AlertNotFoundException extends RuntimeException {
    
    public AlertNotFoundException(String message) {
        super(message);
    }
    
    public AlertNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}