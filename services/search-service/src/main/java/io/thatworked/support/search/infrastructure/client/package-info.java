/**
 * Infrastructure clients for external service communication.
 * 
 * <p>Contains Feign clients and their fallback implementations for resilient
 * communication with other microservices.</p>
 * 
 * <h2>Service Clients:</h2>
 * <ul>
 *   <li>{@link io.thatworked.support.search.infrastructure.client.DeviceServiceClient} - Device service integration</li>
 *   <li>{@link io.thatworked.support.search.infrastructure.client.AlertServiceClient} - Alert service integration</li>
 *   <li>{@link io.thatworked.support.search.infrastructure.client.ReportServiceClient} - Report service integration</li>
 * </ul>
 * 
 * <h2>Design Patterns:</h2>
 * <ul>
 *   <li>Circuit Breaker pattern via Feign fallbacks</li>
 *   <li>Service discovery via Consul</li>
 *   <li>Load balancing for service instances</li>
 * </ul>
 */
package io.thatworked.support.search.infrastructure.client;