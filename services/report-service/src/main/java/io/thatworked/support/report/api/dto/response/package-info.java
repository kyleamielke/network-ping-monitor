/**
 * Response DTOs providing consistent API output with HATEOAS support.
 * 
 * <p>This package contains immutable Data Transfer Objects that represent
 * API responses. These DTOs are serialized to JSON with consistent formatting,
 * include HATEOAS links for discoverability, and provide a stable contract
 * for API consumers while hiding internal implementation details.
 * 
 * <p>Response DTOs:
 * <ul>
 *   <li>{@link io.thatworked.support.report.api.dto.response.ReportResponse} - 
 *       Complete report details including metadata, generation status, file info,
 *       and HATEOAS links for download and related operations</li>
 *   <li>{@link io.thatworked.support.report.api.dto.response.ReportListResponse} - 
 *       Paginated collection of reports with metadata about total count,
 *       page size, and navigation links</li>
 *   <li>{@link io.thatworked.support.report.api.dto.response.ReportSummary} - 
 *       Lightweight report representation for list views, containing only
 *       essential fields to minimize payload size</li>
 *   <li>{@link io.thatworked.support.report.api.dto.response.ReportStatusResponse} - 
 *       Real-time status updates for async report generation including
 *       progress percentage and estimated completion time</li>
 *   <li>{@link io.thatworked.support.report.api.dto.response.ScheduledReportResponse} - 
 *       Scheduled report configuration with next execution time and
 *       execution history summary</li>
 * </ul>
 * 
 * <p>JSON serialization features:
 * <ul>
 *   <li>@JsonInclude(NON_NULL) to omit null fields</li>
 *   <li>@JsonFormat for ISO 8601 date/time formatting</li>
 *   <li>@JsonProperty for API field naming conventions</li>
 *   <li>@JsonView for different detail levels</li>
 *   <li>Custom serializers for complex types</li>
 * </ul>
 * 
 * <p>HATEOAS implementation:
 * <ul>
 *   <li>Self links for resource identification</li>
 *   <li>Related resource links (download, regenerate)</li>
 *   <li>Collection navigation (next, previous, first, last)</li>
 *   <li>Action links based on resource state</li>
 * </ul>
 * 
 * <p>Response patterns:
 * <ul>
 *   <li>Consistent envelope structure for collections</li>
 *   <li>Standard error response format</li>
 *   <li>Metadata sections for additional context</li>
 *   <li>ETag support for caching</li>
 *   <li>Version information in headers</li>
 * </ul>
 */
package io.thatworked.support.report.api.dto.response;