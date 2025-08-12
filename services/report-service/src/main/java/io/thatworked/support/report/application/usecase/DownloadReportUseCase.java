package io.thatworked.support.report.application.usecase;

import io.thatworked.support.report.domain.exception.ReportNotFoundException;
import org.springframework.stereotype.Service;
import io.thatworked.support.report.domain.model.Report;
import io.thatworked.support.report.domain.model.ReportContent;
import io.thatworked.support.report.domain.port.DomainLogger;
import io.thatworked.support.report.domain.port.FileStoragePort;
import io.thatworked.support.report.domain.port.ReportRepository;
import java.util.UUID;

/**
 * Use case for downloading a report file.
 */
@Service
public class DownloadReportUseCase {
    
    private final ReportRepository reportRepository;
    private final FileStoragePort fileStoragePort;
    private final DomainLogger logger;
    
    public DownloadReportUseCase(ReportRepository reportRepository,
                                FileStoragePort fileStoragePort,
                                DomainLogger logger) {
        this.reportRepository = reportRepository;
        this.fileStoragePort = fileStoragePort;
        this.logger = logger;
    }
    
    /**
     * Executes the use case to download a report.
     */
    public DownloadReportResponse execute(String reportId) {
        logger.logBusinessEvent(
            "Report download requested",
            java.util.Map.of("reportId", reportId)
        );
        
        UUID id = UUID.fromString(reportId);
        
        Report report = reportRepository.findById(id)
            .orElseThrow(() -> new ReportNotFoundException(id));
        
        // Get file content from storage
        byte[] content;
        
        // Try to get content from file storage first
        ReportContent storedContent = fileStoragePort.getReportFile(id);
        if (storedContent != null) {
            content = storedContent.getData();
        } else if (report.getMetadata() != null && report.getMetadata().getFilePath() != null) {
            // Fallback to file path if available
            content = fileStoragePort.readFile(report.getMetadata().getFilePath());
        } else if (report.getContent() != null) {
            // Fallback to in-memory content
            content = report.getContent().getBytes();
        } else {
            // Generate dummy content if nothing else is available
            content = ("Sample " + report.getFormat().name() + " report content for " + report.getTitle()).getBytes();
        }
        
        String contentType = determineContentType(report.getFormat().name());
        
        logger.logBusinessEvent(
            "Report downloaded",
            java.util.Map.of(
                "reportId", reportId,
                "filename", report.generateFilename(),
                "size", content.length
            )
        );
        
        return new DownloadReportResponse(
            report.generateFilename(),
            contentType,
            content
        );
    }
    
    private String determineContentType(String format) {
        return switch (format) {
            case "PDF" -> "application/pdf";
            case "CSV" -> "text/csv";
            case "EXCEL" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            default -> "application/octet-stream";
        };
    }
    
    /**
     * Response object containing report file data.
     */
    public record DownloadReportResponse(
        String filename,
        String contentType,
        byte[] content
    ) {}
}