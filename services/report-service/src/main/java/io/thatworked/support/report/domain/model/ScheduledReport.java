package io.thatworked.support.report.domain.model;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Domain model representing a scheduled recurring report.
 */
public class ScheduledReport {
    
    private final UUID id;
    private final ReportType reportType;
    private final ReportFormat format;
    private final String schedule; // Cron expression
    private final String title;
    private final List<String> deviceIds;
    private final boolean isActive;
    private final Instant createdAt;
    private final Instant lastRunTime;
    private final Instant nextRunTime;
    
    private ScheduledReport(UUID id, ReportType reportType, ReportFormat format,
                           String schedule, String title, List<String> deviceIds,
                           boolean isActive, Instant createdAt, Instant lastRunTime, Instant nextRunTime) {
        this.id = id;
        this.reportType = reportType;
        this.format = format;
        this.schedule = schedule;
        this.title = title;
        this.deviceIds = deviceIds;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.lastRunTime = lastRunTime;
        this.nextRunTime = nextRunTime;
    }
    
    /**
     * Creates a new scheduled report.
     */
    public static ScheduledReport create(ReportType reportType, ReportFormat format,
                                       String schedule, String title, List<String> deviceIds) {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        Instant nextRun = calculateNextRunTime(schedule, now);
        
        return new ScheduledReport(
            id, reportType, format, schedule, title, deviceIds,
            true, now, null, nextRun
        );
    }
    
    /**
     * Marks the scheduled report as executed and calculates next run time.
     */
    public ScheduledReport markExecuted() {
        Instant now = Instant.now();
        Instant nextRun = calculateNextRunTime(schedule, now);
        
        return new ScheduledReport(
            id, reportType, format, schedule, title, deviceIds,
            isActive, createdAt, now, nextRun
        );
    }
    
    /**
     * Deactivates the scheduled report.
     */
    public ScheduledReport deactivate() {
        return new ScheduledReport(
            id, reportType, format, schedule, title, deviceIds,
            false, createdAt, lastRunTime, null
        );
    }
    
    /**
     * Activates the scheduled report.
     */
    public ScheduledReport activate() {
        Instant nextRun = calculateNextRunTime(schedule, Instant.now());
        
        return new ScheduledReport(
            id, reportType, format, schedule, title, deviceIds,
            true, createdAt, lastRunTime, nextRun
        );
    }
    
    /**
     * Calculates the next run time based on cron expression.
     * This is a simplified implementation - in production, use a proper cron library.
     */
    private static Instant calculateNextRunTime(String cronExpression, Instant from) {
        // Simplified implementation - assumes daily schedule
        // In production, use a proper cron parsing library
        return from.atZone(ZoneId.systemDefault())
            .plusDays(1)
            .withHour(0)
            .withMinute(0)
            .withSecond(0)
            .toInstant();
    }
    
    // Getters
    public UUID getId() { return id; }
    public ReportType getReportType() { return reportType; }
    public ReportFormat getFormat() { return format; }
    public String getSchedule() { return schedule; }
    public String getTitle() { return title; }
    public List<String> getDeviceIds() { return deviceIds; }
    public boolean isActive() { return isActive; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getLastRunTime() { return lastRunTime; }
    public Instant getNextRunTime() { return nextRunTime; }
}