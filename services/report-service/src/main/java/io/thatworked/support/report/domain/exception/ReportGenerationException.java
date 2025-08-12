package io.thatworked.support.report.domain.exception;

import io.thatworked.support.report.domain.model.ReportType;
import java.util.UUID;

/**
 * Exception thrown when report generation fails.
 */
public class ReportGenerationException extends ReportDomainException {
    
    private final UUID reportId;
    private final ReportType reportType;
    
    public ReportGenerationException(UUID reportId, ReportType reportType, String message) {
        super(String.format("Failed to generate %s report (ID: %s): %s", 
                           reportType.getDisplayName(), reportId, message));
        this.reportId = reportId;
        this.reportType = reportType;
    }
    
    public ReportGenerationException(UUID reportId, ReportType reportType, String message, Throwable cause) {
        super(String.format("Failed to generate %s report (ID: %s): %s", 
                           reportType.getDisplayName(), reportId, message), cause);
        this.reportId = reportId;
        this.reportType = reportType;
    }
    
    public UUID getReportId() { return reportId; }
    public ReportType getReportType() { return reportType; }
}