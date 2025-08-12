/**
 * HTTP clients and adapters for external service integration.
 * 
 * <p>This package contains Feign clients and adapters that communicate with other
 * microservices in the system to gather data for report generation. Each client
 * is designed with resilience patterns including circuit breakers, retries, and
 * fallbacks to ensure report generation can proceed even with partial failures.
 * 
 * <p>Service clients:
 * <ul>
 *   <li>{@link io.thatworked.support.report.infrastructure.client.DeviceServiceClient} - 
 *       Feign client for device-service API, retrieving device inventory and details</li>
 *   <li>{@link io.thatworked.support.report.infrastructure.client.PingServiceClient} - 
 *       Feign client for ping-service API, fetching ping statistics and uptime data</li>
 *   <li>{@link io.thatworked.support.report.infrastructure.client.AlertServiceClient} - 
 *       Feign client for alert-service API, accessing alert history and counts</li>
 *   <li>{@link io.thatworked.support.report.infrastructure.client.PingTargetClient} - 
 *       Specialized client for ping target configuration data</li>
 * </ul>
 * 
 * <p>Data adapters:
 * <ul>
 *   <li>{@link io.thatworked.support.report.infrastructure.client.DeviceDataAdapter} - 
 *       Implements DeviceDataPort by delegating to DeviceServiceClient</li>
 *   <li>{@link io.thatworked.support.report.infrastructure.client.PingDataAdapter} - 
 *       Implements PingDataPort by aggregating data from multiple ping endpoints</li>
 *   <li>{@link io.thatworked.support.report.infrastructure.client.AlertDataAdapter} - 
 *       Implements AlertDataPort with alert statistics calculations</li>
 * </ul>
 * 
 * <p>Fallback implementations:
 * <ul>
 *   <li>{@link io.thatworked.support.report.infrastructure.client.DeviceServiceClientFallback} - 
 *       Provides cached or default device data when service is unavailable</li>
 *   <li>{@link io.thatworked.support.report.infrastructure.client.PingServiceClientFallback} - 
 *       Returns partial ping data or cached results during outages</li>
 * </ul>
 * 
 * <p>Resilience patterns:
 * <ul>
 *   <li>Circuit breaker to prevent cascading failures</li>
 *   <li>Retry with exponential backoff for transient errors</li>
 *   <li>Fallback responses for graceful degradation</li>
 *   <li>Request timeouts to prevent blocking</li>
 *   <li>Bulkhead isolation for concurrent requests</li>
 * </ul>
 * 
 * <p>Configuration:
 * <ul>
 *   <li>Service discovery via Consul or Eureka</li>
 *   <li>Load balancing across service instances</li>
 *   <li>Request/response logging for debugging</li>
 *   <li>Metrics collection for monitoring</li>
 * </ul>
 */
package io.thatworked.support.report.infrastructure.client;