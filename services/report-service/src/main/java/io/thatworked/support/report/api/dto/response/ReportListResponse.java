package io.thatworked.support.report.api.dto.response;

import java.util.List;

/**
 * Response DTO for listing reports.
 */
public class ReportListResponse {
    
    private List<ReportSummary> reports;
    private long totalCount;
    private int pageSize;
    private int pageNumber;
    
    // Constructors
    public ReportListResponse() {}
    
    public ReportListResponse(List<ReportSummary> reports, long totalCount, int pageSize, int pageNumber) {
        this.reports = reports;
        this.totalCount = totalCount;
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
    }
    
    // Getters and Setters
    public List<ReportSummary> getReports() { return reports; }
    public void setReports(List<ReportSummary> reports) { this.reports = reports; }
    
    public long getTotalCount() { return totalCount; }
    public void setTotalCount(long totalCount) { this.totalCount = totalCount; }
    
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
    
    public int getPageNumber() { return pageNumber; }
    public void setPageNumber(int pageNumber) { this.pageNumber = pageNumber; }
}