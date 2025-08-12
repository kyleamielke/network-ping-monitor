package io.thatworked.support.alert.api.exception;

import io.thatworked.support.alert.config.MessagesConfig;
import io.thatworked.support.alert.config.ServiceConfig;
import io.thatworked.support.alert.domain.exception.AlertNotFoundException;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import jakarta.persistence.OptimisticLockException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    private final StructuredLogger logger;
    private final MessagesConfig messagesConfig;
    private final ServiceConfig serviceConfig;
    
    public GlobalExceptionHandler(StructuredLoggerFactory loggerFactory, MessagesConfig messagesConfig, ServiceConfig serviceConfig) {
        this.logger = loggerFactory.getLogger(GlobalExceptionHandler.class);
        this.messagesConfig = messagesConfig;
        this.serviceConfig = serviceConfig;
    }

    @ExceptionHandler(AlertNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAlertNotFoundException(AlertNotFoundException ex) {
        logger.with("message", ex.getMessage())
              .warn("Alert not found");
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error(messagesConfig.getApi().getAlertNotFoundTitle())
            .message(ex.getMessage())
            .path(serviceConfig.getApi().getBasePath())
            .build();
            
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        logger.with("message", ex.getMessage())
              .warn("Validation failed");
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ValidationErrorResponse errorResponse = ValidationErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error(messagesConfig.getApi().getValidationFailedTitle())
            .message(messagesConfig.getErrors().getValidationFailed())
            .validationErrors(errors)
            .path(serviceConfig.getApi().getBasePath())
            .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler({OptimisticLockException.class, ObjectOptimisticLockingFailureException.class})
    public ResponseEntity<ErrorResponse> handleOptimisticLockingException(Exception ex) {
        logger.with("operation", "handleOptimisticLockingException")
              .with("exceptionType", ex.getClass().getSimpleName())
              .with("message", ex.getMessage())
              .warn("Optimistic locking conflict detected");
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.CONFLICT.value())
            .error("Optimistic Lock Conflict")
            .message("The resource was modified by another process. Please refresh and try again.")
            .path(serviceConfig.getApi().getBasePath())
            .build();
            
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        logger.with("operation", "handleGenericException")
              .with("exceptionType", ex.getClass().getSimpleName())
              .with("message", ex.getMessage())
              .error("Unexpected error occurred", ex);
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error(messagesConfig.getApi().getInternalErrorTitle())
            .message(messagesConfig.getErrors().getInternalServerError())
            .path(serviceConfig.getApi().getBasePath())
            .build();
            
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @lombok.Data
    @lombok.Builder
    public static class ErrorResponse {
        private Instant timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
    }

    @lombok.Data
    @lombok.Builder
    public static class ValidationErrorResponse {
        private Instant timestamp;
        private int status;
        private String error;
        private String message;
        private Map<String, String> validationErrors;
        private String path;
    }
}