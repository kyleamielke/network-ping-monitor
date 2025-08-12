package io.thatworked.support.report.api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;

/**
 * Summary information for a report in list responses.
 */
public class ReportSummary {
    
    private String id;
    private String filename;
    private String reportType;
    private String format;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant generatedAt;
    
    private long fileSizeBytes;
    private String title;
    
    // Constructors
    public ReportSummary() {}
    
    public ReportSummary(String id, String filename, String reportType,
                        String format, Instant generatedAt, long fileSizeBytes, String title) {
        this.id = id;
        this.filename = filename;
        this.reportType = reportType;
        this.format = format;
        this.generatedAt = generatedAt;
        this.fileSizeBytes = fileSizeBytes;
        this.title = title;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    
    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }
    
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
    
    public Instant getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(Instant generatedAt) { this.generatedAt = generatedAt; }
    
    public long getFileSizeBytes() { return fileSizeBytes; }
    public void setFileSizeBytes(long fileSizeBytes) { this.fileSizeBytes = fileSizeBytes; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}