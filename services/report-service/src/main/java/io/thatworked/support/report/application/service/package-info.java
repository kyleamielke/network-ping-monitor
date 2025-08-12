/**
 * Application services providing high-level orchestration and coordination.
 * 
 * <p>Application services act as facades that coordinate multiple use cases,
 * manage complex workflows, and provide simplified interfaces for the API layer.
 * They handle cross-cutting concerns such as logging, monitoring, and caching
 * that span multiple use cases.
 * 
 * <p>Core services:
 * <ul>
 *   <li>{@link io.thatworked.support.report.application.service.ReportApplicationService} - 
 *       Primary orchestrator for report operations, providing a unified interface
 *       for report generation, scheduling, and management workflows</li>
 * </ul>
 * 
 * <p>Key responsibilities:
 * <ul>
 *   <li>Workflow orchestration across multiple use cases</li>
 *   <li>Transaction management for complex operations</li>
 *   <li>Caching strategies for performance optimization</li>
 *   <li>Event publishing for system integration</li>
 *   <li>Monitoring and metrics collection</li>
 * </ul>
 * 
 * <p>Integration patterns:
 * <ul>
 *   <li>Async report generation with status tracking</li>
 *   <li>Batch operations for bulk report management</li>
 *   <li>Event-driven notifications on report completion</li>
 *   <li>Circuit breaker for external service calls</li>
 * </ul>
 * 
 * <p>Design considerations:
 * <ul>
 *   <li>Stateless services for horizontal scalability</li>
 *   <li>Idempotent operations where possible</li>
 *   <li>Graceful degradation when dependencies fail</li>
 *   <li>Clear separation from domain logic</li>
 * </ul>
 */
package io.thatworked.support.report.application.service;