package io.thatworked.support.notification.api.exception;

import io.thatworked.support.common.dto.ErrorResponse;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.notification.domain.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler for REST API.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private final StructuredLogger logger;
    
    public GlobalExceptionHandler(StructuredLoggerFactory loggerFactory) {
        this.logger = loggerFactory.getLogger(GlobalExceptionHandler.class);
    }
    
    @ExceptionHandler(InvalidNotificationRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidNotificationRequest(InvalidNotificationRequestException ex) {
        logger.with("operation", "handleInvalidNotificationRequest")
                .with("error", ex.getMessage())
                .with("errorCode", ex.getErrorCode())
                .warn("Invalid notification request");
        
        ErrorResponse error = ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Invalid Notification Request")
            .message(ex.getMessage())
            .errorCode("INVALID_NOTIFICATION_REQUEST")
            .build();
        
        return ResponseEntity.badRequest().body(error);
    }
    
    @ExceptionHandler(UnsupportedChannelException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedChannel(UnsupportedChannelException ex) {
        logger.with("operation", "handleUnsupportedChannel")
                .with("channel", ex.getChannel())
                .with("errorCode", ex.getErrorCode())
                .warn("Unsupported notification channel");
        
        ErrorResponse error = ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Unsupported Channel")
            .message(ex.getMessage())
            .errorCode("UNSUPPORTED_CHANNEL")
            .build();
        
        return ResponseEntity.badRequest().body(error);
    }
    
    @ExceptionHandler(NotificationSendException.class)
    public ResponseEntity<ErrorResponse> handleNotificationSendFailure(NotificationSendException ex) {
        logger.with("operation", "handleNotificationSendFailure")
                .with("channel", ex.getChannel())
                .with("recipient", ex.getRecipient())
                .with("error", ex.getMessage())
                .with("errorCode", ex.getErrorCode())
                .error("Notification send failed", ex);
        
        ErrorResponse error = ErrorResponse.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error("Notification Send Failed")
            .message("Failed to send notification: " + ex.getMessage())
            .errorCode("NOTIFICATION_SEND_FAILED")
            .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult()
            .getAllErrors()
            .stream()
            .collect(Collectors.toMap(
                error -> ((FieldError) error).getField(),
                error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value"
            ));
        
        logger.with("operation", "handleValidationExceptions")
                .with("errors", errors)
                .warn("Validation failed");
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("validationErrors", errors);
        
        ErrorResponse error = ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Validation Failed")
            .message("Invalid request parameters")
            .metadata(metadata)
            .build();
        
        return ResponseEntity.badRequest().body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        logger.with("operation", "handleGenericException")
                .with("error", ex.getMessage())
                .with("errorType", ex.getClass().getSimpleName())
                .error("Unexpected error occurred", ex);
        
        ErrorResponse error = ErrorResponse.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error("Internal Server Error")
            .message("An unexpected error occurred")
            .errorCode("INTERNAL_SERVER_ERROR")
            .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}