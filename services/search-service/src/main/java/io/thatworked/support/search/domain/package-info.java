/**
 * Domain layer for the search service.
 * 
 * <p>This package contains the core business logic and domain models for search functionality.
 * It follows Domain-Driven Design principles and is independent of any infrastructure concerns.</p>
 * 
 * <h2>Key Components:</h2>
 * <ul>
 *   <li>{@link io.thatworked.support.search.domain.model} - Domain models (SearchQuery, SearchResult, SearchItem)</li>
 *   <li>{@link io.thatworked.support.search.domain.service} - Domain services with business logic</li>
 *   <li>{@link io.thatworked.support.search.domain.port} - Port interfaces for external dependencies</li>
 *   <li>{@link io.thatworked.support.search.domain.exception} - Domain-specific exceptions</li>
 * </ul>
 * 
 * <h2>Design Principles:</h2>
 * <ul>
 *   <li>Framework-agnostic - no Spring or infrastructure dependencies</li>
 *   <li>Rich domain models with business logic encapsulation</li>
 *   <li>Immutable value objects for thread safety</li>
 *   <li>Port interfaces for dependency inversion</li>
 * </ul>
 */
package io.thatworked.support.search.domain;