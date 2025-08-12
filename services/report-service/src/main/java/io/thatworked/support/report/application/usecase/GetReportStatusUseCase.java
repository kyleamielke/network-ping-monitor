package io.thatworked.support.report.application.usecase;

import io.thatworked.support.report.domain.exception.ReportNotFoundException;
import org.springframework.stereotype.Service;
import io.thatworked.support.report.domain.model.Report;
import io.thatworked.support.report.domain.port.DomainLogger;
import io.thatworked.support.report.domain.port.ReportRepository;
import java.util.UUID;

/**
 * Use case for getting the status of a report generation.
 */
@Service
public class GetReportStatusUseCase {
    
    private final ReportRepository reportRepository;
    private final DomainLogger logger;
    
    public GetReportStatusUseCase(ReportRepository reportRepository, DomainLogger logger) {
        this.reportRepository = reportRepository;
        this.logger = logger;
    }
    
    /**
     * Executes the use case to get report status.
     */
    public ReportStatusResponse execute(String reportId) {
        // Query operation - no business event to log
        
        UUID id = UUID.fromString(reportId);
        
        Report report = reportRepository.findById(id)
            .orElseThrow(() -> new ReportNotFoundException(id));
        
        // Determine status based on report state
        String status = determineStatus(report);
        
        return new ReportStatusResponse(
            report.getId().toString(),
            status,
            report.getGeneratedAt(),
            report.getMetadata().getFileSizeBytes(),
            report.getMetadata().getProcessingTimeMs(),
            report.getMetadata().getError()
        );
    }
    
    private String determineStatus(Report report) {
        if (report.getMetadata().getError() != null) {
            return "FAILED";
        } else if (report.getContent() != null && !report.getContent().isEmpty()) {
            return "COMPLETED";
        } else {
            return "IN_PROGRESS";
        }
    }
    
    /**
     * Response object containing report status information.
     */
    public record ReportStatusResponse(
        String id,
        String status,
        java.time.Instant generatedAt,
        long fileSizeBytes,
        Long processingTimeMs,
        String error
    ) {}
}