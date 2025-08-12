/**
 * Mapper components for bidirectional conversion between layers.
 * 
 * <p>This package contains mapper classes that handle conversions between domain
 * models and infrastructure representations (entities, DTOs). These mappers
 * maintain clean separation between layers by ensuring domain models never
 * directly depend on infrastructure types and vice versa.
 * 
 * <p>Entity mappers:
 * <ul>
 *   <li>{@link io.thatworked.support.report.infrastructure.mapper.ReportEntityMapper} - 
 *       Bidirectional mapping between Report domain model and ReportEntity,
 *       handling complex type conversions and nested object mapping</li>
 *   <li>{@link io.thatworked.support.report.infrastructure.mapper.ScheduledReportEntityMapper} - 
 *       Maps ScheduledReport domain objects to/from database entities,
 *       including cron expression validation and recipient list handling</li>
 * </ul>
 * 
 * <p>Conversion responsibilities:
 * <ul>
 *   <li>Time zone handling - UTC storage with local time presentation</li>
 *   <li>UUID conversions between String and UUID types</li>
 *   <li>JSON serialization for complex fields (parameters, metadata)</li>
 *   <li>Enum mapping with fallback handling for unknown values</li>
 *   <li>Collection transformations (Set to List conversions)</li>
 * </ul>
 * 
 * <p>Mapping strategies:
 * <ul>
 *   <li>Null-safe conversions with Optional handling</li>
 *   <li>Immutable object construction from entity data</li>
 *   <li>Deep copying to prevent shared references</li>
 *   <li>Lazy loading awareness for JPA proxies</li>
 *   <li>Performance optimization through caching</li>
 * </ul>
 * 
 * <p>Error handling:
 * <ul>
 *   <li>Validation during mapping to catch data issues early</li>
 *   <li>Clear exception messages for debugging</li>
 *   <li>Graceful handling of missing or corrupt data</li>
 *   <li>Logging of mapping operations for troubleshooting</li>
 * </ul>
 * 
 * <p>Best practices:
 * <ul>
 *   <li>Stateless mappers for thread safety</li>
 *   <li>No business logic - pure data transformation</li>
 *   <li>Consistent naming conventions (toEntity, toDomain)</li>
 *   <li>Unit tested with edge cases</li>
 * </ul>
 */
package io.thatworked.support.report.infrastructure.mapper;