package io.thatworked.support.report.application.usecase;

import java.util.UUID;
import org.springframework.stereotype.Service;
import io.thatworked.support.report.domain.port.ReportRepository;
import io.thatworked.support.report.domain.exception.ReportNotFoundException;
import java.time.Instant;

/**
 * Use case for retrieving a report by its ID.
 */
@Service
public class GetReportByIdUseCase {
    
    private final ReportRepository reportRepository;
    
    public GetReportByIdUseCase(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }
    
    /**
     * Executes the use case to retrieve a report.
     */
    public ReportResponse execute(String reportId) {
        UUID id = UUID.fromString(reportId);
        
        return reportRepository.findById(id)
            .map(report -> new ReportResponse(
                report.getId().toString(),
                report.generateFilename(),
                report.getReportType().name(),
                report.getFormat().name(),
                report.getGeneratedAt(),
                report.getMetadata().getFileSizeBytes(),
                report.getMetadata().getDownloadUrl(),
                report.getTitle()
            ))
            .orElseThrow(() -> new ReportNotFoundException(id));
    }
    
    /**
     * Response object containing report information.
     */
    public record ReportResponse(
        String id,
        String filename,
        String reportType,
        String format,
        java.time.Instant generatedAt,
        long fileSizeBytes,
        String downloadUrl,
        String title
    ) {}
}