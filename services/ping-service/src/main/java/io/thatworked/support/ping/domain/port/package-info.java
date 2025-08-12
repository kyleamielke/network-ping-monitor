/**
 * Port interfaces defining contracts for external dependencies.
 * Following hexagonal architecture, these ports allow the domain to remain independent.
 * 
 * <p>Port interfaces:
 * <ul>
 *   <li>{@link io.thatworked.support.ping.domain.port.PingTargetRepository} - Ping target persistence</li>
 *   <li>{@link io.thatworked.support.ping.domain.port.PingResultRepository} - Ping result persistence</li>
 *   <li>{@link io.thatworked.support.ping.domain.port.AlertStateRepository} - Alert state persistence</li>
 *   <li>{@link io.thatworked.support.ping.domain.port.PingExecutor} - Ping execution operations</li>
 *   <li>{@link io.thatworked.support.ping.domain.port.EventPublisher} - Domain event publishing</li>
 *   <li>{@link io.thatworked.support.ping.domain.port.DeviceClient} - Device service integration</li>
 *   <li>{@link io.thatworked.support.ping.domain.port.DomainLogger} - Domain-specific logging</li>
 * </ul>
 */
package io.thatworked.support.ping.domain.port;