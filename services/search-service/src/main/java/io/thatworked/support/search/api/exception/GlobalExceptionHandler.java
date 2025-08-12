package io.thatworked.support.search.api.exception;

import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.search.domain.exception.InvalidSearchQueryException;
import io.thatworked.support.search.domain.exception.SearchDomainException;
import io.thatworked.support.search.domain.exception.SearchProviderException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler for the search service API.
 * Converts exceptions to RFC 7807 Problem Details format.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private final StructuredLogger logger;
    
    public GlobalExceptionHandler(StructuredLoggerFactory loggerFactory) {
        this.logger = loggerFactory.getLogger(GlobalExceptionHandler.class);
    }
    
    @ExceptionHandler(InvalidSearchQueryException.class)
    public ResponseEntity<ProblemDetail> handleInvalidSearchQuery(
            InvalidSearchQueryException ex, WebRequest request) {
        
        logger.with("error", ex.getMessage())
              .with("path", request.getDescription(false))
              .warn("Invalid search query");
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, ex.getMessage()
        );
        problemDetail.setTitle("Invalid Search Query");
        problemDetail.setType(URI.create("/problems/invalid-search-query"));
        problemDetail.setProperty("timestamp", Instant.now());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }
    
    @ExceptionHandler(SearchProviderException.class)
    public ResponseEntity<ProblemDetail> handleSearchProviderException(
            SearchProviderException ex, WebRequest request) {
        
        logger.with("error", ex.getMessage())
              .with("path", request.getDescription(false))
              .error("Search provider error", ex);
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.SERVICE_UNAVAILABLE, "Search service temporarily unavailable"
        );
        problemDetail.setTitle("Search Provider Error");
        problemDetail.setType(URI.create("/problems/search-provider-error"));
        problemDetail.setProperty("timestamp", Instant.now());
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(problemDetail);
    }
    
    @ExceptionHandler(SearchDomainException.class)
    public ResponseEntity<ProblemDetail> handleSearchDomainException(
            SearchDomainException ex, WebRequest request) {
        
        logger.with("error", ex.getMessage())
              .with("path", request.getDescription(false))
              .error("Domain exception", ex);
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred during search"
        );
        problemDetail.setTitle("Search Error");
        problemDetail.setType(URI.create("/problems/search-error"));
        problemDetail.setProperty("timestamp", Instant.now());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {
        
        Map<String, String> violations = ex.getConstraintViolations().stream()
            .collect(Collectors.toMap(
                violation -> getPropertyPath(violation),
                ConstraintViolation::getMessage
            ));
        
        logger.with("violations", violations)
              .with("path", request.getDescription(false))
              .warn("Validation failed");
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, "Validation failed"
        );
        problemDetail.setTitle("Validation Error");
        problemDetail.setType(URI.create("/problems/validation-error"));
        problemDetail.setProperty("violations", violations);
        problemDetail.setProperty("timestamp", Instant.now());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        logger.with("errors", errors)
              .with("path", request.getDescription(false))
              .warn("Method argument validation failed");
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, "Validation failed"
        );
        problemDetail.setTitle("Validation Error");
        problemDetail.setType(URI.create("/problems/validation-error"));
        problemDetail.setProperty("errors", errors);
        problemDetail.setProperty("timestamp", Instant.now());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(
            Exception ex, WebRequest request) {
        
        logger.with("error", ex.getMessage())
              .with("errorType", ex.getClass().getSimpleName())
              .with("path", request.getDescription(false))
              .error("Unexpected error", ex);
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred"
        );
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setType(URI.create("/problems/internal-error"));
        problemDetail.setProperty("timestamp", Instant.now());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }
    
    private String getPropertyPath(ConstraintViolation<?> violation) {
        String propertyPath = violation.getPropertyPath().toString();
        // Remove method name prefix if present
        int lastDot = propertyPath.lastIndexOf('.');
        return lastDot >= 0 ? propertyPath.substring(lastDot + 1) : propertyPath;
    }
}