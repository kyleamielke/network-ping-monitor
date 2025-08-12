package io.thatworked.support.report.infrastructure.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.util.UUID;

/**
 * JPA entity for report persistence.
 */
@Entity
@Table(name = "reports", indexes = {
    @Index(name = "idx_reports_type", columnList = "report_type"),
    @Index(name = "idx_reports_generated_at", columnList = "generated_at"),
    @Index(name = "idx_reports_status", columnList = "status")
})
public class ReportEntity {
    
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;
    
    @Column(name = "filename", nullable = false)
    private String filename;
    
    @Column(name = "title")
    private String title;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "report_type", nullable = false)
    private ReportType reportType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "format", nullable = false)
    private ReportFormat format;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReportStatus status;
    
    @Column(name = "file_path")
    private String filePath;
    
    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;
    
    @Column(name = "download_url")
    private String downloadUrl;
    
    @Column(name = "time_range_start")
    private Instant timeRangeStart;
    
    @Column(name = "time_range_end")
    private Instant timeRangeEnd;
    
    @Column(name = "parameters", columnDefinition = "TEXT")
    private String parameters;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @Column(name = "generated_at", nullable = false)
    private Instant generatedAt;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
    
    // Constructors
    public ReportEntity() {}
    
    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public ReportType getReportType() { return reportType; }
    public void setReportType(ReportType reportType) { this.reportType = reportType; }
    
    public ReportFormat getFormat() { return format; }
    public void setFormat(ReportFormat format) { this.format = format; }
    
    public ReportStatus getStatus() { return status; }
    public void setStatus(ReportStatus status) { this.status = status; }
    
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    
    public Long getFileSizeBytes() { return fileSizeBytes; }
    public void setFileSizeBytes(Long fileSizeBytes) { this.fileSizeBytes = fileSizeBytes; }
    
    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
    
    public Instant getTimeRangeStart() { return timeRangeStart; }
    public void setTimeRangeStart(Instant timeRangeStart) { this.timeRangeStart = timeRangeStart; }
    
    public Instant getTimeRangeEnd() { return timeRangeEnd; }
    public void setTimeRangeEnd(Instant timeRangeEnd) { this.timeRangeEnd = timeRangeEnd; }
    
    public String getParameters() { return parameters; }
    public void setParameters(String parameters) { this.parameters = parameters; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public Instant getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(Instant generatedAt) { this.generatedAt = generatedAt; }
    
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    
    // Enums
    public enum ReportType {
        DEVICE_STATUS,
        UPTIME_SUMMARY,
        PING_PERFORMANCE,
        ALERT_HISTORY
    }
    
    public enum ReportFormat {
        PDF,
        CSV
    }
    
    public enum ReportStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        FAILED
    }
}