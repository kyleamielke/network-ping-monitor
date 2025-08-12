package io.thatworked.support.device.api.exception.handler;

import io.thatworked.support.common.dto.ErrorResponse;
import io.thatworked.support.device.api.exception.*;
import io.thatworked.support.device.api.exception.base.DeviceServiceException;
import io.thatworked.support.common.exception.EntityNotFoundException;
import io.thatworked.support.device.infrastructure.exception.DeviceEventPublishException;
import io.thatworked.support.device.domain.exception.OptimisticLockingDomainException;
import io.thatworked.support.device.domain.exception.DuplicateDeviceDomainException;
import io.thatworked.support.device.domain.exception.DeviceNotFoundDomainException;
import org.slf4j.MDC;
import io.thatworked.support.common.logging.correlation.CorrelationIdConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.persistence.OptimisticLockException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final StructuredLogger logger;
    
    public GlobalExceptionHandler(StructuredLoggerFactory loggerFactory) {
        this.logger = loggerFactory.getLogger(GlobalExceptionHandler.class);
    }

    // Handle specific device exceptions first
    @ExceptionHandler(DeviceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDeviceNotFoundException(
            DeviceNotFoundException e, HttpServletRequest request) {
        logger.with("errorCode", e.getErrorCode())
                .with("httpStatus", e.getHttpStatus())
                .with("requestPath", request.getRequestURI())
                .error("Device not found", e);
        
        ErrorResponse error = ErrorResponse.builder()
            .errorCode(e.getErrorCode())
            .message(e.getMessage())
            .status(e.getHttpStatus())
            .path(request.getRequestURI())
            .correlationId(MDC.get(CorrelationIdConstants.CORRELATION_ID_MDC_KEY))
            .build();
            
        return ResponseEntity.status(e.getHttpStatus()).body(error);
    }
    
    @ExceptionHandler(DuplicateDeviceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateDeviceException(
            DuplicateDeviceException e, HttpServletRequest request) {
        logger.with("errorCode", e.getErrorCode())
                .with("httpStatus", e.getHttpStatus())
                .with("requestPath", request.getRequestURI())
                .error("Duplicate device", e);
        
        ErrorResponse error = ErrorResponse.builder()
            .errorCode(e.getErrorCode())
            .message(e.getMessage())
            .details("A device with the same unique identifier already exists")
            .status(e.getHttpStatus())
            .path(request.getRequestURI())
            .correlationId(MDC.get(CorrelationIdConstants.CORRELATION_ID_MDC_KEY))
            .build();
            
        return ResponseEntity.status(e.getHttpStatus()).body(error);
    }
    
    @ExceptionHandler(DeviceValidationException.class)
    public ResponseEntity<ErrorResponse> handleDeviceValidationException(
            DeviceValidationException e, HttpServletRequest request) {
        logger.with("errorCode", e.getErrorCode())
                .with("httpStatus", e.getHttpStatus())
                .with("requestPath", request.getRequestURI())
                .with("fieldErrors", e.getFieldErrors())
                .error("Device validation failed", e);
        
        ErrorResponse error = ErrorResponse.builder()
            .errorCode(e.getErrorCode())
            .message(e.getMessage())
            .status(e.getHttpStatus())
            .path(request.getRequestURI())
            .correlationId(MDC.get(CorrelationIdConstants.CORRELATION_ID_MDC_KEY))
            .build();
            
        if (e.getFieldErrors() != null && !e.getFieldErrors().isEmpty()) {
            error.setMetadata(formatFieldErrors(e.getFieldErrors()));
        }
        
        return ResponseEntity.status(e.getHttpStatus()).body(error);
    }
    
    @ExceptionHandler(InvalidDeviceStateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDeviceStateException(
            InvalidDeviceStateException e, HttpServletRequest request) {
        logger.with("errorCode", e.getErrorCode())
                .with("httpStatus", e.getHttpStatus())
                .with("requestPath", request.getRequestURI())
                .error("Invalid device state", e);
        
        ErrorResponse error = ErrorResponse.builder()
            .errorCode(e.getErrorCode())
            .message(e.getMessage())
            .details("The requested operation cannot be performed in the current device state")
            .status(e.getHttpStatus())
            .path(request.getRequestURI())
            .correlationId(MDC.get(CorrelationIdConstants.CORRELATION_ID_MDC_KEY))
            .build();
            
        return ResponseEntity.status(e.getHttpStatus()).body(error);
    }
    
    @ExceptionHandler(DeviceOperationException.class)
    public ResponseEntity<ErrorResponse> handleDeviceOperationException(
            DeviceOperationException e, HttpServletRequest request) {
        logger.with("errorCode", e.getErrorCode())
                .with("httpStatus", e.getHttpStatus())
                .with("requestPath", request.getRequestURI())
                .error("Device operation failed", e);
        
        ErrorResponse error = ErrorResponse.builder()
            .errorCode(e.getErrorCode())
            .message(e.getMessage())
            .status(e.getHttpStatus())
            .path(request.getRequestURI())
            .correlationId(MDC.get(CorrelationIdConstants.CORRELATION_ID_MDC_KEY))
            .build();
            
        return ResponseEntity.status(e.getHttpStatus()).body(error);
    }
    
    @ExceptionHandler(DeviceEventPublishException.class)
    public ResponseEntity<ErrorResponse> handleDeviceEventPublishException(
            DeviceEventPublishException e, HttpServletRequest request) {
        // Event publishing failures should not fail the request
        logger.with("errorCode", e.getErrorCode())
                .with("httpStatus", e.getHttpStatus())
                .with("requestPath", request.getRequestURI())
                .with("exception", e.getMessage())
                .warn("Device event publish failed");
        
        // Return success since the main operation succeeded
        // This is logged but doesn't fail the request
        return null; // This exception should be caught internally
    }
    
    // Handle all other DeviceServiceException subclasses
    @ExceptionHandler(DeviceServiceException.class)
    public ResponseEntity<ErrorResponse> handleDeviceServiceException(
            DeviceServiceException e, HttpServletRequest request) {
        logger.with("errorCode", e.getErrorCode())
                .with("httpStatus", e.getHttpStatus())
                .with("requestPath", request.getRequestURI())
                .error("Device service exception", e);
        
        ErrorResponse error = ErrorResponse.builder()
            .errorCode(e.getErrorCode())
            .message(e.getMessage())
            .status(e.getHttpStatus())
            .path(request.getRequestURI())
            .correlationId(MDC.get(CorrelationIdConstants.CORRELATION_ID_MDC_KEY))
            .build();
            
        return ResponseEntity.status(e.getHttpStatus()).body(error);
    }

    // Handle common EntityNotFoundException
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(
            EntityNotFoundException e, HttpServletRequest request) {
        logger.with("entityType", e.getEntityType())
                .with("entityId", e.getEntityId())
                .with("requestPath", request.getRequestURI())
                .error("Entity not found", e);
        
        ErrorResponse error = ErrorResponse.builder()
            .errorCode(e.getErrorCode())
            .message(e.getMessage())
            .status(e.getHttpStatus())
            .path(request.getRequestURI())
            .correlationId(MDC.get(CorrelationIdConstants.CORRELATION_ID_MDC_KEY))
            .build();
        
        return ResponseEntity.status(e.getHttpStatus()).body(error);
    }

    // Handle validation errors from @Valid annotations
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException e, HttpServletRequest request) {
        logger.with("requestPath", request.getRequestURI())
                .with("validationErrors", e.getBindingResult().getFieldErrors().size())
                .error("Validation error", e);
        
        Map<String, String> fieldErrors = e.getBindingResult()
            .getFieldErrors()
            .stream()
            .collect(Collectors.toMap(
                FieldError::getField,
                FieldError::getDefaultMessage,
                (existing, replacement) -> existing
            ));
        
        ErrorResponse error = ErrorResponse.builder()
            .errorCode("VALIDATION_ERROR")
            .message("Validation failed for one or more fields")
            .status(HttpStatus.BAD_REQUEST.value())
            .path(request.getRequestURI())
            .correlationId(MDC.get(CorrelationIdConstants.CORRELATION_ID_MDC_KEY))
            .metadata(formatFieldErrors(fieldErrors))
            .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Handle constraint violations
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException e, HttpServletRequest request) {
        logger.with("requestPath", request.getRequestURI())
                .with("violationCount", e.getConstraintViolations().size())
                .error("Constraint violation", e);
        
        Map<String, String> fieldErrors = e.getConstraintViolations()
            .stream()
            .collect(Collectors.toMap(
                violation -> violation.getPropertyPath().toString(),
                ConstraintViolation::getMessage,
                (existing, replacement) -> existing
            ));
        
        ErrorResponse error = ErrorResponse.builder()
            .errorCode("CONSTRAINT_VIOLATION")
            .message("Constraint validation failed")
            .status(HttpStatus.BAD_REQUEST.value())
            .path(request.getRequestURI())
            .correlationId(MDC.get(CorrelationIdConstants.CORRELATION_ID_MDC_KEY))
            .metadata(formatFieldErrors(fieldErrors))
            .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Handle IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException e, HttpServletRequest request) {
        logger.with("requestPath", request.getRequestURI())
                .error("Invalid argument", e);
        
        ErrorResponse error = ErrorResponse.builder()
            .errorCode("INVALID_ARGUMENT")
            .message(e.getMessage())
            .status(HttpStatus.BAD_REQUEST.value())
            .path(request.getRequestURI())
            .correlationId(MDC.get(CorrelationIdConstants.CORRELATION_ID_MDC_KEY))
            .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    // Handle domain duplicate device exception
    @ExceptionHandler(DuplicateDeviceDomainException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateDeviceDomainException(
            DuplicateDeviceDomainException e, HttpServletRequest request) {
        logger.with("requestPath", request.getRequestURI())
                .with("field", e.getField())
                .with("value", e.getValue())
                .warn("Duplicate device field detected");
        
        String message = String.format("A device with the same %s already exists: %s", 
                e.getField(), e.getValue());
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("field", e.getField());
        metadata.put("value", e.getValue());
        
        ErrorResponse error = ErrorResponse.builder()
            .errorCode("DUPLICATE_DEVICE")
            .message(message)
            .status(HttpStatus.CONFLICT.value())
            .path(request.getRequestURI())
            .correlationId(MDC.get(CorrelationIdConstants.CORRELATION_ID_MDC_KEY))
            .details("Please use a different " + e.getField())
            .metadata(metadata)
            .build();
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
    
    // Handle domain device not found exception
    @ExceptionHandler(DeviceNotFoundDomainException.class)
    public ResponseEntity<ErrorResponse> handleDeviceNotFoundDomainException(
            DeviceNotFoundDomainException e, HttpServletRequest request) {
        logger.with("requestPath", request.getRequestURI())
                .with("deviceId", e.getDeviceId())
                .warn("Device not found");
        
        ErrorResponse error = ErrorResponse.builder()
            .errorCode("DEVICE_NOT_FOUND")
            .message(e.getMessage())
            .status(HttpStatus.NOT_FOUND.value())
            .path(request.getRequestURI())
            .correlationId(MDC.get(CorrelationIdConstants.CORRELATION_ID_MDC_KEY))
            .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    // Handle domain optimistic locking exception
    @ExceptionHandler(OptimisticLockingDomainException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLockingDomainException(
            OptimisticLockingDomainException e, HttpServletRequest request) {
        logger.with("requestPath", request.getRequestURI())
                .with("resourceType", e.getResourceType())
                .with("resourceId", e.getResourceId())
                .with("expectedVersion", e.getExpectedVersion())
                .with("currentVersion", e.getCurrentVersion())
                .warn("Optimistic locking conflict detected");
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("expectedVersion", e.getExpectedVersion());
        metadata.put("currentVersion", e.getCurrentVersion());
        
        ErrorResponse error = ErrorResponse.builder()
            .errorCode("OPTIMISTIC_LOCKING_ERROR")
            .message(e.getMessage())
            .status(HttpStatus.CONFLICT.value())
            .path(request.getRequestURI())
            .correlationId(MDC.get(CorrelationIdConstants.CORRELATION_ID_MDC_KEY))
            .details("Concurrent update detected. Please refresh and retry.")
            .metadata(metadata)
            .build();
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
    
    // Handle JPA optimistic locking exceptions
    @ExceptionHandler({OptimisticLockException.class, ObjectOptimisticLockingFailureException.class})
    public ResponseEntity<ErrorResponse> handleOptimisticLockingException(
            Exception e, HttpServletRequest request) {
        logger.with("requestPath", request.getRequestURI())
                .with("exceptionType", e.getClass().getSimpleName())
                .warn("Optimistic locking conflict detected");
        
        ErrorResponse error = ErrorResponse.builder()
            .errorCode("OPTIMISTIC_LOCKING_ERROR")
            .message("The resource was modified by another process. Please refresh and retry.")
            .status(HttpStatus.CONFLICT.value())
            .path(request.getRequestURI())
            .correlationId(MDC.get(CorrelationIdConstants.CORRELATION_ID_MDC_KEY))
            .details("Concurrent update detected")
            .build();
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // Handle all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllUncaughtException(
            Exception e, HttpServletRequest request) {
        logger.with("requestPath", request.getRequestURI())
                .with("exceptionType", e.getClass().getName())
                .error("Unexpected error", e);
        
        ErrorResponse error = ErrorResponse.builder()
            .errorCode("INTERNAL_ERROR")
            .message("An unexpected error occurred")
            .details("Please contact support if the problem persists")
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .path(request.getRequestURI())
            .correlationId(MDC.get(CorrelationIdConstants.CORRELATION_ID_MDC_KEY))
            .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
    
    /**
     * Helper method to format field validation errors consistently.
     */
    private Map<String, Object> formatFieldErrors(Map<String, String> fieldErrors) {
        Map<String, Object> errors = new LinkedHashMap<>();
        errors.put("count", fieldErrors.size());
        errors.put("fields", fieldErrors);
        return errors;
    }
}