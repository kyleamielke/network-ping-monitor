/**
 * Search Service - Provides unified search capabilities across the support system.
 * 
 * <p>This service implements a search aggregator pattern that queries multiple backend services
 * to provide a unified search experience. It follows Clean Architecture principles with clear
 * separation between domain logic, application orchestration, and infrastructure concerns.</p>
 * 
 * <h2>Architecture Layers:</h2>
 * <ul>
 *   <li>{@link io.thatworked.support.search.domain} - Core business logic and domain models</li>
 *   <li>{@link io.thatworked.support.search.application} - Use cases and orchestration</li>
 *   <li>{@link io.thatworked.support.search.infrastructure} - Technical implementations</li>
 *   <li>{@link io.thatworked.support.search.api} - REST API endpoints</li>
 * </ul>
 * 
 * <h2>Key Features:</h2>
 * <ul>
 *   <li>Global search across devices, alerts, and reports</li>
 *   <li>Type-specific search with optimized queries</li>
 *   <li>Result caching for improved performance</li>
 *   <li>Relevance scoring and ranking</li>
 *   <li>Parallel search execution</li>
 *   <li>Pattern detection (IP addresses, MAC addresses, asset tags)</li>
 * </ul>
 * 
 * <h2>Integration Points:</h2>
 * <ul>
 *   <li>Device Service - via Feign client</li>
 *   <li>Alert Service - via Feign client (future)</li>
 *   <li>Report Service - via Feign client (future)</li>
 *   <li>Service Discovery - via Consul</li>
 *   <li>Event Bus - via Kafka (future enhancement)</li>
 * </ul>
 * 
 * @since 1.0.0
 */
package io.thatworked.support.search;