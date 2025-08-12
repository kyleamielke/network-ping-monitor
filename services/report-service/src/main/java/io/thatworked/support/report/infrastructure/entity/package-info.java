/**
 * JPA entity classes for database persistence.
 * 
 * <p>This package contains JPA-annotated entity classes that map domain models
 * to relational database tables. These entities are strictly infrastructure
 * concerns and are never exposed outside the infrastructure layer. Domain models
 * are converted to/from entities at the repository adapter boundary.
 * 
 * <p>Core entities:
 * <ul>
 *   <li>{@link io.thatworked.support.report.infrastructure.entity.ReportEntity} - 
 *       Maps to 'reports' table, storing report metadata, generation status,
 *       file location, and execution statistics</li>
 *   <li>{@link io.thatworked.support.report.infrastructure.entity.ScheduledReportEntity} - 
 *       Maps to 'scheduled_reports' table, storing recurring report configurations
 *       with cron expressions and recipient lists</li>
 * </ul>
 * 
 * <p>Entity features:
 * <ul>
 *   <li>UUID primary keys for distributed system compatibility</li>
 *   <li>Optimistic locking with @Version for concurrent updates</li>
 *   <li>Audit fields (createdAt, updatedAt) with @EntityListeners</li>
 *   <li>Lazy loading strategies for performance</li>
 *   <li>Database indexes on frequently queried fields</li>
 * </ul>
 * 
 * <p>Mapping strategies:
 * <ul>
 *   <li>@Enumerated for type-safe enum persistence</li>
 *   <li>@ElementCollection for list fields (recipients)</li>
 *   <li>@Embedded for value object mapping</li>
 *   <li>@Convert for custom type conversions</li>
 *   <li>@Column with constraints matching domain rules</li>
 * </ul>
 * 
 * <p>Performance optimizations:
 * <ul>
 *   <li>Composite indexes for common query patterns</li>
 *   <li>Fetch strategies to prevent N+1 queries</li>
 *   <li>Column definitions for optimal data types</li>
 *   <li>Query hints for read-only operations</li>
 * </ul>
 * 
 * <p>Design principles:
 * <ul>
 *   <li>Entities are anemic - business logic stays in domain</li>
 *   <li>No domain dependencies - pure infrastructure</li>
 *   <li>Immutable where possible (setter protection)</li>
 *   <li>Clear separation from domain models</li>
 * </ul>
 */
package io.thatworked.support.report.infrastructure.entity;