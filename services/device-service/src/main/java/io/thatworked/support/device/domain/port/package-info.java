/**
 * Domain ports (interfaces) for external dependencies.
 * 
 * Defines contracts that infrastructure must implement:
 * - Repository interfaces for data persistence
 * - Event publisher interfaces for messaging
 * - Query interfaces for read operations
 * 
 * Infrastructure adapters implement these ports.
 */
package io.thatworked.support.device.domain.port;