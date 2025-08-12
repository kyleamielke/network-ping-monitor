package io.thatworked.support.report.api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;

/**
 * Response DTO for report status inquiry.
 */
public class ReportStatusResponse {
    
    private String id;
    private String status;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant generatedAt;
    
    private long fileSizeBytes;
    private Long processingTimeMs;
    private String error;
    
    // Constructors
    public ReportStatusResponse() {}
    
    public ReportStatusResponse(String id, String status, Instant generatedAt,
                               long fileSizeBytes, Long processingTimeMs, String error) {
        this.id = id;
        this.status = status;
        this.generatedAt = generatedAt;
        this.fileSizeBytes = fileSizeBytes;
        this.processingTimeMs = processingTimeMs;
        this.error = error;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Instant getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(Instant generatedAt) { this.generatedAt = generatedAt; }
    
    public long getFileSizeBytes() { return fileSizeBytes; }
    public void setFileSizeBytes(long fileSizeBytes) { this.fileSizeBytes = fileSizeBytes; }
    
    public Long getProcessingTimeMs() { return processingTimeMs; }
    public void setProcessingTimeMs(Long processingTimeMs) { this.processingTimeMs = processingTimeMs; }
    
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}