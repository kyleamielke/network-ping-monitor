/**
 * Application-level port interfaces defining service contracts.
 * 
 * <p>These ports represent the public API of the application layer, defining
 * the operations available to external layers (API, CLI, scheduled tasks).
 * They provide a stable contract that shields clients from internal implementation
 * changes while exposing business capabilities.
 * 
 * <p>Primary ports:
 * <ul>
 *   <li>{@link io.thatworked.support.report.application.port.ReportApplicationPort} - 
 *       Main contract for report operations including generation, retrieval,
 *       scheduling, and management. Implemented by the ReportApplicationService</li>
 * </ul>
 * 
 * <p>Contract design principles:
 * <ul>
 *   <li>Method names reflect business operations, not technical details</li>
 *   <li>Parameters use simple types or well-defined DTOs</li>
 *   <li>Return types are domain objects or result wrappers</li>
 *   <li>Exceptions declare business-level failures</li>
 * </ul>
 * 
 * <p>Typical operations exposed:
 * <ul>
 *   <li>generateReport(type, timeRange, format) - Synchronous report generation</li>
 *   <li>generateReportAsync(type, timeRange, format) - Async with job tracking</li>
 *   <li>scheduleReport(type, cronExpression, recipients) - Recurring reports</li>
 *   <li>getReport(reportId) - Retrieve report metadata and status</li>
 *   <li>downloadReport(reportId) - Get report file content</li>
 * </ul>
 * 
 * <p>Benefits of port interfaces:
 * <ul>
 *   <li>Clear API boundary for the application layer</li>
 *   <li>Enables testing with mock implementations</li>
 *   <li>Supports multiple implementations (standard, cached, etc.)</li>
 *   <li>Facilitates API versioning and evolution</li>
 * </ul>
 */
package io.thatworked.support.report.application.port;