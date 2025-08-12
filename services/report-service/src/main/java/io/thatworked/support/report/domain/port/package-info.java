/**
 * Port interfaces defining contracts for external dependencies.
 * 
 * <p>This package implements the Ports and Adapters (Hexagonal) architecture pattern,
 * defining interfaces that the domain requires from the outside world. These ports
 * enable the domain to remain independent of infrastructure concerns while still
 * accessing required data and services.
 * 
 * <p>Data access ports:
 * <ul>
 *   <li>{@link io.thatworked.support.report.domain.port.DeviceDataPort} - Contract for
 *       accessing device information from device-service</li>
 *   <li>{@link io.thatworked.support.report.domain.port.PingDataPort} - Contract for
 *       retrieving ping statistics and uptime data from ping-service</li>
 *   <li>{@link io.thatworked.support.report.domain.port.AlertDataPort} - Contract for
 *       accessing alert history and statistics from alert-service</li>
 * </ul>
 * 
 * <p>Service ports:
 * <ul>
 *   <li>{@link io.thatworked.support.report.domain.port.ReportGeneratorPort} - Contract
 *       for generating report content in various formats (PDF, CSV, JSON)</li>
 *   <li>{@link io.thatworked.support.report.domain.port.FileStoragePort} - Contract for
 *       persisting and retrieving generated report files</li>
 *   <li>{@link io.thatworked.support.report.domain.port.DomainLogger} - Structured
 *       logging abstraction for domain events and errors</li>
 * </ul>
 * 
 * <p>Design principles:
 * <ul>
 *   <li>Interfaces use domain language and models</li>
 *   <li>No infrastructure types in method signatures</li>
 *   <li>Single responsibility per port interface</li>
 *   <li>Async operations return CompletableFuture for non-blocking I/O</li>
 * </ul>
 * 
 * <p>Implementation notes:
 * <ul>
 *   <li>Adapters in the infrastructure layer implement these ports</li>
 *   <li>Domain services depend on port interfaces, not implementations</li>
 *   <li>Dependency injection wires adapters to ports at runtime</li>
 * </ul>
 */
package io.thatworked.support.report.domain.port;