package io.thatworked.support.report.api.mapper;

import io.thatworked.support.report.api.dto.request.GenerateReportRequest;
import io.thatworked.support.report.api.dto.response.*;
import io.thatworked.support.report.application.usecase.*;
import io.thatworked.support.report.domain.model.ReportFormat;
import io.thatworked.support.report.domain.model.ReportType;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between API DTOs and domain/application objects.
 */
@Component
public class ReportDtoMapper {
    
    /**
     * Maps generate report request DTO to use case request.
     */
    public GenerateReportUseCase.ReportRequest toUseCaseRequest(GenerateReportRequest dto) {
        return new GenerateReportUseCase.ReportRequest(
            dto.getReportType().name(),
            dto.getFormat().name(),
            dto.getStartDate() != null ? dto.getStartDate().atZone(ZoneId.systemDefault()).toInstant() : null,
            dto.getEndDate() != null ? dto.getEndDate().atZone(ZoneId.systemDefault()).toInstant() : null,
            dto.getDeviceIds(),
            dto.getTitle()
        );
    }
    
    /**
     * Maps use case response to API response DTO.
     */
    public ReportResponse toReportResponse(GenerateReportUseCase.ReportResponse ucResponse) {
        return new ReportResponse(
            ucResponse.id(),
            ucResponse.filename(),
            ucResponse.reportType(),
            ucResponse.format(),
            ucResponse.generatedAt(),
            ucResponse.fileSizeBytes(),
            ucResponse.downloadUrl()
        );
    }
    
    /**
     * Maps get report by ID response to API response DTO.
     */
    public ReportResponse toReportResponse(GetReportByIdUseCase.ReportResponse ucResponse) {
        return new ReportResponse(
            ucResponse.id(),
            ucResponse.filename(),
            ucResponse.reportType(),
            ucResponse.format(),
            ucResponse.generatedAt(),
            ucResponse.fileSizeBytes(),
            ucResponse.downloadUrl()
        );
    }
    
    /**
     * Maps list reports use case response to API response DTO.
     */
    public ReportListResponse toReportListResponse(ListReportsUseCase.ListReportsResponse ucResponse, 
                                                  int pageSize, int pageNumber) {
        List<ReportSummary> summaries = ucResponse.reports().stream()
            .map(summary -> new ReportSummary(
                summary.id(),
                summary.filename(),
                summary.reportType(),
                summary.format(),
                summary.generatedAt(),
                summary.fileSizeBytes(),
                summary.title()
            ))
            .collect(Collectors.toList());
        
        return new ReportListResponse(
            summaries,
            summaries.size(), // Total count would need to be implemented properly
            pageSize,
            pageNumber
        );
    }
    
    /**
     * Maps report status use case response to API response DTO.
     */
    public ReportStatusResponse toReportStatusResponse(GetReportStatusUseCase.ReportStatusResponse ucResponse) {
        return new ReportStatusResponse(
            ucResponse.id(),
            ucResponse.status(),
            ucResponse.generatedAt(),
            ucResponse.fileSizeBytes(),
            ucResponse.processingTimeMs(),
            ucResponse.error()
        );
    }
    
    /**
     * Maps scheduled report use case response to API response DTO.
     */
    public ScheduledReportResponse toScheduledReportResponse(ScheduleReportUseCase.ScheduledReportResponse ucResponse) {
        return new ScheduledReportResponse(
            ucResponse.scheduledReportId(),
            ucResponse.reportType(),
            ucResponse.format(),
            ucResponse.schedule(),
            ucResponse.nextRunTime(),
            null, // lastRunTime would need to be added to use case response
            ucResponse.active(),
            null  // title would need to be added to use case response
        );
    }
    
    /**
     * Maps API report type to domain report type.
     */
    public ReportType toDomainReportType(GenerateReportRequest.ReportType apiType) {
        return switch (apiType) {
            case UPTIME_SUMMARY -> ReportType.UPTIME_SUMMARY;
            case DEVICE_STATUS -> ReportType.DEVICE_STATUS;
            case PING_PERFORMANCE -> ReportType.PING_PERFORMANCE;
            case ALERT_HISTORY -> ReportType.ALERT_HISTORY;
        };
    }
    
    /**
     * Maps API report format to domain report format.
     */
    public ReportFormat toDomainReportFormat(GenerateReportRequest.ReportFormat apiFormat) {
        return switch (apiFormat) {
            case PDF -> ReportFormat.PDF;
            case CSV -> ReportFormat.CSV;
        };
    }
}