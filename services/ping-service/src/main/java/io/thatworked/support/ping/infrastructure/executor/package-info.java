/**
 * Ping execution infrastructure implementing high-performance ICMP operations.
 * Uses virtual threads for efficient concurrent ping execution.
 * 
 * <p>Executor components:
 * <ul>
 *   <li>{@link io.thatworked.support.ping.infrastructure.executor.VirtualThreadPingExecutor} - Virtual thread-based executor</li>
 *   <li>{@link io.thatworked.support.ping.infrastructure.executor.PingExecutorAdapter} - Adapter for domain port</li>
 *   <li>{@link io.thatworked.support.ping.infrastructure.executor.BatchPingProcessor} - Batch ping processing</li>
 *   <li>{@link io.thatworked.support.ping.infrastructure.executor.PingExecutionDelegate} - Ping execution delegation</li>
 *   <li>{@link io.thatworked.support.ping.infrastructure.executor.PingCircuitBreaker} - Circuit breaker for resilience</li>
 * </ul>
 */
package io.thatworked.support.ping.infrastructure.executor;