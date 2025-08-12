package io.thatworked.support.report.application.service;

import io.thatworked.support.report.application.usecase.*;
import io.thatworked.support.report.application.port.ReportApplicationPort;
import io.thatworked.support.report.domain.model.*;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

/**
 * Application service coordinating report operations and use cases.
 */
public class ReportApplicationService implements ReportApplicationPort {
    
    private final GenerateReportUseCase generateReportUseCase;
    private final GetReportByIdUseCase getReportByIdUseCase;
    private final ListReportsUseCase listReportsUseCase;
    private final DeleteReportUseCase deleteReportUseCase;
    private final DownloadReportUseCase downloadReportUseCase;
    
    public ReportApplicationService(GenerateReportUseCase generateReportUseCase,
                                   GetReportByIdUseCase getReportByIdUseCase,
                                   ListReportsUseCase listReportsUseCase,
                                   DeleteReportUseCase deleteReportUseCase,
                                   DownloadReportUseCase downloadReportUseCase) {
        this.generateReportUseCase = generateReportUseCase;
        this.getReportByIdUseCase = getReportByIdUseCase;
        this.listReportsUseCase = listReportsUseCase;
        this.deleteReportUseCase = deleteReportUseCase;
        this.downloadReportUseCase = downloadReportUseCase;
    }
    
    @Override
    public ReportResponse generateReport(ReportRequest request) {
        // Get dates from request
        Instant startDate = request.getStartDate();
        Instant endDate = request.getEndDate();
        
        // Set default time range if not provided
        if (startDate == null || endDate == null) {
            ReportTimeRange defaultRange = getDefaultTimeRange(request.getReportType());
            if (defaultRange != null) {
                startDate = defaultRange.getStartDate();
                endDate = defaultRange.getEndDate();
            }
        }
        
        // Create use case request
        GenerateReportUseCase.ReportRequest ucRequest = new GenerateReportUseCase.ReportRequest(
            request.getReportType().name(),
            request.getFormat().name(),
            startDate,
            endDate,
            request.getDeviceIds(),
            request.getTitle()
        );
        
        // Execute use case
        GenerateReportUseCase.ReportResponse ucResponse = generateReportUseCase.execute(ucRequest);
        
        // Convert response to application DTO
        return new ReportResponse(
            ucResponse.id(),
            ucResponse.filename(),
            request.getReportType(),
            request.getFormat(),
            ucResponse.generatedAt(),
            ucResponse.fileSizeBytes(),
            ucResponse.downloadUrl()
        );
    }
    
    private ReportTimeRange getDefaultTimeRange(ReportType reportType) {
        return switch (reportType) {
            case UPTIME_SUMMARY, PING_PERFORMANCE -> ReportTimeRange.lastDays(1);
            case ALERT_HISTORY -> ReportTimeRange.lastDays(7);
            case DEVICE_STATUS -> null; // No time range needed
        };
    }
    
    /**
     * Application-layer request object.
     */
    public static class ReportRequest {
        private ReportType reportType;
        private ReportFormat format;
        private java.time.Instant startDate;
        private java.time.Instant endDate;
        private List<String> deviceIds;
        private String title;
        
        // Constructors
        public ReportRequest() {}
        
        public ReportRequest(ReportType reportType, ReportFormat format) {
            this.reportType = reportType;
            this.format = format;
        }
        
        // Getters and setters
        public ReportType getReportType() { return reportType; }
        public void setReportType(ReportType reportType) { this.reportType = reportType; }
        
        public ReportFormat getFormat() { return format; }
        public void setFormat(ReportFormat format) { this.format = format; }
        
        public java.time.Instant getStartDate() { return startDate; }
        public void setStartDate(java.time.Instant startDate) { this.startDate = startDate; }
        
        public java.time.Instant getEndDate() { return endDate; }
        public void setEndDate(java.time.Instant endDate) { this.endDate = endDate; }
        
        public List<String> getDeviceIds() { return deviceIds; }
        public void setDeviceIds(List<String> deviceIds) { this.deviceIds = deviceIds; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
    }
    
    /**
     * Application-layer response object.
     */
    public static class ReportResponse {
        private final String reportId;
        private final String filename;
        private final ReportType reportType;
        private final ReportFormat format;
        private final java.time.Instant generatedAt;
        private final long fileSizeBytes;
        private final String downloadUrl;
        
        public ReportResponse(String reportId, String filename, ReportType reportType,
                             ReportFormat format, java.time.Instant generatedAt,
                             long fileSizeBytes, String downloadUrl) {
            this.reportId = reportId;
            this.filename = filename;
            this.reportType = reportType;
            this.format = format;
            this.generatedAt = generatedAt;
            this.fileSizeBytes = fileSizeBytes;
            this.downloadUrl = downloadUrl;
        }
        
        // Getters
        public String getReportId() { return reportId; }
        public String getFilename() { return filename; }
        public ReportType getReportType() { return reportType; }
        public ReportFormat getFormat() { return format; }
        public java.time.Instant getGeneratedAt() { return generatedAt; }
        public long getFileSizeBytes() { return fileSizeBytes; }
        public String getDownloadUrl() { return downloadUrl; }
    }
}