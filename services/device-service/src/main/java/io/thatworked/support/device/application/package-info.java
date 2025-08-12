/**
 * Application layer - Use cases and orchestration.
 * 
 * Coordinates between domain and infrastructure:
 * - Use case implementations
 * - Application services
 * - Query services for read operations
 * - Transaction orchestration
 * 
 * Can use Spring annotations and common infrastructure
 * like logging, but no direct database or messaging access.
 */
package io.thatworked.support.device.application;