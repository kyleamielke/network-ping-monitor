/**
 * Logging infrastructure adapters and utilities.
 * 
 * <p>This package provides implementations of the domain logging port, enabling
 * the domain layer to perform structured logging without depending on specific
 * logging frameworks. It acts as an anti-corruption layer between the domain
 * and technical logging infrastructure.
 * 
 * <p>Core components:
 * <ul>
 *   <li>{@link io.thatworked.support.report.infrastructure.logger.DomainLoggerAdapter} - 
 *       Implements the DomainLogger port interface using SLF4J/Logback,
 *       providing structured logging with MDC context</li>
 * </ul>
 * 
 * <p>Logging features:
 * <ul>
 *   <li>Structured logging with key-value pairs</li>
 *   <li>Correlation IDs for request tracing</li>
 *   <li>Performance metrics logging</li>
 *   <li>Error tracking with stack traces</li>
 *   <li>Audit logging for compliance</li>
 * </ul>
 * 
 * <p>Log levels and usage:
 * <ul>
 *   <li>ERROR - System failures requiring immediate attention</li>
 *   <li>WARN - Recoverable issues or degraded functionality</li>
 *   <li>INFO - Business events and major operations</li>
 *   <li>DEBUG - Detailed execution flow for troubleshooting</li>
 *   <li>TRACE - Fine-grained data for deep debugging</li>
 * </ul>
 * 
 * <p>MDC (Mapped Diagnostic Context) fields:
 * <ul>
 *   <li>reportId - Current report being processed</li>
 *   <li>userId - User initiating the operation</li>
 *   <li>operationType - Business operation name</li>
 *   <li>duration - Operation execution time</li>
 *   <li>correlationId - Request correlation across services</li>
 * </ul>
 * 
 * <p>Best practices:
 * <ul>
 *   <li>Log at appropriate levels based on severity</li>
 *   <li>Include contextual information in structured format</li>
 *   <li>Avoid logging sensitive data (PII, credentials)</li>
 *   <li>Use parameterized messages for performance</li>
 *   <li>Ensure logs are actionable and meaningful</li>
 * </ul>
 */
package io.thatworked.support.report.infrastructure.logger;