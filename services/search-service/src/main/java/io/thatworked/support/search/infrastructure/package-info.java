/**
 * Infrastructure layer for the search service.
 * 
 * <p>This layer contains technical implementations of domain ports and external integrations.
 * It handles all infrastructure concerns such as databases, external services, and frameworks.</p>
 * 
 * <h2>Key Components:</h2>
 * <ul>
 *   <li>{@link io.thatworked.support.search.infrastructure.adapter} - Port adapters implementing domain interfaces</li>
 *   <li>{@link io.thatworked.support.search.infrastructure.client} - External service clients (Feign)</li>
 *   <li>{@link io.thatworked.support.search.infrastructure.config} - Infrastructure configuration</li>
 *   <li>{@link io.thatworked.support.search.infrastructure.dto} - Data transfer objects for external communication</li>
 *   <li>{@link io.thatworked.support.search.infrastructure.mapper} - Mappers between domain models and DTOs</li>
 * </ul>
 * 
 * <h2>Design Principles:</h2>
 * <ul>
 *   <li>Implements domain ports to satisfy dependency inversion</li>
 *   <li>Handles all technical concerns and framework integrations</li>
 *   <li>Maps between external representations and domain models</li>
 *   <li>Provides fallback implementations for resilience</li>
 * </ul>
 */
package io.thatworked.support.search.infrastructure;