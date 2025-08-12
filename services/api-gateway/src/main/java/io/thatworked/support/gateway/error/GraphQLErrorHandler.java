package io.thatworked.support.gateway.error;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import io.thatworked.support.gateway.exception.*;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import feign.FeignException;

import java.util.HashMap;
import java.util.Map;

/**
 * Global error handler for GraphQL operations.
 * Converts exceptions into GraphQL errors with consistent formatting.
 */
@Component
public class GraphQLErrorHandler extends DataFetcherExceptionResolverAdapter {
    
    private final StructuredLogger logger;
    private final boolean debugMode;
    
    public GraphQLErrorHandler(StructuredLoggerFactory loggerFactory,
                              @Value("${gateway.error.debug-mode:false}") boolean debugMode) {
        this.logger = loggerFactory.getLogger(GraphQLErrorHandler.class);
        this.debugMode = debugMode;
    }
    
    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        String path = env.getExecutionStepInfo().getPath().toString();
        String fieldName = env.getFieldDefinition().getName();
        
        logger.with("errorHandler", "GraphQL")
              .with("path", path)
              .with("field", fieldName)
              .with("errorType", ex.getClass().getSimpleName())
              .with("errorMessage", ex.getMessage())
              .error("GraphQL execution error", ex);
        
        // Handle specific exception types
        if (ex instanceof ResourceNotFoundException) {
            ResourceNotFoundException notFoundEx = (ResourceNotFoundException) ex;
            Map<String, Object> extensions = buildExtensions("RESOURCE_NOT_FOUND", ex);
            extensions.put("resourceType", notFoundEx.getResourceType());
            extensions.put("resourceId", notFoundEx.getResourceId());
            return GraphqlErrorBuilder.newError()
                .errorType(ErrorType.NOT_FOUND)
                .message(ex.getMessage())
                .path(env.getExecutionStepInfo().getPath())
                .location(env.getField().getSourceLocation())
                .extensions(extensions)
                .build();
        } else if (ex instanceof InvalidInputException) {
            InvalidInputException invalidEx = (InvalidInputException) ex;
            Map<String, Object> extensions = buildExtensions("INVALID_INPUT", ex);
            if (invalidEx.getField() != null) {
                extensions.put("field", invalidEx.getField());
                extensions.put("value", invalidEx.getValue());
                extensions.put("constraint", invalidEx.getConstraint());
            }
            return GraphqlErrorBuilder.newError()
                .errorType(ErrorType.BAD_REQUEST)
                .message(ex.getMessage())
                .path(env.getExecutionStepInfo().getPath())
                .location(env.getField().getSourceLocation())
                .extensions(extensions)
                .build();
        } else if (ex instanceof OperationFailedException) {
            OperationFailedException opEx = (OperationFailedException) ex;
            
            // Check if the cause is a FeignException
            if (opEx.getCause() instanceof FeignException) {
                FeignException feignEx = (FeignException) opEx.getCause();
                String responseBody = feignEx.contentUTF8();
                String message = opEx.getMessage();
                
                // Try to parse JSON error response from the service
                if (responseBody != null && !responseBody.isEmpty()) {
                    try {
                        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                        Map<String, Object> errorResponse = mapper.readValue(responseBody, Map.class);
                        if (errorResponse.containsKey("message")) {
                            message = (String) errorResponse.get("message");
                        }
                        
                        Map<String, Object> extensions = buildExtensions("SERVICE_ERROR", ex);
                        if (errorResponse.containsKey("metadata")) {
                            extensions.put("metadata", errorResponse.get("metadata"));
                        }
                        if (errorResponse.containsKey("errorCode")) {
                            extensions.put("serviceErrorCode", errorResponse.get("errorCode"));
                        }
                        
                        ErrorType type = ErrorType.INTERNAL_ERROR;
                        int status = feignEx.status();
                        if (status == 400) type = ErrorType.BAD_REQUEST;
                        else if (status == 401) type = ErrorType.UNAUTHORIZED;
                        else if (status == 403) type = ErrorType.FORBIDDEN;
                        else if (status == 404) type = ErrorType.NOT_FOUND;
                        else if (status == 409) type = ErrorType.BAD_REQUEST;
                        
                        return GraphqlErrorBuilder.newError()
                            .errorType(type)
                            .message(message)
                            .path(env.getExecutionStepInfo().getPath())
                            .location(env.getField().getSourceLocation())
                            .extensions(extensions)
                            .build();
                    } catch (Exception e) {
                        // If JSON parsing fails, continue with default handling
                    }
                }
            }
            
            Map<String, Object> extensions = buildExtensions("OPERATION_FAILED", ex);
            extensions.put("operation", opEx.getOperation());
            extensions.put("reason", opEx.getReason());
            return GraphqlErrorBuilder.newError()
                .errorType(ErrorType.INTERNAL_ERROR)
                .message(ex.getMessage())
                .path(env.getExecutionStepInfo().getPath())
                .location(env.getField().getSourceLocation())
                .extensions(extensions)
                .build();
        } else if (ex instanceof OptimisticLockingException) {
            OptimisticLockingException lockingEx = (OptimisticLockingException) ex;
            Map<String, Object> extensions = buildExtensions("OPTIMISTIC_LOCKING_ERROR", ex);
            extensions.put("expectedVersion", lockingEx.getExpectedVersion());
            extensions.put("currentVersion", lockingEx.getCurrentVersion());
            return GraphqlErrorBuilder.newError()
                .errorType(ErrorType.BAD_REQUEST)
                .message(ex.getMessage())
                .path(env.getExecutionStepInfo().getPath())
                .location(env.getField().getSourceLocation())
                .extensions(extensions)
                .build();
        } else if (ex instanceof ValidationException) {
            ValidationException validationEx = (ValidationException) ex;
            Map<String, Object> extensions = buildExtensions("VALIDATION_ERROR", ex);
            if (validationEx.hasFieldErrors()) {
                extensions.put("fieldErrors", validationEx.getFieldErrors());
            }
            return GraphqlErrorBuilder.newError()
                .errorType(ErrorType.BAD_REQUEST)
                .message(ex.getMessage())
                .path(env.getExecutionStepInfo().getPath())
                .location(env.getField().getSourceLocation())
                .extensions(extensions)
                .build();
        } else if (ex instanceof IllegalArgumentException) {
            return GraphqlErrorBuilder.newError()
                .errorType(ErrorType.BAD_REQUEST)
                .message("Invalid input: " + ex.getMessage())
                .path(env.getExecutionStepInfo().getPath())
                .location(env.getField().getSourceLocation())
                .extensions(buildExtensions("INVALID_INPUT", ex))
                .build();
        } else if (ex instanceof SecurityException) {
            return GraphqlErrorBuilder.newError()
                .errorType(ErrorType.UNAUTHORIZED)
                .message("Unauthorized access")
                .path(env.getExecutionStepInfo().getPath())
                .location(env.getField().getSourceLocation())
                .extensions(buildExtensions("UNAUTHORIZED", ex))
                .build();
        } else if (ex instanceof UnsupportedOperationException) {
            return GraphqlErrorBuilder.newError()
                .errorType(ErrorType.FORBIDDEN)
                .message("Operation not supported: " + ex.getMessage())
                .path(env.getExecutionStepInfo().getPath())
                .location(env.getField().getSourceLocation())
                .extensions(buildExtensions("UNSUPPORTED_OPERATION", ex))
                .build();
        } else if (ex instanceof FeignException) {
            // Handle Feign client errors from backend service calls
            FeignException feignEx = (FeignException) ex;
            String responseBody = feignEx.contentUTF8();
            String message = feignEx.getMessage();
            
            // Try to parse JSON error response
            if (responseBody != null && !responseBody.isEmpty()) {
                try {
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    Map<String, Object> errorResponse = mapper.readValue(responseBody, Map.class);
                    if (errorResponse.containsKey("message")) {
                        message = (String) errorResponse.get("message");
                    }
                    
                    Map<String, Object> extensions = buildExtensions("SERVICE_ERROR", ex);
                    if (errorResponse.containsKey("metadata")) {
                        extensions.put("metadata", errorResponse.get("metadata"));
                    }
                    if (errorResponse.containsKey("errorCode")) {
                        extensions.put("errorCode", errorResponse.get("errorCode"));
                    }
                    
                    ErrorType type = ErrorType.INTERNAL_ERROR;
                    int status = feignEx.status();
                    if (status == 400) type = ErrorType.BAD_REQUEST;
                    else if (status == 401) type = ErrorType.UNAUTHORIZED;
                    else if (status == 403) type = ErrorType.FORBIDDEN;
                    else if (status == 404) type = ErrorType.NOT_FOUND;
                    else if (status == 409) type = ErrorType.BAD_REQUEST;
                    
                    return GraphqlErrorBuilder.newError()
                        .errorType(type)
                        .message(message)
                        .path(env.getExecutionStepInfo().getPath())
                        .location(env.getField().getSourceLocation())
                        .extensions(extensions)
                        .build();
                } catch (Exception e) {
                    // If JSON parsing fails, use original message
                }
            }
            
            // Fallback to basic error
            ErrorType type = ErrorType.INTERNAL_ERROR;
            int status = feignEx.status();
            if (status == 400) type = ErrorType.BAD_REQUEST;
            else if (status == 401) type = ErrorType.UNAUTHORIZED;
            else if (status == 403) type = ErrorType.FORBIDDEN;
            else if (status == 404) type = ErrorType.NOT_FOUND;
            else if (status == 409) type = ErrorType.BAD_REQUEST;
            
            return GraphqlErrorBuilder.newError()
                .errorType(type)
                .message("Service error: " + message)
                .path(env.getExecutionStepInfo().getPath())
                .location(env.getField().getSourceLocation())
                .extensions(buildExtensions("SERVICE_ERROR_" + status, ex))
                .build();
        } else if (ex.getMessage() != null && ex.getMessage().contains("not found")) {
            return GraphqlErrorBuilder.newError()
                .errorType(ErrorType.NOT_FOUND)
                .message(ex.getMessage())
                .path(env.getExecutionStepInfo().getPath())
                .location(env.getField().getSourceLocation())
                .extensions(buildExtensions("NOT_FOUND", ex))
                .build();
        } else if (ex instanceof org.springframework.web.client.HttpClientErrorException) {
            // Handle HTTP client errors from backend service calls
            org.springframework.web.client.HttpClientErrorException httpEx = 
                (org.springframework.web.client.HttpClientErrorException) ex;
            String responseBody = httpEx.getResponseBodyAsString();
            String message = httpEx.getMessage();
            
            // Try to parse JSON error response
            if (responseBody != null && !responseBody.isEmpty()) {
                try {
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    Map<String, Object> errorResponse = mapper.readValue(responseBody, Map.class);
                    if (errorResponse.containsKey("message")) {
                        message = (String) errorResponse.get("message");
                    }
                    // Pass through metadata if present
                    if (errorResponse.containsKey("metadata")) {
                        Map<String, Object> extensions = buildExtensions("HTTP_ERROR_" + httpEx.getStatusCode().value(), ex);
                        extensions.put("metadata", errorResponse.get("metadata"));
                        extensions.put("errorCode", errorResponse.get("errorCode"));
                        
                        ErrorType type = ErrorType.INTERNAL_ERROR;
                        if (httpEx.getStatusCode().value() == 400) type = ErrorType.BAD_REQUEST;
                        else if (httpEx.getStatusCode().value() == 401) type = ErrorType.UNAUTHORIZED;
                        else if (httpEx.getStatusCode().value() == 403) type = ErrorType.FORBIDDEN;
                        else if (httpEx.getStatusCode().value() == 404) type = ErrorType.NOT_FOUND;
                        else if (httpEx.getStatusCode().value() == 409) type = ErrorType.BAD_REQUEST;
                        
                        return GraphqlErrorBuilder.newError()
                            .errorType(type)
                            .message(message)
                            .path(env.getExecutionStepInfo().getPath())
                            .location(env.getField().getSourceLocation())
                            .extensions(extensions)
                            .build();
                    }
                } catch (Exception e) {
                    // Ignore JSON parsing errors
                }
            }
            
            ErrorType type = ErrorType.INTERNAL_ERROR;
            if (httpEx.getStatusCode().value() == 400) type = ErrorType.BAD_REQUEST;
            else if (httpEx.getStatusCode().value() == 401) type = ErrorType.UNAUTHORIZED;
            else if (httpEx.getStatusCode().value() == 403) type = ErrorType.FORBIDDEN;
            else if (httpEx.getStatusCode().value() == 404) type = ErrorType.NOT_FOUND;
            else if (httpEx.getStatusCode().value() == 409) type = ErrorType.BAD_REQUEST;
            
            return GraphqlErrorBuilder.newError()
                .errorType(type)
                .message(message)
                .path(env.getExecutionStepInfo().getPath())
                .location(env.getField().getSourceLocation())
                .extensions(buildExtensions("HTTP_ERROR_" + httpEx.getStatusCode().value(), ex))
                .build();
        } else if (ex instanceof java.util.concurrent.TimeoutException) {
            return GraphqlErrorBuilder.newError()
                .errorType(ErrorType.INTERNAL_ERROR)
                .message("Request timeout: " + ex.getMessage())
                .path(env.getExecutionStepInfo().getPath())
                .location(env.getField().getSourceLocation())
                .extensions(buildExtensions("TIMEOUT", ex))
                .build();
        } else if (ex instanceof NullPointerException) {
            return GraphqlErrorBuilder.newError()
                .errorType(ErrorType.INTERNAL_ERROR)
                .message("Null value encountered: Please check your input data")
                .path(env.getExecutionStepInfo().getPath())
                .location(env.getField().getSourceLocation())
                .extensions(buildExtensions("NULL_POINTER", ex))
                .build();
        }
        
        // Default error handling - return actual error message
        String errorMessage = ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName();
        
        // Determine error type based on exception
        ErrorType errorType = ErrorType.INTERNAL_ERROR;
        if (ex instanceof RuntimeException && ex.getCause() != null) {
            // Check if it's a wrapped exception
            Throwable cause = ex.getCause();
            if (cause.getMessage() != null && cause.getMessage().toLowerCase().contains("validation")) {
                errorType = ErrorType.BAD_REQUEST;
            }
        }
        
        return GraphqlErrorBuilder.newError()
            .errorType(errorType)
            .message(errorMessage)
            .path(env.getExecutionStepInfo().getPath())
            .location(env.getField().getSourceLocation())
            .extensions(buildExtensions("INTERNAL_ERROR", ex))
            .build();
    }
    
    private Map<String, Object> buildExtensions(String errorCode, Throwable ex) {
        Map<String, Object> extensions = new HashMap<>();
        extensions.put("code", errorCode);
        extensions.put("timestamp", System.currentTimeMillis());
        
        // Only include exception details in non-production environments
        if (isDebugMode()) {
            extensions.put("exception", ex.getClass().getName());
            extensions.put("detail", ex.getMessage());
            
            // Include root cause if different from main exception
            Throwable rootCause = getRootCause(ex);
            if (rootCause != ex && rootCause != null) {
                Map<String, Object> rootCauseInfo = new HashMap<>();
                rootCauseInfo.put("exception", rootCause.getClass().getName());
                rootCauseInfo.put("message", rootCause.getMessage());
                extensions.put("rootCause", rootCauseInfo);
            }
            
            // Include stack trace first few lines for debugging
            StackTraceElement[] stackTrace = ex.getStackTrace();
            if (stackTrace.length > 0) {
                extensions.put("location", stackTrace[0].toString());
            }
        }
        
        return extensions;
    }
    
    private Throwable getRootCause(Throwable throwable) {
        Throwable rootCause = throwable;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        return rootCause;
    }
    
    private boolean isDebugMode() {
        return debugMode;
    }
}