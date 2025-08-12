/**
 * API layer for the search service.
 * 
 * <p>This layer handles HTTP REST communication and request/response mapping.
 * It follows RESTful principles and provides a clean API interface.</p>
 * 
 * <h2>Key Components:</h2>
 * <ul>
 *   <li>{@link io.thatworked.support.search.api.controller} - REST controllers</li>
 *   <li>{@link io.thatworked.support.search.api.dto.request} - Request DTOs</li>
 *   <li>{@link io.thatworked.support.search.api.dto.response} - Response DTOs</li>
 *   <li>{@link io.thatworked.support.search.api.mapper} - API model mappers</li>
 *   <li>{@link io.thatworked.support.search.api.exception} - Exception handling</li>
 * </ul>
 * 
 * <h2>API Endpoints:</h2>
 * <ul>
 *   <li>GET /api/v1/search - Global search across all types</li>
 *   <li>GET /api/v1/search/{type} - Type-specific search</li>
 *   <li>DELETE /api/v1/cache - Clear all cache</li>
 *   <li>DELETE /api/v1/cache/{key} - Clear specific cache key</li>
 *   <li>GET /api/v1/health - Health check</li>
 * </ul>
 * 
 * <h2>Design Principles:</h2>
 * <ul>
 *   <li>RESTful API design with proper HTTP status codes</li>
 *   <li>RFC 7807 Problem Details for error responses</li>
 *   <li>Request validation using Bean Validation</li>
 *   <li>Constructor-based dependency injection</li>
 *   <li>Structured logging for all requests</li>
 * </ul>
 */
package io.thatworked.support.search.api;