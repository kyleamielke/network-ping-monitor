/**
 * Domain-specific exceptions representing business rule violations and error conditions.
 * 
 * <p>These exceptions make domain errors explicit and self-documenting, providing
 * clear error messages and context for troubleshooting. They are thrown when
 * business invariants are violated or domain operations cannot be completed.
 * 
 * <p>Exception hierarchy:
 * <ul>
 *   <li>{@link io.thatworked.support.report.domain.exception.ReportDomainException} - 
 *       Base exception for all domain errors, extends RuntimeException for
 *       unchecked exception semantics</li>
 *   <li>{@link io.thatworked.support.report.domain.exception.ReportGenerationException} - 
 *       Thrown when report generation fails due to invalid parameters, insufficient
 *       data, or processing errors</li>
 *   <li>{@link io.thatworked.support.report.domain.exception.ReportDataException} - 
 *       Thrown when required data cannot be retrieved from external services
 *       or data quality issues are detected</li>
 * </ul>
 * 
 * <p>Common scenarios:
 * <ul>
 *   <li>Invalid report time range (end before start)</li>
 *   <li>Unsupported report type and format combination</li>
 *   <li>Insufficient data for statistical calculations</li>
 *   <li>External service unavailable during data collection</li>
 *   <li>Report generation timeout exceeded</li>
 * </ul>
 * 
 * <p>Design principles:
 * <ul>
 *   <li>Exceptions include meaningful error messages</li>
 *   <li>Context information preserved through exception chaining</li>
 *   <li>Error codes for programmatic error handling</li>
 *   <li>Immutable exception state for thread safety</li>
 * </ul>
 */
package io.thatworked.support.report.domain.exception;