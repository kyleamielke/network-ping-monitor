package io.thatworked.support.report.application.usecase;

import io.thatworked.support.report.domain.model.Report;
import org.springframework.stereotype.Service;
import io.thatworked.support.report.domain.model.ReportFormat;
import io.thatworked.support.report.domain.model.ReportType;
import io.thatworked.support.report.domain.port.DomainLogger;
import io.thatworked.support.report.domain.port.ReportRepository;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Use case for listing reports with optional filtering.
 */
@Service
public class ListReportsUseCase {
    
    private final ReportRepository reportRepository;
    private final DomainLogger logger;
    
    public ListReportsUseCase(ReportRepository reportRepository, DomainLogger logger) {
        this.reportRepository = reportRepository;
        this.logger = logger;
    }
    
    /**
     * Executes the use case to list reports.
     */
    public ListReportsResponse execute(ListReportsRequest request) {
        logger.logBusinessEvent(
            "Report listing requested",
            java.util.Map.of(
                "reportType", request.reportType() != null ? request.reportType() : "all",
                "format", request.format() != null ? request.format() : "all",
                "limit", request.limit()
            )
        );
        
        List<Report> reports = reportRepository.findReports(
            request.reportType() != null ? ReportType.valueOf(request.reportType()) : null,
            request.format() != null ? ReportFormat.valueOf(request.format()) : null,
            request.startDate(),
            request.endDate(),
            request.limit()
        );
        
        List<ReportSummary> summaries = reports.stream()
            .map(report -> new ReportSummary(
                report.getId().toString(),
                report.generateFilename(),
                report.getReportType().name(),
                report.getFormat().name(),
                report.getGeneratedAt(),
                report.getMetadata().getFileSizeBytes(),
                report.getTitle()
            ))
            .collect(Collectors.toList());
        
        logger.logBusinessEvent(
            "Reports listed",
            java.util.Map.of("reportCount", summaries.size())
        );
        
        return new ListReportsResponse(summaries);
    }
    
    /**
     * Request object for listing reports.
     */
    public record ListReportsRequest(
        String reportType,
        String format,
        Instant startDate,
        Instant endDate,
        int limit
    ) {}
    
    /**
     * Response object containing list of reports.
     */
    public record ListReportsResponse(
        List<ReportSummary> reports
    ) {}
    
    /**
     * Summary information for a report.
     */
    public record ReportSummary(
        String id,
        String filename,
        String reportType,
        String format,
        Instant generatedAt,
        long fileSizeBytes,
        String title
    ) {}
}