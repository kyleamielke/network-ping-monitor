package io.thatworked.support.report.application.usecase;

import io.thatworked.support.report.domain.model.*;
import org.springframework.stereotype.Service;
import io.thatworked.support.report.domain.port.DomainLogger;
import io.thatworked.support.report.domain.port.ScheduledReportRepository;

import java.time.Instant;
import java.util.List;

/**
 * Use case for scheduling recurring report generation.
 */
@Service
public class ScheduleReportUseCase {
    
    private final ScheduledReportRepository scheduledReportRepository;
    private final DomainLogger logger;
    
    public ScheduleReportUseCase(ScheduledReportRepository scheduledReportRepository,
                                DomainLogger logger) {
        this.scheduledReportRepository = scheduledReportRepository;
        this.logger = logger;
    }
    
    /**
     * Executes the use case to schedule a recurring report.
     */
    public ScheduledReportResponse execute(ScheduleReportRequest request) {
        logger.logBusinessEvent(
            "Recurring report schedule requested",
            java.util.Map.of(
                "reportType", request.reportType(),
                "schedule", request.schedule(),
                "format", request.format()
            )
        );
        
        // Create scheduled report
        ScheduledReport scheduledReport = ScheduledReport.create(
            ReportType.valueOf(request.reportType()),
            ReportFormat.valueOf(request.format()),
            request.schedule(),
            request.title(),
            request.deviceIds()
        );
        
        // Save to repository
        scheduledReport = scheduledReportRepository.save(scheduledReport);
        
        logger.logDomainStateChange(
            "ScheduledReport",
            scheduledReport.getId().toString(),
            "new",
            "scheduled",
            java.util.Map.of(
                "nextRunTime", scheduledReport.getNextRunTime(),
                "schedule", scheduledReport.getSchedule()
            )
        );
        
        return new ScheduledReportResponse(
            scheduledReport.getId().toString(),
            scheduledReport.getReportType().name(),
            scheduledReport.getFormat().name(),
            scheduledReport.getSchedule(),
            scheduledReport.getNextRunTime(),
            scheduledReport.isActive()
        );
    }
    
    /**
     * Request object for scheduling a report.
     */
    public record ScheduleReportRequest(
        String reportType,
        String format,
        String schedule, // Cron expression
        String title,
        List<String> deviceIds
    ) {}
    
    /**
     * Response object containing scheduled report information.
     */
    public record ScheduledReportResponse(
        String scheduledReportId,
        String reportType,
        String format,
        String schedule,
        Instant nextRunTime,
        boolean active
    ) {}
}