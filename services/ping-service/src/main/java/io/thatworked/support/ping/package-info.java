/**
 * Root package for the ping monitoring service.
 * This service handles ICMP ping operations and network device monitoring.
 * 
 * <p>The service follows Clean Architecture principles with the following layers:
 * <ul>
 *   <li>{@link io.thatworked.support.ping.api} - REST API layer</li>
 *   <li>{@link io.thatworked.support.ping.application} - Application services and use cases</li>
 *   <li>{@link io.thatworked.support.ping.domain} - Core business domain</li>
 *   <li>{@link io.thatworked.support.ping.infrastructure} - Technical infrastructure</li>
 * </ul>
 */
package io.thatworked.support.ping;