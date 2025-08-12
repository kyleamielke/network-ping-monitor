/**
 * Adapter implementations of domain and application port interfaces.
 * 
 * <p>Adapters implement the port interfaces defined in the domain and application
 * layers, providing concrete implementations that integrate with external systems,
 * frameworks, and technical infrastructure. They act as the bridge between the
 * business logic and technical implementation details.
 * 
 * <p>Core adapters:
 * <ul>
 *   <li>{@link io.thatworked.support.report.infrastructure.adapter.FileStorageAdapter} - 
 *       Implements FileStoragePort for report file persistence using local file
 *       system or cloud storage</li>
 *   <li>{@link io.thatworked.support.report.infrastructure.adapter.CompositeReportGeneratorAdapter} - 
 *       Implements ReportGeneratorPort by delegating to format-specific generators
 *       based on the requested output format</li>
 * </ul>
 * 
 * <p>Report generation services:
 * <ul>
 *   <li>{@link io.thatworked.support.report.infrastructure.adapter.DomainPdfReportGenerator} - 
 *       Generates PDF reports directly from domain models using iTextPDF</li>
 *   <li>{@link io.thatworked.support.report.infrastructure.adapter.DomainCsvReportGenerator} - 
 *       Generates CSV reports directly from domain models</li>
 * </ul>
 * 
 * <p>Storage services:
 * <ul>
 *   <li>{@link io.thatworked.support.report.infrastructure.adapter.FileStorageService} - 
 *       Low-level file operations with support for local and cloud storage</li>
 * </ul>
 * 
 * <p>Adapter responsibilities:
 * <ul>
 *   <li>Implement port interfaces with technical solutions</li>
 *   <li>Handle framework-specific concerns (transactions, retries)</li>
 *   <li>Map between domain models and technical representations</li>
 *   <li>Manage external library dependencies</li>
 *   <li>Provide error handling and logging</li>
 * </ul>
 * 
 * <p>Design patterns:
 * <ul>
 *   <li>Adapter pattern for port implementation</li>
 *   <li>Strategy pattern for format-specific report generation</li>
 *   <li>Template method for common report generation steps</li>
 *   <li>Decorator pattern for adding features (compression, encryption)</li>
 * </ul>
 */
package io.thatworked.support.report.infrastructure.adapter;