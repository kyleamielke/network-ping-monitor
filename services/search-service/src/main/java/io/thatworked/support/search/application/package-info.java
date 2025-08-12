/**
 * Application layer for the search service.
 * 
 * <p>This layer contains use cases that orchestrate domain logic and coordinate
 * between the domain layer and infrastructure layer. It implements the application's
 * business workflows.</p>
 * 
 * <h2>Key Components:</h2>
 * <ul>
 *   <li>{@link io.thatworked.support.search.application.usecase} - Use case implementations</li>
 *   <li>{@link io.thatworked.support.search.application.config} - Application layer configuration</li>
 * </ul>
 * 
 * <h2>Use Cases:</h2>
 * <ul>
 *   <li>GlobalSearchUseCase - Searches across all entity types</li>
 *   <li>TypedSearchUseCase - Searches within a specific entity type</li>
 *   <li>ClearCacheUseCase - Manages search result caching</li>
 *   <li>CheckHealthUseCase - Monitors search provider health</li>
 * </ul>
 * 
 * <h2>Design Principles:</h2>
 * <ul>
 *   <li>Thin orchestration layer - delegates business logic to domain</li>
 *   <li>Transaction boundaries defined here</li>
 *   <li>Use case per operation pattern</li>
 *   <li>Spring Service annotations for dependency injection</li>
 * </ul>
 */
package io.thatworked.support.search.application;