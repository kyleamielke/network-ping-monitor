/**
 * Spring Data JPA repository interfaces for database operations.
 * 
 * <p>This package contains Spring Data JPA repository interfaces that provide
 * CRUD operations and custom queries for report entities. These interfaces
 * leverage Spring Data's repository abstraction to minimize boilerplate code
 * while providing powerful query capabilities.
 * 
 * <p>Repository interfaces:
 * <ul>
 *   <li>{@link io.thatworked.support.report.infrastructure.repository.JpaReportRepository} - 
 *       Manages Report entities with custom queries for filtering by type,
 *       date range, status, and device. Includes pagination support</li>
 *   <li>{@link io.thatworked.support.report.infrastructure.repository.JpaScheduledReportRepository} - 
 *       Handles ScheduledReport entities with queries for finding active
 *       schedules and reports due for execution</li>
 * </ul>
 * 
 * <p>Query methods:
 * <ul>
 *   <li>findByType - Filter reports by report type</li>
 *   <li>findByCreatedBetween - Date range queries with indexes</li>
 *   <li>findByStatus - Reports in specific generation states</li>
 *   <li>findDueReports - Scheduled reports ready for execution</li>
 *   <li>deleteOlderThan - Batch cleanup of expired reports</li>
 * </ul>
 * 
 * <p>Advanced features:
 * <ul>
 *   <li>@Query annotations for complex JPQL queries</li>
 *   <li>@Modifying for bulk update/delete operations</li>
 *   <li>Pageable support for large result sets</li>
 *   <li>Sort specifications for flexible ordering</li>
 *   <li>Projection interfaces for partial entity loading</li>
 * </ul>
 * 
 * <p>Performance optimizations:
 * <ul>
 *   <li>Query hints for read-only operations</li>
 *   <li>Fetch joins to prevent N+1 queries</li>
 *   <li>Native queries for complex aggregations</li>
 *   <li>Query result caching for frequently accessed data</li>
 * </ul>
 * 
 * <p>Transaction handling:
 * <ul>
 *   <li>Inherited transactional behavior from Spring Data</li>
 *   <li>Custom transaction boundaries with @Transactional</li>
 *   <li>Optimistic locking support via @Version</li>
 *   <li>Batch operations within single transactions</li>
 * </ul>
 */
package io.thatworked.support.report.infrastructure.repository;