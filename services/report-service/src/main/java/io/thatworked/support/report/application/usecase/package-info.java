/**
 * Use case implementations representing discrete system operations.
 * 
 * <p>Each use case encapsulates a single business operation that can be performed
 * by the system. Use cases orchestrate domain services, handle transactions, and
 * coordinate with infrastructure through ports. They represent the application's
 * API in terms of business operations rather than technical endpoints.
 * 
 * <p>Report generation use cases:
 * <ul>
 *   <li>{@link io.thatworked.support.report.application.usecase.GenerateReportUseCase} - 
 *       Orchestrates report generation by collecting data from multiple services,
 *       invoking domain calculations, and producing formatted output</li>
 *   <li>{@link io.thatworked.support.report.application.usecase.RegenerateReportUseCase} - 
 *       Re-executes report generation with updated data while preserving report ID</li>
 *   <li>{@link io.thatworked.support.report.application.usecase.ScheduleReportUseCase} - 
 *       Configures recurring report generation with cron expressions and recipients</li>
 * </ul>
 * 
 * <p>Report management use cases:
 * <ul>
 *   <li>{@link io.thatworked.support.report.application.usecase.GetReportByIdUseCase} - 
 *       Retrieves report metadata and generation details</li>
 *   <li>{@link io.thatworked.support.report.application.usecase.ListReportsUseCase} - 
 *       Provides paginated report listings with filtering by type, date, and status</li>
 *   <li>{@link io.thatworked.support.report.application.usecase.GetReportStatusUseCase} - 
 *       Monitors ongoing report generation progress</li>
 *   <li>{@link io.thatworked.support.report.application.usecase.DownloadReportUseCase} - 
 *       Retrieves report file content for client download</li>
 *   <li>{@link io.thatworked.support.report.application.usecase.DeleteReportUseCase} - 
 *       Removes report metadata and associated files</li>
 *   <li>{@link io.thatworked.support.report.application.usecase.CleanupOldReportsUseCase} - 
 *       Batch deletion of reports exceeding retention period</li>
 * </ul>
 * 
 * <p>Design principles:
 * <ul>
 *   <li>Single Responsibility - one business operation per use case</li>
 *   <li>Command pattern - request objects encapsulate parameters</li>
 *   <li>Explicit dependencies - constructor injection only</li>
 *   <li>Transaction per use case - atomic business operations</li>
 *   <li>Domain model protection - never expose entities directly</li>
 * </ul>
 * 
 * <p>Error handling:
 * <ul>
 *   <li>Validation errors return descriptive messages</li>
 *   <li>Business rule violations throw domain exceptions</li>
 *   <li>Infrastructure failures wrapped in application exceptions</li>
 *   <li>Compensating transactions for multi-step operations</li>
 * </ul>
 */
package io.thatworked.support.report.application.usecase;