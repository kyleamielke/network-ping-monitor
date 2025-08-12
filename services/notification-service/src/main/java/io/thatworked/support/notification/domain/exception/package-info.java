/**
 * Domain exceptions for notification business logic.
 * 
 * Custom exceptions representing domain-specific error conditions:
 * - NotificationDomainException: Base exception for all domain errors
 * - NotificationSendException: Thrown when notification sending fails
 * - UnsupportedChannelException: Thrown for unsupported notification channels
 * - InvalidNotificationRequestException: Thrown for invalid notification requests
 * - NotificationRepositoryException: Thrown when repository operations fail
 * - EventPublishingException: Thrown when event publishing fails
 * 
 * These exceptions ensure that infrastructure concerns don't leak into
 * the domain layer, maintaining clean architecture principles.
 */
package io.thatworked.support.notification.domain.exception;