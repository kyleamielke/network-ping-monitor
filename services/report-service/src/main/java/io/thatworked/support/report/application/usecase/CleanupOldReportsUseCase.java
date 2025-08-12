package io.thatworked.support.report.application.usecase;

import io.thatworked.support.report.domain.model.Report;
import org.springframework.stereotype.Service;
import io.thatworked.support.report.domain.port.DomainLogger;
import io.thatworked.support.report.domain.port.FileStoragePort;
import io.thatworked.support.report.domain.port.ReportRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Use case for cleaning up old reports and their associated files.
 */
@Service
public class CleanupOldReportsUseCase {
    
    private final ReportRepository reportRepository;
    private final FileStoragePort fileStoragePort;
    private final DomainLogger logger;
    private final int retentionDays;
    
    public CleanupOldReportsUseCase(ReportRepository reportRepository,
                                   FileStoragePort fileStoragePort,
                                   DomainLogger logger,
                                   int retentionDays) {
        this.reportRepository = reportRepository;
        this.fileStoragePort = fileStoragePort;
        this.logger = logger;
        this.retentionDays = retentionDays;
    }
    
    /**
     * Executes the use case to clean up old reports.
     */
    public CleanupResult execute() {
        logger.logBusinessEvent(
            "Report cleanup started",
            java.util.Map.of(
                "retentionDays", retentionDays,
                "cutoffDate", Instant.now().minus(retentionDays, ChronoUnit.DAYS).toString()
            )
        );
        
        Instant cutoffDate = Instant.now().minus(retentionDays, ChronoUnit.DAYS);
        
        // Find old reports
        List<Report> oldReports = reportRepository.findReportsOlderThan(cutoffDate);
        
        int deletedCount = 0;
        int failedCount = 0;
        
        for (Report report : oldReports) {
            try {
                // Delete file if exists
                String filePath = report.getMetadata().getFilePath();
                if (filePath != null) {
                    fileStoragePort.deleteFile(filePath);
                }
                
                // Delete report record
                reportRepository.delete(report.getId());
                deletedCount++;
                
                logger.logDomainStateChange(
                    "Report",
                    report.getId().toString(),
                    "expired",
                    "deleted",
                    java.util.Map.of("generatedAt", report.getGeneratedAt())
                );
                      
            } catch (Exception e) {
                failedCount++;
                logger.logBusinessWarning(
                    "Failed to delete old report",
                    java.util.Map.of(
                        "reportId", report.getId().toString(),
                        "error", e.getMessage()
                    )
                );
            }
        }
        
        logger.logBusinessEvent(
            "Report cleanup completed",
            java.util.Map.of(
                "totalReports", oldReports.size(),
                "deletedCount", deletedCount,
                "failedCount", failedCount
            )
        );
        
        return new CleanupResult(
            oldReports.size(),
            deletedCount,
            failedCount,
            cutoffDate
        );
    }
    
    /**
     * Result of the cleanup operation.
     */
    public record CleanupResult(
        int totalReports,
        int deletedCount,
        int failedCount,
        Instant cutoffDate
    ) {}
}