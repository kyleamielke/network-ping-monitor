package io.thatworked.support.report.api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

/**
 * Response DTO for report generation result.
 */
public class ReportResponse {
    
    private String id;
    private String filename;
    private String reportType;
    private String format;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant generatedAt;
    
    private long fileSizeBytes;
    private String downloadUrl;
    
    // Constructors
    public ReportResponse() {}
    
    public ReportResponse(String id, String filename, String reportType,
                         String format, Instant generatedAt, long fileSizeBytes, String downloadUrl) {
        this.id = id;
        this.filename = filename;
        this.reportType = reportType;
        this.format = format;
        this.generatedAt = generatedAt;
        this.fileSizeBytes = fileSizeBytes;
        this.downloadUrl = downloadUrl;
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
    
    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
}