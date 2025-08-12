/**
 * Global exception handling providing consistent error responses.
 * 
 * <p>This package implements centralized exception handling for the REST API,
 * ensuring all errors are returned in a consistent format following RFC 7807
 * Problem Details standard. It translates various exception types into
 * appropriate HTTP status codes and user-friendly error messages.
 * 
 * <p>Exception handlers:
 * <ul>
 *   <li>{@link io.thatworked.support.report.api.exception.GlobalExceptionHandler} - 
 *       Central @RestControllerAdvice component that intercepts all exceptions
 *       thrown by controllers and converts them to standardized error responses</li>
 * </ul>
 * 
 * <p>Handled exception types:
 * <ul>
 *   <li>Domain exceptions - Business rule violations (400 Bad Request)</li>
 *   <li>Validation exceptions - Input validation failures (400 Bad Request)</li>
 *   <li>Not found exceptions - Missing resources (404 Not Found)</li>
 *   <li>Conflict exceptions - State conflicts (409 Conflict)</li>
 *   <li>Infrastructure exceptions - External service failures (503 Service Unavailable)</li>
 *   <li>Generic exceptions - Unexpected errors (500 Internal Server Error)</li>
 * </ul>
 * 
 * <p>Error response format (RFC 7807):
 * <ul>
 *   <li>type - URI reference identifying the problem type</li>
 *   <li>title - Short, human-readable summary</li>
 *   <li>status - HTTP status code</li>
 *   <li>detail - Human-readable explanation</li>
 *   <li>instance - URI reference for this occurrence</li>
 *   <li>timestamp - When the error occurred</li>
 *   <li>violations - Field-level validation errors</li>
 * </ul>
 * 
 * <p>Additional features:
 * <ul>
 *   <li>Request ID correlation for log tracing</li>
 *   <li>Stack trace inclusion in development mode</li>
 *   <li>Sensitive data filtering in production</li>
 *   <li>Internationalized error messages</li>
 *   <li>Metrics collection for error monitoring</li>
 * </ul>
 * 
 * <p>Best practices:
 * <ul>
 *   <li>Log errors with appropriate severity</li>
 *   <li>Never expose internal implementation details</li>
 *   <li>Provide actionable error messages</li>
 *   <li>Include links to documentation when helpful</li>
 * </ul>
 */
package io.thatworked.support.report.api.exception;