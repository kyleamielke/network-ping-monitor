package io.thatworked.support.report.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Domain model representing a report entity with business logic and validation.
 * Contains report generation metadata and content references.
 */
public class Report {
    
    private final UUID id;
    private final ReportType reportType;
    private final ReportFormat format;
    private final ReportTimeRange timeRange;
    private final List<UUID> deviceIds;
    private final String title;
    private final Instant generatedAt;
    private final ReportContent content;
    private final ReportMetadata metadata;
    
    public Report(UUID id, 
                  ReportType reportType, 
                  ReportFormat format,
                  ReportTimeRange timeRange,
                  List<UUID> deviceIds,
                  String title,
                  Instant generatedAt,
                  ReportContent content,
                  ReportMetadata metadata) {
        this.id = validateId(id);
        this.reportType = validateReportType(reportType);
        this.format = validateFormat(format);
        this.timeRange = timeRange;
        this.deviceIds = deviceIds;
        this.title = title;
        this.generatedAt = generatedAt != null ? generatedAt : Instant.now();
        this.content = content;
        this.metadata = metadata;
    }
    
    /**
     * Creates a new report with generated ID and current timestamp.
     */
    public static Report create(ReportType reportType,
                               ReportFormat format,
                               ReportTimeRange timeRange,
                               List<UUID> deviceIds,
                               String title) {
        return new Report(
            UUID.randomUUID(),
            reportType,
            format,
            timeRange,
            deviceIds,
            title,
            Instant.now(),
            null, // Content will be set after generation
            ReportMetadata.empty()
        );
    }
    
    /**
     * Sets the report content after generation.
     */
    public Report withContent(ReportContent content) {
        return new Report(
            this.id,
            this.reportType,
            this.format,
            this.timeRange,
            this.deviceIds,
            this.title,
            this.generatedAt,
            content,
            this.metadata.withSize(content.getSize())
        );
    }
    
    /**
     * Sets the report metadata.
     */
    public Report withMetadata(ReportMetadata metadata) {
        return new Report(
            this.id,
            this.reportType,
            this.format,
            this.timeRange,
            this.deviceIds,
            this.title,
            this.generatedAt,
            this.content,
            metadata
        );
    }
    
    /**
     * Generates the filename for this report based on type, format, and timestamp.
     */
    public String generateFilename() {
        String timestamp = java.time.format.DateTimeFormatter
            .ofPattern("yyyyMMdd_HHmmss")
            .withZone(java.time.ZoneId.systemDefault())
            .format(generatedAt);
        
        String typeStr = reportType.name().toLowerCase().replace("_", "-");
        String extension = format.name().toLowerCase();
        
        return String.format("%s-report_%s.%s", typeStr, timestamp, extension);
    }
    
    /**
     * Validates if this report can be generated with the current configuration.
     */
    public boolean canGenerate() {
        return reportType != null && 
               format != null && 
               (timeRange != null || !reportType.requiresTimeRange());
    }
    
    private UUID validateId(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Report ID cannot be null");
        }
        return id;
    }
    
    private ReportType validateReportType(ReportType reportType) {
        if (reportType == null) {
            throw new IllegalArgumentException("Report type cannot be null");
        }
        return reportType;
    }
    
    private ReportFormat validateFormat(ReportFormat format) {
        if (format == null) {
            throw new IllegalArgumentException("Report format cannot be null");
        }
        return format;
    }
    
    // Getters
    public UUID getId() { return id; }
    public ReportType getReportType() { return reportType; }
    public ReportFormat getFormat() { return format; }
    public ReportTimeRange getTimeRange() { return timeRange; }
    public List<UUID> getDeviceIds() { return deviceIds; }
    public String getTitle() { return title; }
    public Instant getGeneratedAt() { return generatedAt; }
    public ReportContent getContent() { return content; }
    public ReportMetadata getMetadata() { return metadata; }
    
    /**
     * Returns the report parameters.
     */
    public ReportParameters getParameters() {
        return new ReportParameters(
            timeRange != null ? timeRange.getStartDate() : null,
            timeRange != null ? timeRange.getEndDate() : null,
            deviceIds
        );
    }
    
    /**
     * Inner class for report parameters.
     */
    public record ReportParameters(Instant startDate, Instant endDate, List<UUID> deviceIds) {
        public List<String> getDeviceIdStrings() {
            return deviceIds.stream().map(UUID::toString).toList();
        }
    }
}