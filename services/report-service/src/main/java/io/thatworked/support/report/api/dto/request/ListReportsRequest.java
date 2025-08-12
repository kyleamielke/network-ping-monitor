package io.thatworked.support.report.api.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;

/**
 * Request DTO for listing reports with optional filtering.
 */
public class ListReportsRequest {
    
    private String reportType;
    
    private String format;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant startDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant endDate;
    
    @Min(1)
    @Max(100)
    private Integer limit = 20;
    
    @Min(0)
    private Integer offset = 0;
    
    private String sortBy = "generatedAt";
    
    private String sortOrder = "DESC";
    
    // Getters and Setters
    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }
    
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
    
    public Instant getStartDate() { return startDate; }
    public void setStartDate(Instant startDate) { this.startDate = startDate; }
    
    public Instant getEndDate() { return endDate; }
    public void setEndDate(Instant endDate) { this.endDate = endDate; }
    
    public Integer getLimit() { return limit; }
    public void setLimit(Integer limit) { this.limit = limit; }
    
    public Integer getOffset() { return offset; }
    public void setOffset(Integer offset) { this.offset = offset; }
    
    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }
    
    public String getSortOrder() { return sortOrder; }
    public void setSortOrder(String sortOrder) { this.sortOrder = sortOrder; }
}