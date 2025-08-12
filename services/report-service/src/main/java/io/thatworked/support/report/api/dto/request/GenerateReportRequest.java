package io.thatworked.support.report.api.dto.request;

import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.util.List;

/**
 * Request DTO for generating a new report.
 */
public class GenerateReportRequest {
    
    @NotNull(message = "Report type is required")
    private ReportType reportType;
    
    @NotNull(message = "Report format is required")
    private ReportFormat format;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant startDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant endDate;
    
    private List<String> deviceIds;
    
    private String title;
    
    // Constructors
    public GenerateReportRequest() {}
    
    public GenerateReportRequest(ReportType reportType, ReportFormat format) {
        this.reportType = reportType;
        this.format = format;
    }
    
    // Getters and Setters
    public ReportType getReportType() { return reportType; }
    public void setReportType(ReportType reportType) { this.reportType = reportType; }
    
    public ReportFormat getFormat() { return format; }
    public void setFormat(ReportFormat format) { this.format = format; }
    
    public Instant getStartDate() { return startDate; }
    public void setStartDate(Instant startDate) { this.startDate = startDate; }
    
    public Instant getEndDate() { return endDate; }
    public void setEndDate(Instant endDate) { this.endDate = endDate; }
    
    public List<String> getDeviceIds() { return deviceIds; }
    public void setDeviceIds(List<String> deviceIds) { this.deviceIds = deviceIds; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    /**
     * Report type enumeration for API layer.
     */
    public enum ReportType {
        UPTIME_SUMMARY,
        DEVICE_STATUS,
        PING_PERFORMANCE,
        ALERT_HISTORY
    }
    
    /**
     * Report format enumeration for API layer.
     */
    public enum ReportFormat {
        PDF,
        CSV
    }
}