/**
 * Domain services encapsulating complex business logic and algorithms.
 * 
 * <p>Domain services contain business logic that doesn't naturally fit within a single
 * entity or value object. They orchestrate operations across multiple domain objects
 * and coordinate with external systems through port interfaces.
 * 
 * <p>Core services:
 * <ul>
 *   <li>{@link io.thatworked.support.report.domain.service.ReportDomainService} - 
 *       Orchestrates report generation including data aggregation, statistical
 *       calculations, and content formatting</li>
 * </ul>
 * 
 * <p>Key responsibilities:
 * <ul>
 *   <li>Complex business logic spanning multiple entities</li>
 *   <li>Coordination of data retrieval from multiple sources</li>
 *   <li>Business rule validation and enforcement</li>
 *   <li>Statistical calculations and data aggregation</li>
 *   <li>Report generation algorithms and formatting logic</li>
 * </ul>
 * 
 * <p>Design patterns:
 * <ul>
 *   <li>Stateless services with no mutable state</li>
 *   <li>Dependencies injected through constructor</li>
 *   <li>Operations return domain objects or primitives</li>
 *   <li>Exceptions for business rule violations</li>
 * </ul>
 * 
 * <p>Example workflows:
 * <ul>
 *   <li>Device uptime calculation across time ranges</li>
 *   <li>Ping statistics aggregation with percentile analysis</li>
 *   <li>Alert correlation and trend detection</li>
 *   <li>Multi-format report generation (PDF, CSV, JSON)</li>
 * </ul>
 */
package io.thatworked.support.report.domain.service;