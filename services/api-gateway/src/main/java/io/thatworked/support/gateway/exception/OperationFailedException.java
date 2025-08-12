package io.thatworked.support.gateway.exception;

/**
 * Exception thrown when an operation fails with details about the failure.
 */
public class OperationFailedException extends RuntimeException {
    
    private final String operation;
    private final String reason;
    
    public OperationFailedException(String operation, String reason) {
        super(String.format("Operation '%s' failed: %s", operation, reason));
        this.operation = operation;
        this.reason = reason;
    }
    
    public OperationFailedException(String operation, String reason, Throwable cause) {
        super(String.format("Operation '%s' failed: %s", operation, reason), cause);
        this.operation = operation;
        this.reason = reason;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public String getReason() {
        return reason;
    }
}