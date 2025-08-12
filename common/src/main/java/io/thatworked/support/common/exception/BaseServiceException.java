package io.thatworked.support.common.exception;

/**
 * Base exception class for all service-level exceptions.
 * Provides common functionality for error codes, HTTP status, and correlation tracking.
 */
public abstract class BaseServiceException extends RuntimeException {
    
    private final String errorCode;
    private final int httpStatus;
    private final String correlationId;
    
    protected BaseServiceException(String errorCode, String message, int httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.correlationId = null; // Can be set by logging framework
    }
    
    protected BaseServiceException(String errorCode, String message, int httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.correlationId = null;
    }
    
    protected BaseServiceException(String errorCode, String message, int httpStatus, String correlationId) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.correlationId = correlationId;
    }
    
    protected BaseServiceException(String errorCode, String message, int httpStatus, String correlationId, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.correlationId = correlationId;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public int getHttpStatus() {
        return httpStatus;
    }
    
    public String getCorrelationId() {
        return correlationId;
    }
}