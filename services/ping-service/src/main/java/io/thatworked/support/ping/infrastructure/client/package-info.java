/**
 * External service client implementations.
 * Handles integration with other microservices and external systems.
 * 
 * <p>Client components:
 * <ul>
 *   <li>{@link io.thatworked.support.ping.infrastructure.client.DeviceClient} - Device service HTTP client</li>
 *   <li>{@link io.thatworked.support.ping.infrastructure.client.DeviceClientAdapter} - Device client adapter</li>
 *   <li>{@link io.thatworked.support.ping.infrastructure.client.DeviceClientFallback} - Circuit breaker fallback</li>
 * </ul>
 */
package io.thatworked.support.ping.infrastructure.client;