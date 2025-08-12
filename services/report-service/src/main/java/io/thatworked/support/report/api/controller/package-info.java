/**
 * REST controllers implementing HTTP endpoints for report operations.
 * 
 * <p>Controllers in this package handle HTTP requests, perform input validation,
 * delegate to application services, and format responses. They are responsible
 * for HTTP-specific concerns while business logic remains in the application layer.
 * 
 * <p>Controller classes:
 * <ul>
 *   <li>{@link io.thatworked.support.report.api.controller.ReportController} - 
 *       Handles report generation, retrieval, listing, downloading, and deletion.
 *       Implements both synchronous and asynchronous report generation endpoints</li>
 *   <li>{@link io.thatworked.support.report.api.controller.ScheduledReportController} - 
 *       Manages scheduled report configurations including creation, updates,
 *       and cancellation of recurring reports</li>
 * </ul>
 * 
 * <p>Controller responsibilities:
 * <ul>
 *   <li>HTTP request/response handling with proper status codes</li>
 *   <li>Input validation using Bean Validation annotations</li>
 *   <li>Request DTO to application command mapping</li>
 *   <li>Domain model to response DTO conversion</li>
 *   <li>Error handling and response formatting</li>
 * </ul>
 * 
 * <p>RESTful endpoint patterns:
 * <ul>
 *   <li>GET for resource retrieval (reports, lists)</li>
 *   <li>POST for resource creation (generate report)</li>
 *   <li>PUT for full resource updates</li>
 *   <li>PATCH for partial updates</li>
 *   <li>DELETE for resource removal</li>
 * </ul>
 * 
 * <p>Common features:
 * <ul>
 *   <li>@RestController for automatic JSON serialization</li>
 *   <li>@RequestMapping for consistent URL prefixes</li>
 *   <li>@Valid for request body validation</li>
 *   <li>ResponseEntity for flexible response control</li>
 *   <li>@ApiOperation for Swagger documentation</li>
 * </ul>
 * 
 * <p>Response patterns:
 * <ul>
 *   <li>201 Created with Location header for new resources</li>
 *   <li>200 OK for successful retrievals and updates</li>
 *   <li>204 No Content for successful deletions</li>
 *   <li>404 Not Found for missing resources</li>
 *   <li>400 Bad Request for validation errors</li>
 * </ul>
 */
package io.thatworked.support.report.api.controller;