/**
 * Data Transfer Objects for external service communication.
 * 
 * <p>This package contains DTOs used for communication with external services
 * and APIs. These objects represent the data structures expected by or returned
 * from other microservices in the system. They act as an anti-corruption layer,
 * preventing external API changes from directly impacting the domain model.
 * 
 * <p>External service DTOs:
 * <ul>
 *   <li>{@link io.thatworked.support.report.infrastructure.dto.DeviceDTO} - 
 *       Device information from device-service API</li>
 *   <li>{@link io.thatworked.support.report.infrastructure.dto.PingResultDTO} - 
 *       Individual ping result from ping-service</li>
 *   <li>{@link io.thatworked.support.report.infrastructure.dto.PingStatisticsDTO} - 
 *       Aggregated ping statistics for reporting</li>
 *   <li>{@link io.thatworked.support.report.infrastructure.dto.PingTargetDTO} - 
 *       Ping monitoring configuration data</li>
 *   <li>{@link io.thatworked.support.report.infrastructure.dto.AlertDTO} - 
 *       Alert information from alert-service</li>
 * </ul>
 * 
 * <p>Query DTOs:
 * <ul>
 *   <li>{@link io.thatworked.support.report.infrastructure.dto.AlertSearchCriteria} - 
 *       Query parameters for alert filtering</li>
 * </ul>
 * 
 * <p>DTO characteristics:
 * <ul>
 *   <li>Immutable with builder pattern or record types</li>
 *   <li>JSON serialization annotations for API compatibility</li>
 *   <li>Validation annotations for input sanitization</li>
 *   <li>Null-safe with Optional fields where appropriate</li>
 *   <li>Clear documentation of expected formats</li>
 * </ul>
 * 
 * <p>Versioning strategy:
 * <ul>
 *   <li>Backward compatible field additions</li>
 *   <li>@JsonIgnoreProperties for unknown fields</li>
 *   <li>Version-specific DTOs when breaking changes required</li>
 *   <li>Adapter pattern for version translation</li>
 * </ul>
 */
package io.thatworked.support.report.infrastructure.dto;