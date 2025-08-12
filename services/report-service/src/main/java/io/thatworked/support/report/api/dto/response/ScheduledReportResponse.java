package io.thatworked.support.report.api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;

/**
 * Response DTO for scheduled report information.
 */
public class ScheduledReportResponse {
    
    private String id;
    private String reportType;
    private String format;
    private String schedule;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant nextRunTime;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant lastRunTime;
    
    private boolean active;
    private String title;
    
    // Constructors
    public ScheduledReportResponse() {}
    
    public ScheduledReportResponse(String id, String reportType, String format,
                                  String schedule, Instant nextRunTime, Instant lastRunTime,
                                  boolean active, String title) {
        this.id = id;
        this.reportType = reportType;
        this.format = format;
        this.schedule = schedule;
        this.nextRunTime = nextRunTime;
        this.lastRunTime = lastRunTime;
        this.active = active;
        this.title = title;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }
    
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
    
    public String getSchedule() { return schedule; }
    public void setSchedule(String schedule) { this.schedule = schedule; }
    
    public Instant getNextRunTime() { return nextRunTime; }
    public void setNextRunTime(Instant nextRunTime) { this.nextRunTime = nextRunTime; }
    
    public Instant getLastRunTime() { return lastRunTime; }
    public void setLastRunTime(Instant lastRunTime) { this.lastRunTime = lastRunTime; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}