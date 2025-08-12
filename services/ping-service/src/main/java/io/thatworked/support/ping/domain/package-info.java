/**
 * Domain layer containing the core business logic of the ping monitoring service.
 * This layer has no dependencies on external frameworks or infrastructure.
 * 
 * <p>Key components:
 * <ul>
 *   <li>{@link io.thatworked.support.ping.domain.model} - Pure domain models</li>
 *   <li>{@link io.thatworked.support.ping.domain.port} - Port interfaces for external dependencies</li>
 *   <li>{@link io.thatworked.support.ping.domain.service} - Domain services with business logic</li>
 *   <li>{@link io.thatworked.support.ping.domain.exception} - Domain-specific exceptions</li>
 * </ul>
 * 
 * <p>Core domain entities:
 * <ul>
 *   <li>{@link io.thatworked.support.ping.domain.PingTarget} - Device monitoring configuration</li>
 *   <li>{@link io.thatworked.support.ping.domain.PingResult} - Ping execution results</li>
 *   <li>{@link io.thatworked.support.ping.domain.AlertState} - Device alert state tracking</li>
 * </ul>
 */
package io.thatworked.support.ping.domain;