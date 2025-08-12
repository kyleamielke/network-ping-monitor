/**
 * Application layer orchestrating use cases and coordinating domain operations.
 * This layer contains application-specific business logic and use case implementations.
 * 
 * <p>The application layer acts as the orchestrator between the API layer and the
 * domain layer, implementing use cases that represent user intentions and system
 * operations. It manages transactions, coordinates multiple domain operations,
 * and handles cross-cutting concerns.
 * 
 * <p>Key components:
 * <ul>
 *   <li>{@link io.thatworked.support.report.application.usecase} - Use case
 *       implementations representing system operations</li>
 *   <li>{@link io.thatworked.support.report.application.service} - Application
 *       services coordinating complex workflows</li>
 *   <li>{@link io.thatworked.support.report.application.port} - Output ports
 *       for persistence and external service integration</li>
 * </ul>
 * 
 * <p>Key principles:
 * <ul>
 *   <li>Use cases accept primitive types and simple DTOs as input</li>
 *   <li>Use cases return domain objects or simple values</li>
 *   <li>Transaction boundaries are defined at this layer</li>
 *   <li>Orchestrates calls to domain services and infrastructure</li>
 *   <li>Handles application-level validation and authorization</li>
 * </ul>
 * 
 * <p>Typical use cases:
 * <ul>
 *   <li>Generate device uptime report for date range</li>
 *   <li>Schedule recurring report generation</li>
 *   <li>List available reports with filtering</li>
 *   <li>Download report in specified format</li>
 *   <li>Cancel scheduled report generation</li>
 * </ul>
 * 
 * <p>Design patterns:
 * <ul>
 *   <li>Command pattern for use case inputs</li>
 *   <li>Repository pattern for data persistence</li>
 *   <li>Unit of Work for transaction management</li>
 *   <li>Dependency injection for loose coupling</li>
 * </ul>
 */
package io.thatworked.support.report.application;