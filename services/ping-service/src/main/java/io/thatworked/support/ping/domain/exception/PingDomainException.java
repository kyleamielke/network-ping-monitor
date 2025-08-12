package io.thatworked.support.ping.domain.exception;

/**
 * Base exception for ping domain operations.
 */
public class PingDomainException extends RuntimeException {
    
    public PingDomainException(String message) {
        super(message);
    }
    
    public PingDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}