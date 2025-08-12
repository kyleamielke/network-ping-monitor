/**
 * Kafka consumers for processing external events.
 * 
 * Event consumers that trigger notifications:
 * - DeviceAlertConsumer: Processes device down/up alerts
 * - Event DTOs: DeviceAlertEvent, DeviceDownEvent, DeviceRecoveredEvent
 */
package io.thatworked.support.notification.infrastructure.consumer;