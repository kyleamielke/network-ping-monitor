/**
 * Alert event definitions for device monitoring state changes.
 * These events are published when devices transition between healthy and unhealthy states.
 * 
 * <p>Alert events:
 * <ul>
 *   <li>{@link io.thatworked.support.ping.infrastructure.event.alert.DeviceAlertEvent} - Base alert event</li>
 *   <li>{@link io.thatworked.support.ping.infrastructure.event.alert.DeviceDownEvent} - Device failure event</li>
 *   <li>{@link io.thatworked.support.ping.infrastructure.event.alert.DeviceRecoveredEvent} - Device recovery event</li>
 * </ul>
 */
package io.thatworked.support.ping.infrastructure.event.alert;