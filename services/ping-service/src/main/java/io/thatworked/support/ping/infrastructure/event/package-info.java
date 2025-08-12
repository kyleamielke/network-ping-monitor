/**
 * Event handling and event definitions for the ping service.
 * Contains event classes and handlers for domain events.
 * 
 * <p>Event components:
 * <ul>
 *   <li>{@link io.thatworked.support.ping.infrastructure.event.PingEventHandler} - Handles ping-related events</li>
 *   <li>{@link io.thatworked.support.ping.infrastructure.event.PingResultEvent} - Ping execution result event</li>
 *   <li>{@link io.thatworked.support.ping.infrastructure.event.PingTargetStartedEvent} - Monitoring started event</li>
 *   <li>{@link io.thatworked.support.ping.infrastructure.event.PingTargetStoppedEvent} - Monitoring stopped event</li>
 *   <li>{@link io.thatworked.support.ping.infrastructure.event.alert} - Alert-related events</li>
 * </ul>
 */
package io.thatworked.support.ping.infrastructure.event;