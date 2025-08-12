package io.thatworked.support.report.application.usecase;

import io.thatworked.support.report.domain.model.*;
import org.springframework.stereotype.Service;
import io.thatworked.support.report.domain.port.DomainLogger;
import io.thatworked.support.report.domain.service.ReportDomainService;
import io.thatworked.support.report.domain.port.ReportRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Use case for generating reports with specified parameters.
 */
@Service
public class GenerateReportUseCase {
    
    private final ReportDomainService reportDomainService;
    private final ReportRepository reportRepository;
    private final DomainLogger logger;
    
    public GenerateReportUseCase(ReportDomainService reportDomainService,
                                ReportRepository reportRepository,
                                DomainLogger logger) {
        this.reportDomainService = reportDomainService;
        this.reportRepository = reportRepository;
        this.logger = logger;
    }
    
    /**
     * Executes the report generation use case.
     */
    public ReportResponse execute(ReportRequest request) {
        logger.logBusinessEvent(
            "Report generation requested",
            java.util.Map.of(
                "reportType", request.reportType(),
                "format", request.format(),
                "hasDateRange", request.startDate() != null && request.endDate() != null,
                "deviceCount", request.deviceIds() != null ? request.deviceIds().size() : 0
            )
        );
        
        try {
            // Convert request to domain objects
            ReportType reportType = ReportType.valueOf(request.reportType());
            ReportFormat format = ReportFormat.valueOf(request.format());
            ReportTimeRange timeRange = ReportTimeRange.of(request.startDate(), request.endDate());
            List<UUID> deviceIds = request.deviceIds() != null 
                ? request.deviceIds().stream().map(UUID::fromString).collect(Collectors.toList())
                : List.of();
            
            // Generate report through domain service
            Report report = reportDomainService.generateReport(
                reportType,
                format,
                timeRange,
                deviceIds,
                request.title()
            );
            
            // Save to repository
            report = reportRepository.save(report);
            
            logger.logDomainStateChange(
                "Report",
                report.getId().toString(),
                "created",
                "saved",
                java.util.Map.of("filename", report.generateFilename())
            );
            
            return new ReportResponse(
                report.getId().toString(),
                report.generateFilename(),
                report.getReportType().name(),
                report.getFormat().name(),
                report.getGeneratedAt(),
                report.getMetadata().getFileSizeBytes(),
                report.getMetadata().getDownloadUrl()
            );
            
        } catch (Exception e) {
            logger.logBusinessWarning(
                "Failed to generate report",
                java.util.Map.of(
                    "reportType", request.reportType(),
                    "error", e.getMessage()
                )
            );
            throw new RuntimeException("Failed to generate report", e);
        }
    }
    
    /**
     * Request object for report generation.
     */
    public record ReportRequest(
        String reportType,
        String format,
        Instant startDate,
        Instant endDate,
        List<String> deviceIds,
        String title
    ) {}
    
    /**
     * Response object for report generation result.
     */
    public record ReportResponse(
        String id,
        String filename,
        String reportType,
        String format,
        Instant generatedAt,
        long fileSizeBytes,
        String downloadUrl
    ) {}
}