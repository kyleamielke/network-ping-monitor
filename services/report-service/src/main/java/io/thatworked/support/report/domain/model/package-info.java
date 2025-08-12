/**
 * Domain models representing core business entities and value objects.
 * 
 * <p>These are pure Java objects with no framework dependencies.
 * They encapsulate business logic and maintain invariants through
 * immutable design and constructor validation.
 * 
 * <p>Core entities:
 * <ul>
 *   <li>{@link io.thatworked.support.report.domain.model.Report} - Main report entity
 *       containing generated content, metadata, and lifecycle state</li>
 *   <li>{@link io.thatworked.support.report.domain.model.ScheduledReport} - Scheduled
 *       report configuration with recurrence rules and recipients</li>
 * </ul>
 * 
 * <p>Value objects:
 * <ul>
 *   <li>{@link io.thatworked.support.report.domain.model.ReportTimeRange} - Time period
 *       specification with validation for report data selection</li>
 *   <li>{@link io.thatworked.support.report.domain.model.ReportContent} - Encapsulated
 *       report data with format-specific rendering</li>
 *   <li>{@link io.thatworked.support.report.domain.model.ReportMetadata} - Report
 *       generation context including parameters and statistics</li>
 * </ul>
 * 
 * <p>Enumerations:
 * <ul>
 *   <li>{@link io.thatworked.support.report.domain.model.ReportType} - Available report
 *       types (DEVICE_UPTIME, PING_STATISTICS, etc.)</li>
 *   <li>{@link io.thatworked.support.report.domain.model.ReportFormat} - Supported
 *       output formats (PDF, CSV, JSON)</li>
 * </ul>
 * 
 * <p>Design patterns:
 * <ul>
 *   <li>All value objects are immutable with final fields</li>
 *   <li>Constructor validation ensures business invariants</li>
 *   <li>Meaningful equals/hashCode for value object semantics</li>
 *   <li>Builder pattern for complex object construction</li>
 * </ul>
 */
package io.thatworked.support.report.domain.model;