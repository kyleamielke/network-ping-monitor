/**
 * Kafka message consumers for handling external events.
 * Processes events from other services to maintain data consistency.
 * 
 * <p>Consumer components:
 * <ul>
 *   <li>{@link io.thatworked.support.ping.infrastructure.consumer.DeviceEventConsumer} - Handles device lifecycle events</li>
 * </ul>
 */
package io.thatworked.support.ping.infrastructure.consumer;