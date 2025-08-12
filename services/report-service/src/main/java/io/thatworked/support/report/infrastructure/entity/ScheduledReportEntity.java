package io.thatworked.support.report.infrastructure.entity;

import io.thatworked.support.report.domain.model.ScheduledReportStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.util.UUID;

/**
 * JPA entity for scheduled report persistence.
 */
@Entity
@Table(name = "scheduled_reports", indexes = {
    @Index(name = "idx_scheduled_reports_active", columnList = "is_active"),
    @Index(name = "idx_scheduled_reports_next_run", columnList = "next_run_time"),
    @Index(name = "idx_scheduled_reports_type", columnList = "report_type")
})
public class ScheduledReportEntity {
    
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "report_type", nullable = false)
    private ReportEntity.ReportType reportType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "format", nullable = false)
    private ReportEntity.ReportFormat format;
    
    @Column(name = "schedule", nullable = false)
    private String schedule; // Cron expression
    
    @Column(name = "device_ids", columnDefinition = "TEXT")
    private String deviceIds; // JSON array of device IDs
    
    @Column(name = "is_active", nullable = false)
    private boolean isActive;
    
    @Column(name = "last_run_time")
    private Instant lastRunTime;
    
    @Column(name = "next_run_time")
    private Instant nextRunTime;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "last_run_status")
    private ScheduledReportStatus lastRunStatus;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
    
    // Constructors
    public ScheduledReportEntity() {}
    
    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public ReportEntity.ReportType getReportType() { return reportType; }
    public void setReportType(ReportEntity.ReportType reportType) { this.reportType = reportType; }
    
    public ReportEntity.ReportFormat getFormat() { return format; }
    public void setFormat(ReportEntity.ReportFormat format) { this.format = format; }
    
    public String getSchedule() { return schedule; }
    public void setSchedule(String schedule) { this.schedule = schedule; }
    
    public String getDeviceIds() { return deviceIds; }
    public void setDeviceIds(String deviceIds) { this.deviceIds = deviceIds; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean isActive) { this.isActive = isActive; }
    
    public Instant getLastRunTime() { return lastRunTime; }
    public void setLastRunTime(Instant lastRunTime) { this.lastRunTime = lastRunTime; }
    
    public Instant getNextRunTime() { return nextRunTime; }
    public void setNextRunTime(Instant nextRunTime) { this.nextRunTime = nextRunTime; }
    
    public ScheduledReportStatus getLastRunStatus() { return lastRunStatus; }
    public void setLastRunStatus(ScheduledReportStatus lastRunStatus) { this.lastRunStatus = lastRunStatus; }
    
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}