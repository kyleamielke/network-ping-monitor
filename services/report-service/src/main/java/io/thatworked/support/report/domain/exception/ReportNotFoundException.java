package io.thatworked.support.report.domain.exception;

import java.util.UUID;

/**
 * Exception thrown when a report is not found.
 */
public class ReportNotFoundException extends RuntimeException {
    
    private final UUID reportId;
    
    public ReportNotFoundException(UUID reportId) {
        super("Report not found: " + reportId.toString());
        this.reportId = reportId;
    }
    
    public UUID getReportId() {
        return reportId;
    }
}