package io.thatworked.support.report.api.controller;

import io.thatworked.support.report.api.dto.request.ScheduleReportRequest;
import io.thatworked.support.report.api.dto.response.ScheduledReportResponse;
import io.thatworked.support.report.api.mapper.ReportDtoMapper;
import io.thatworked.support.report.application.usecase.ScheduleReportUseCase;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for scheduled report operations.
 */
@RestController
@RequestMapping("/api/v1/scheduled-reports")
public class ScheduledReportController {
    
    private final ScheduleReportUseCase scheduleReportUseCase;
    private final ReportDtoMapper mapper;
    private final StructuredLogger logger;
    
    public ScheduledReportController(ScheduleReportUseCase scheduleReportUseCase,
                                   ReportDtoMapper mapper,
                                   StructuredLoggerFactory loggerFactory) {
        this.scheduleReportUseCase = scheduleReportUseCase;
        this.mapper = mapper;
        this.logger = loggerFactory.getLogger(ScheduledReportController.class);
    }
    
    /**
     * Creates a new scheduled report.
     */
    @PostMapping
    public ResponseEntity<ScheduledReportResponse> scheduleReport(@Valid @RequestBody ScheduleReportRequest request) {
        logger.with("operation", "scheduleReport")
              .with("reportType", request.getReportType())
              .with("schedule", request.getSchedule())
              .info("Received request to schedule report");
        
        ScheduleReportUseCase.ScheduleReportRequest ucRequest = new ScheduleReportUseCase.ScheduleReportRequest(
            request.getReportType().name(),
            request.getFormat().name(),
            request.getSchedule(),
            request.getTitle(),
            request.getDeviceIds() != null ? request.getDeviceIds() : List.of()
        );
        
        ScheduleReportUseCase.ScheduledReportResponse ucResponse = scheduleReportUseCase.execute(ucRequest);
        ScheduledReportResponse response = mapper.toScheduledReportResponse(ucResponse);
        
        logger.with("operation", "scheduleReport")
              .with("scheduledReportId", response.getId())
              .info("Successfully scheduled report");
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}