package io.thatworked.support.report.application.usecase;

import io.thatworked.support.report.domain.exception.ReportNotFoundException;
import org.springframework.stereotype.Service;
import io.thatworked.support.report.domain.model.Report;
import io.thatworked.support.report.domain.port.DomainLogger;
import io.thatworked.support.report.domain.port.FileStoragePort;
import io.thatworked.support.report.domain.port.ReportRepository;
import java.util.UUID;

/**
 * Use case for deleting a report and its associated file.
 */
@Service
public class DeleteReportUseCase {
    
    private final ReportRepository reportRepository;
    private final FileStoragePort fileStoragePort;
    private final DomainLogger logger;
    
    public DeleteReportUseCase(ReportRepository reportRepository, 
                              FileStoragePort fileStoragePort,
                              DomainLogger logger) {
        this.reportRepository = reportRepository;
        this.fileStoragePort = fileStoragePort;
        this.logger = logger;
    }
    
    /**
     * Executes the use case to delete a report.
     */
    public void execute(String reportId) {
        logger.logBusinessEvent(
            "Report deletion requested",
            java.util.Map.of("reportId", reportId)
        );
        
        UUID id = UUID.fromString(reportId);
        
        // Get report to verify it exists and get file path
        Report report = reportRepository.findById(id)
            .orElseThrow(() -> new ReportNotFoundException(id));
        
        // Delete file from storage
        String filePath = report.getMetadata().getFilePath();
        if (filePath != null) {
            try {
                fileStoragePort.deleteFile(filePath);
                logger.logBusinessEvent(
                    "Report file deleted from storage",
                    java.util.Map.of(
                        "reportId", reportId,
                        "filePath", filePath
                    )
                );
            } catch (Exception e) {
                logger.logBusinessWarning(
                    "Failed to delete report file from storage",
                    java.util.Map.of(
                        "reportId", reportId,
                        "filePath", filePath,
                        "error", e.getMessage()
                    )
                );
                // Continue with report deletion even if file deletion fails
            }
        }
        
        // Delete report from repository
        reportRepository.delete(id);
        
        logger.logDomainStateChange(
            "Report",
            reportId,
            "saved",
            "deleted",
            java.util.Map.of()
        );
    }
}