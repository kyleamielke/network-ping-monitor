/**
 * REST API layer exposing report service capabilities via HTTP endpoints.
 * 
 * <p>This layer provides the external HTTP interface to the report service,
 * implementing RESTful endpoints for report generation, management, and retrieval.
 * It follows Clean Architecture principles by depending only on the application
 * layer, ensuring a clean separation between HTTP concerns and business logic.
 * 
 * <p>Layer organization:
 * <ul>
 *   <li>{@link io.thatworked.support.report.api.controller} - REST controllers
 *       implementing HTTP endpoints with proper status codes and error handling</li>
 *   <li>{@link io.thatworked.support.report.api.dto.request} - Request DTOs
 *       with validation annotations for input sanitization</li>
 *   <li>{@link io.thatworked.support.report.api.dto.response} - Response DTOs
 *       providing consistent API responses with HATEOAS links</li>
 *   <li>{@link io.thatworked.support.report.api.mapper} - Bidirectional mappers
 *       between API DTOs and application layer objects</li>
 *   <li>{@link io.thatworked.support.report.api.exception} - Global exception
 *       handling with standardized error responses</li>
 * </ul>
 * 
 * <p>API design principles:
 * <ul>
 *   <li>RESTful resource modeling with proper HTTP verbs</li>
 *   <li>Consistent URL patterns (/api/v1/reports)</li>
 *   <li>JSON as the primary data exchange format</li>
 *   <li>ISO 8601 date/time formatting</li>
 *   <li>Pagination for list endpoints</li>
 *   <li>Comprehensive input validation</li>
 * </ul>
 * 
 * <p>Core endpoints:
 * <ul>
 *   <li>POST /api/v1/reports - Generate new report</li>
 *   <li>GET /api/v1/reports/{id} - Retrieve report metadata</li>
 *   <li>GET /api/v1/reports - List reports with filtering</li>
 *   <li>GET /api/v1/reports/{id}/download - Download report file</li>
 *   <li>DELETE /api/v1/reports/{id} - Delete report</li>
 *   <li>POST /api/v1/scheduled-reports - Schedule recurring report</li>
 * </ul>
 * 
 * <p>Cross-cutting concerns:
 * <ul>
 *   <li>Request/response logging for debugging</li>
 *   <li>API versioning strategy (URL path versioning)</li>
 *   <li>OpenAPI/Swagger documentation generation</li>
 *   <li>CORS configuration for browser clients</li>
 *   <li>Rate limiting for resource protection</li>
 * </ul>
 */
package io.thatworked.support.report.api;