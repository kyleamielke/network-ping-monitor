/**
 * Application services implementing business workflows and orchestration.
 * These services coordinate domain services and infrastructure components.
 * 
 * <p>Application services:
 * <ul>
 *   <li>{@link io.thatworked.support.ping.application.service.PingTargetService} - Ping target management</li>
 *   <li>{@link io.thatworked.support.ping.application.service.VirtualThreadPingService} - Virtual thread-based ping execution</li>
 *   <li>{@link io.thatworked.support.ping.application.service.PingStatisticsService} - Statistics aggregation</li>
 *   <li>{@link io.thatworked.support.ping.application.service.PingReportService} - Report generation</li>
 *   <li>{@link io.thatworked.support.ping.application.service.AlertStateService} - Alert state management</li>
 * </ul>
 */
package io.thatworked.support.ping.application.service;