/**
 * Repository implementations for data persistence.
 * Provides adapter implementations that bridge domain repositories with JPA repositories.
 * 
 * <p>Repository components:
 * <ul>
 *   <li>{@link io.thatworked.support.ping.infrastructure.repository.PingTargetRepositoryAdapter} - Ping target persistence</li>
 *   <li>{@link io.thatworked.support.ping.infrastructure.repository.PingResultRepositoryAdapter} - Ping result persistence</li>
 *   <li>{@link io.thatworked.support.ping.infrastructure.repository.AlertStateRepositoryAdapter} - Alert state persistence</li>
 *   <li>{@link io.thatworked.support.ping.infrastructure.repository.jpa} - JPA repository interfaces</li>
 * </ul>
 */
package io.thatworked.support.ping.infrastructure.repository;