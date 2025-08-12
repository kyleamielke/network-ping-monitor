package io.thatworked.support.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception for failures in inter-service communication.
 * Used when downstream services are unavailable or return errors.
 */
public class ServiceCommunicationException extends BaseServiceException {
    
    private static final String DEFAULT_ERROR_CODE = "SERVICE_COMMUNICATION_ERROR";
    private static final int DEFAULT_HTTP_STATUS = HttpStatus.SERVICE_UNAVAILABLE.value();
    
    private final String serviceName;
    private final String operation;
    
    public ServiceCommunicationException(String serviceName, String operation) {
        super(DEFAULT_ERROR_CODE, 
              String.format("Failed to communicate with %s service during %s operation", serviceName, operation), 
              DEFAULT_HTTP_STATUS);
        this.serviceName = serviceName;
        this.operation = operation;
    }
    
    public ServiceCommunicationException(String serviceName, String operation, String customMessage) {
        super(DEFAULT_ERROR_CODE, customMessage, DEFAULT_HTTP_STATUS);
        this.serviceName = serviceName;
        this.operation = operation;
    }
    
    public ServiceCommunicationException(String serviceName, String operation, Throwable cause) {
        super(DEFAULT_ERROR_CODE, 
              String.format("Failed to communicate with %s service during %s operation: %s", serviceName, operation, cause.getMessage()), 
              DEFAULT_HTTP_STATUS, cause);
        this.serviceName = serviceName;
        this.operation = operation;
    }
    
    public String getServiceName() {
        return serviceName;
    }
    
    public String getOperation() {
        return operation;
    }
}