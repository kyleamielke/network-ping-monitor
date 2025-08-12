package io.thatworked.support.report.application.usecase;

import io.thatworked.support.report.domain.exception.ReportNotFoundException;
import org.springframework.stereotype.Service;
import io.thatworked.support.report.domain.model.Report;
import io.thatworked.support.report.domain.port.DomainLogger;
import io.thatworked.support.report.domain.port.ReportGeneratorPort;
import io.thatworked.support.report.domain.port.ReportRepository;
import java.util.UUID;

/**
 * Use case for regenerating an existing report with the same parameters.
 */
@Service
public class RegenerateReportUseCase {
    
    private final ReportRepository reportRepository;
    private final ReportGeneratorPort reportGeneratorPort;
    private final GenerateReportUseCase generateReportUseCase;
    private final DomainLogger logger;
    
    public RegenerateReportUseCase(ReportRepository reportRepository,
                                  ReportGeneratorPort reportGeneratorPort,
                                  GenerateReportUseCase generateReportUseCase,
                                  DomainLogger logger) {
        this.reportRepository = reportRepository;
        this.reportGeneratorPort = reportGeneratorPort;
        this.generateReportUseCase = generateReportUseCase;
        this.logger = logger;
    }
    
    /**
     * Executes the use case to regenerate a report.
     */
    public GenerateReportUseCase.ReportResponse execute(String reportId) {
        logger.logBusinessEvent(
            "Report regeneration requested",
            java.util.Map.of("reportId", reportId)
        );
        
        UUID id = UUID.fromString(reportId);
        
        // Get original report
        Report originalReport = reportRepository.findById(id)
            .orElseThrow(() -> new ReportNotFoundException(id));
        
        // Extract parameters from original report
        GenerateReportUseCase.ReportRequest request = new GenerateReportUseCase.ReportRequest(
            originalReport.getReportType().name(),
            originalReport.getFormat().name(),
            originalReport.getParameters().startDate(),
            originalReport.getParameters().endDate(),
            originalReport.getParameters().getDeviceIdStrings(),
            originalReport.getTitle() + " (Regenerated)"
        );
        
        logger.logBusinessDecision(
            "Regenerating report with original parameters",
            java.util.Map.of(
                "originalReportId", reportId,
                "reportType", request.reportType(),
                "format", request.format()
            ),
            "New report will be generated"
        );
        
        // Generate new report with same parameters
        return generateReportUseCase.execute(request);
    }
}