/**
 * Notification service for the NetworkPing Monitor system.
 * 
 * This service handles sending notifications through various channels (email, Slack, Teams, etc.)
 * when monitoring events occur. It follows clean architecture principles with clear separation
 * between domain, application, infrastructure, and API layers.
 * 
 * Key features:
 * - Multi-channel notification support
 * - Event-driven architecture using Kafka
 * - Notification history tracking
 * - Template-based email notifications
 * - Extensible channel support
 */
package io.thatworked.support.notification;