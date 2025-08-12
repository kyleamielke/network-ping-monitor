package io.thatworked.support.report.api.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;

/**
 * Request DTO for scheduling a recurring report.
 */
public class ScheduleReportRequest {
    
    @NotNull(message = "Report type is required")
    private GenerateReportRequest.ReportType reportType;
    
    @NotNull(message = "Report format is required")
    private GenerateReportRequest.ReportFormat format;
    
    @NotNull(message = "Schedule is required")
    @Pattern(regexp = "^[0-9\\s\\*\\/\\-\\,]+$", message = "Invalid cron expression")
    private String schedule; // Cron expression
    
    @NotNull(message = "Title is required")
    private String title;
    
    private List<String> deviceIds;
    
    private boolean active = true;
    
    // Getters and Setters
    public GenerateReportRequest.ReportType getReportType() { return reportType; }
    public void setReportType(GenerateReportRequest.ReportType reportType) { this.reportType = reportType; }
    
    public GenerateReportRequest.ReportFormat getFormat() { return format; }
    public void setFormat(GenerateReportRequest.ReportFormat format) { this.format = format; }
    
    public String getSchedule() { return schedule; }
    public void setSchedule(String schedule) { this.schedule = schedule; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public List<String> getDeviceIds() { return deviceIds; }
    public void setDeviceIds(List<String> deviceIds) { this.deviceIds = deviceIds; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}