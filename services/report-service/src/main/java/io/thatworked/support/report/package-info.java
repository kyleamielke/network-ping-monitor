/**
 * Report Service - Comprehensive report generation and management microservice.
 * 
 * <p>The Report Service is responsible for generating, storing, and managing various
 * types of reports for the network monitoring system. It aggregates data from multiple
 * microservices (device, ping, alert) to produce comprehensive reports in multiple
 * formats (PDF, CSV, JSON).
 * 
 * <p>Service capabilities:
 * <ul>
 *   <li>On-demand report generation with configurable parameters</li>
 *   <li>Scheduled report generation with cron-based execution</li>
 *   <li>Multiple report types (device uptime, ping statistics, alert summary)</li>
 *   <li>Multiple output formats with professional formatting</li>
 *   <li>Asynchronous generation for long-running reports</li>
 *   <li>Report storage and retrieval with metadata</li>
 * </ul>
 * 
 * <p>Architecture layers:
 * <ul>
 *   <li>{@link io.thatworked.support.report.domain} - Core business logic and domain models</li>
 *   <li>{@link io.thatworked.support.report.application} - Use cases and application services</li>
 *   <li>{@link io.thatworked.support.report.infrastructure} - Technical implementations and adapters</li>
 *   <li>{@link io.thatworked.support.report.api} - REST API endpoints and DTOs</li>
 *   <li>{@link io.thatworked.support.report.config} - Service configuration and initialization</li>
 * </ul>
 * 
 * <p>Clean Architecture principles:
 * <ul>
 *   <li>Domain layer has no external dependencies</li>
 *   <li>Application layer depends only on domain</li>
 *   <li>Infrastructure implements domain and application ports</li>
 *   <li>API layer depends only on application layer</li>
 *   <li>Dependency injection wires everything together</li>
 * </ul>
 * 
 * <p>External integrations:
 * <ul>
 *   <li>Device Service - Device inventory and metadata</li>
 *   <li>Ping Service - Ping results and uptime statistics</li>
 *   <li>Alert Service - Alert history and counts</li>
 *   <li>PostgreSQL - Report metadata persistence</li>
 *   <li>File Storage - Generated report file storage</li>
 * </ul>
 * 
 * <p>Key design patterns:
 * <ul>
 *   <li>Hexagonal Architecture (Ports and Adapters)</li>
 *   <li>Domain-Driven Design for rich domain models</li>
 *   <li>Repository pattern for data access</li>
 *   <li>Strategy pattern for format-specific generators</li>
 *   <li>Event-driven notifications on report completion</li>
 * </ul>
 * 
 * @since 1.0.0
 */
package io.thatworked.support.report;