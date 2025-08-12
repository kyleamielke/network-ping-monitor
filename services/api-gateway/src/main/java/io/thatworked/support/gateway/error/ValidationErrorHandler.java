package io.thatworked.support.gateway.error;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.validation.ValidationError;
import graphql.validation.ValidationErrorClassification;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handles validation errors for GraphQL operations.
 * Provides detailed validation error messages for better client feedback.
 */
@Component
public class ValidationErrorHandler {
    
    private final StructuredLogger logger;
    
    public ValidationErrorHandler(StructuredLoggerFactory loggerFactory) {
        this.logger = loggerFactory.getLogger(ValidationErrorHandler.class);
    }
    
    /**
     * Convert validation errors into GraphQL errors with detailed messages.
     */
    public List<GraphQLError> handleValidationErrors(List<ValidationError> errors) {
        return errors.stream()
            .map(this::convertValidationError)
            .collect(Collectors.toList());
    }
    
    private GraphQLError convertValidationError(ValidationError error) {
        logger.with("errorHandler", "Validation")
              .with("errorType", error.getValidationErrorType())
              .with("message", error.getMessage())
              .warn("GraphQL validation error");
        
        String message = formatValidationMessage(error);
        
        return GraphqlErrorBuilder.newError()
            .errorType(ErrorType.BAD_REQUEST)
            .message(message)
            .locations(error.getLocations())
            .extensions(Map.of(
                "code", "VALIDATION_ERROR",
                "validationType", error.getValidationErrorType().toString(),
                "field", error.getQueryPath() != null ? error.getQueryPath().toString() : ""
            ))
            .build();
    }
    
    private String formatValidationMessage(ValidationError error) {
        ValidationErrorClassification classification = error.getValidationErrorType();
        
        // For now, just return the error message
        // GraphQL Java validation errors are quite descriptive already
        return error.getMessage() != null ? error.getMessage() : "Validation error";
    }
}