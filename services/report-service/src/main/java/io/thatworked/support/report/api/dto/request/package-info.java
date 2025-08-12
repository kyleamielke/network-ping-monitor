/**
 * Request DTOs for incoming API calls with comprehensive validation.
 * 
 * <p>This package contains immutable Data Transfer Objects that represent
 * incoming API requests. These DTOs are deserialized from JSON, validated
 * using Jakarta Bean Validation, and converted to application commands.
 * They provide a stable API contract while protecting the domain from
 * invalid input.
 * 
 * <p>Request DTOs:
 * <ul>
 *   <li>{@link io.thatworked.support.report.api.dto.request.GenerateReportRequest} - 
 *       Parameters for report generation including type, time range, format,
 *       and optional device filters. Validates date ranges and enum values</li>
 *   <li>{@link io.thatworked.support.report.api.dto.request.ListReportsRequest} - 
 *       Query parameters for report listing with pagination, sorting, and
 *       filtering by type, date range, and status</li>
 *   <li>{@link io.thatworked.support.report.api.dto.request.ScheduleReportRequest} - 
 *       Configuration for recurring reports with cron expression validation,
 *       recipient email list, and report parameters</li>
 * </ul>
 * 
 * <p>Validation features:
 * <ul>
 *   <li>@NotNull for required fields</li>
 *   <li>@Valid for nested object validation</li>
 *   <li>@Pattern for format validation (cron, email)</li>
 *   <li>@Size for collection and string length limits</li>
 *   <li>@Future/@Past for temporal validation</li>
 *   <li>Custom validators for business rules</li>
 * </ul>
 * 
 * <p>JSON deserialization:
 * <ul>
 *   <li>@JsonProperty for field naming</li>
 *   <li>@JsonFormat for date/time parsing</li>
 *   <li>@JsonIgnoreProperties for forward compatibility</li>
 *   <li>@JsonCreator for immutable object construction</li>
 * </ul>
 * 
 * <p>Design patterns:
 * <ul>
 *   <li>Builder pattern for complex request construction</li>
 *   <li>Immutable objects with validation in constructor</li>
 *   <li>Default values for optional parameters</li>
 *   <li>Fluent API for test data creation</li>
 * </ul>
 */
package io.thatworked.support.report.api.dto.request;