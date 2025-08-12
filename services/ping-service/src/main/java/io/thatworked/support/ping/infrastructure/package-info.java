/**
 * Infrastructure layer providing technical implementations for domain ports.
 * This layer handles all technical concerns like persistence, messaging, and external integrations.
 * 
 * <p>Key components:
 * <ul>
 *   <li>{@link io.thatworked.support.ping.infrastructure.adapter} - Port adapter implementations</li>
 *   <li>{@link io.thatworked.support.ping.infrastructure.client} - External service clients</li>
 *   <li>{@link io.thatworked.support.ping.infrastructure.config} - Configuration classes</li>
 *   <li>{@link io.thatworked.support.ping.infrastructure.consumer} - Message consumers</li>
 *   <li>{@link io.thatworked.support.ping.infrastructure.entity} - JPA entities</li>
 *   <li>{@link io.thatworked.support.ping.infrastructure.event} - Event handling</li>
 *   <li>{@link io.thatworked.support.ping.infrastructure.executor} - Ping execution implementation</li>
 *   <li>{@link io.thatworked.support.ping.infrastructure.publisher} - Event publishing</li>
 *   <li>{@link io.thatworked.support.ping.infrastructure.repository} - Repository implementations</li>
 *   <li>{@link io.thatworked.support.ping.infrastructure.scheduler} - Scheduling infrastructure</li>
 * </ul>
 */
package io.thatworked.support.ping.infrastructure;