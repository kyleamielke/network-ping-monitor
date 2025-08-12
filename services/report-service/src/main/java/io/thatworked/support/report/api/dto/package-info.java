/**
 * Data Transfer Objects for API request and response handling.
 * 
 * <p>This package is the parent for request and response DTOs used in the REST API.
 * DTOs provide a stable contract for API consumers while allowing internal domain
 * models to evolve independently. They include validation annotations and
 * JSON serialization directives.
 * 
 * <p>Sub-packages:
 * <ul>
 *   <li>{@link io.thatworked.support.report.api.dto.request} - Request DTOs
 *       for incoming API calls with validation rules</li>
 *   <li>{@link io.thatworked.support.report.api.dto.response} - Response DTOs
 *       for API responses with consistent structure</li>
 * </ul>
 * 
 * <p>DTO design principles:
 * <ul>
 *   <li>Immutable objects using records or final fields</li>
 *   <li>Clear separation from domain models</li>
 *   <li>Comprehensive validation annotations</li>
 *   <li>Self-documenting with Swagger annotations</li>
 *   <li>Null-safety with Optional or @Nullable</li>
 * </ul>
 */
package io.thatworked.support.report.api.dto;