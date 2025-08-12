/**
 * Domain layer containing the core business logic of the report service.
 * This layer has no dependencies on external frameworks or infrastructure.
 * 
 * <p>The domain layer implements the core report generation and scheduling
 * capabilities, including device uptime reports, ping statistics analysis,
 * and scheduled report management. It follows Domain-Driven Design principles
 * with a focus on rich domain models and business invariants.
 * 
 * <p>Key components:
 * <ul>
 *   <li>{@link io.thatworked.support.report.domain.model} - Pure domain models 
 *       representing reports, schedules, and value objects</li>
 *   <li>{@link io.thatworked.support.report.domain.port} - Port interfaces defining
 *       contracts for external dependencies (repositories, clients)</li>
 *   <li>{@link io.thatworked.support.report.domain.service} - Domain services 
 *       encapsulating complex business logic and report generation algorithms</li>
 *   <li>{@link io.thatworked.support.report.domain.exception} - Domain-specific 
 *       exceptions for business rule violations</li>
 * </ul>
 * 
 * <p>Design principles:
 * <ul>
 *   <li>Zero framework dependencies - pure Java business logic</li>
 *   <li>Immutable value objects for data integrity</li>
 *   <li>Rich domain models with encapsulated behavior</li>
 *   <li>Explicit business rules through domain exceptions</li>
 * </ul>
 */
package io.thatworked.support.report.domain;