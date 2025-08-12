/**
 * Mapper components for API layer data transformations.
 * 
 * <p>This package contains mapper classes that handle bidirectional conversions
 * between API DTOs and domain/application objects. These mappers maintain clean
 * architectural boundaries by ensuring that API concerns (JSON formatting,
 * HATEOAS links, etc.) don't leak into the domain layer and vice versa.
 * 
 * <p>Mapper classes:
 * <ul>
 *   <li>{@link io.thatworked.support.report.api.mapper.ReportDtoMapper} - 
 *       Comprehensive mapper handling all report-related conversions including
 *       request to command mapping, domain to response transformation, and
 *       HATEOAS link generation</li>
 * </ul>
 * 
 * <p>Mapping responsibilities:
 * <ul>
 *   <li>Request DTO to application command conversion</li>
 *   <li>Domain model to response DTO transformation</li>
 *   <li>Collection mapping with pagination metadata</li>
 *   <li>HATEOAS link generation based on resource state</li>
 *   <li>Error response formatting</li>
 * </ul>
 * 
 * <p>Conversion features:
 * <ul>
 *   <li>Null-safe mapping with Optional handling</li>
 *   <li>Date/time format conversions (domain Instant to API ISO strings)</li>
 *   <li>Enum mapping with validation</li>
 *   <li>Collection transformations with streaming</li>
 *   <li>Context-aware mapping (user permissions, base URLs)</li>
 * </ul>
 * 
 * <p>HATEOAS link generation:
 * <ul>
 *   <li>Self links for all resources</li>
 *   <li>Download links for completed reports</li>
 *   <li>Regenerate links for failed reports</li>
 *   <li>Cancel links for in-progress reports</li>
 *   <li>Navigation links for paginated results</li>
 * </ul>
 * 
 * <p>Design principles:
 * <ul>
 *   <li>Stateless mappers as Spring components</li>
 *   <li>Constructor injection for dependencies</li>
 *   <li>Immutable DTO creation</li>
 *   <li>Defensive copying of collections</li>
 *   <li>Clear method naming (toResponse, toCommand)</li>
 * </ul>
 */
package io.thatworked.support.report.api.mapper;