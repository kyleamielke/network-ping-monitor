/**
 * Query adapters implementing domain search ports.
 * 
 * Contains infrastructure implementations for complex queries and searches:
 * - DeviceSearchAdapter: Implements DeviceSearchPort for advanced device searches
 * - Handles pagination, filtering, and sorting at the database level
 * - Converts between domain models and Spring Data structures
 */
package io.thatworked.support.device.infrastructure.adapter.query;