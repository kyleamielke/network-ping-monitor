package io.thatworked.support.report.api.exception;

import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.report.domain.exception.ReportNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the report service API.
 * Provides consistent error responses across all endpoints.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private final StructuredLogger logger;
    
    public GlobalExceptionHandler(StructuredLoggerFactory loggerFactory) {
        this.logger = loggerFactory.getLogger(GlobalExceptionHandler.class);
    }
    
    /**
     * Handles report not found exceptions.
     */
    @ExceptionHandler(ReportNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleReportNotFoundException(
            ReportNotFoundException ex, WebRequest request) {
        
        logger.with("exception", "ReportNotFoundException")
              .with("reportId", ex.getReportId().toString())
              .with("path", request.getDescription(false))
              .warn("Report not found");
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND, ex.getMessage()
        );
        problemDetail.setTitle("Report Not Found");
        problemDetail.setType(URI.create("urn:report-service:report-not-found"));
        problemDetail.setProperty("reportId", ex.getReportId().toString());
        problemDetail.setProperty("timestamp", Instant.now());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }
    
    /**
     * Handles validation errors.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        logger.with("exception", "ValidationException")
              .with("errors", errors)
              .with("path", request.getDescription(false))
              .warn("Validation failed");
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, "Validation failed"
        );
        problemDetail.setTitle("Bad Request");
        problemDetail.setType(URI.create("urn:report-service:validation-error"));
        problemDetail.setProperty("errors", errors);
        problemDetail.setProperty("timestamp", Instant.now());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }
    
    /**
     * Handles illegal argument exceptions.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        
        logger.with("exception", "IllegalArgumentException")
              .with("message", ex.getMessage())
              .with("path", request.getDescription(false))
              .warn("Invalid argument");
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, ex.getMessage()
        );
        problemDetail.setTitle("Invalid Request");
        problemDetail.setType(URI.create("urn:report-service:invalid-argument"));
        problemDetail.setProperty("timestamp", Instant.now());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }
    
    /**
     * Handles runtime exceptions.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ProblemDetail> handleRuntimeException(
            RuntimeException ex, WebRequest request) {
        
        logger.with("exception", ex.getClass().getSimpleName())
              .with("message", ex.getMessage())
              .with("path", request.getDescription(false))
              .error("Unexpected runtime exception", ex);
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, 
            "An unexpected error occurred. Please try again later."
        );
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setType(URI.create("urn:report-service:internal-error"));
        problemDetail.setProperty("timestamp", Instant.now());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }
    
    /**
     * Handles all other exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGlobalException(
            Exception ex, WebRequest request) {
        
        logger.with("exception", ex.getClass().getName())
              .with("message", ex.getMessage())
              .with("path", request.getDescription(false))
              .error("Unhandled exception", ex);
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, 
            "An unexpected error occurred"
        );
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setType(URI.create("urn:report-service:unknown-error"));
        problemDetail.setProperty("timestamp", Instant.now());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }
}