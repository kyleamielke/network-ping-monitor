package io.thatworked.support.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * Standard error response structure for all API errors across microservices.
 * Provides consistent error information and format for client applications.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    
    /**
     * Timestamp when the error occurred (always in UTC).
     */
    @Builder.Default
    private Instant timestamp = Instant.now();
    
    /**
     * HTTP status code.
     */
    private int status;
    
    /**
     * Standard HTTP error name (e.g., "Bad Request", "Not Found").
     */
    private String error;
    
    /**
     * Human-readable error message for end users.
     */
    private String message;
    
    /**
     * API path where the error occurred.
     */
    private String path;
    
    /**
     * Unique error code for categorizing the error type.
     * Example: "DEVICE_NOT_FOUND", "VALIDATION_ERROR"
     */
    private String errorCode;
    
    /**
     * Additional details about the error.
     * May include technical information for debugging.
     */
    private String details;
    
    /**
     * Correlation ID for request tracking across services.
     */
    private String correlationId;
    
    /**
     * Field-specific validation errors or additional metadata.
     * Common keys: "field", "validationErrors", "stackTrace" (dev mode only)
     */
    private Map<String, Object> metadata;
    
    /**
     * Factory method for creating a basic error response.
     */
    public static ErrorResponse of(int status, String error, String message) {
        return ErrorResponse.builder()
            .status(status)
            .error(error)
            .message(message)
            .build();
    }
    
    /**
     * Factory method for creating an error response with path.
     */
    public static ErrorResponse of(int status, String error, String message, String path) {
        return ErrorResponse.builder()
            .status(status)
            .error(error)
            .message(message)
            .path(path)
            .build();
    }
    
    /**
     * Factory method for creating an error response with error code.
     */
    public static ErrorResponse of(String errorCode, String message, int status) {
        return ErrorResponse.builder()
            .errorCode(errorCode)
            .message(message)
            .status(status)
            .build();
    }
    
    /**
     * Factory method for creating a detailed error response.
     */
    public static ErrorResponse of(String errorCode, String message, String details, int status, String path) {
        return ErrorResponse.builder()
            .errorCode(errorCode)
            .message(message)
            .details(details)
            .status(status)
            .path(path)
            .build();
    }
}