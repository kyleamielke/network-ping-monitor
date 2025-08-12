/**
 * Adapter implementations of domain ports.
 * 
 * Concrete implementations that bridge domain and infrastructure:
 * - EmailNotificationSender: Email implementation of NotificationSender
 * - NotificationRepositoryAdapter: JPA implementation of NotificationRepository
 * - KafkaEventPublisher: Kafka implementation of EventPublisher
 * - StructuredDomainLogger: Structured logging implementation of DomainLogger
 */
package io.thatworked.support.notification.infrastructure.adapter;