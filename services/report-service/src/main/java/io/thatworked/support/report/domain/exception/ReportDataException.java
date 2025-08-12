package io.thatworked.support.report.domain.exception;

/**
 * Exception thrown when there are issues with report data collection or validation.
 */
public class ReportDataException extends ReportDomainException {
    
    public ReportDataException(String message) {
        super(message);
    }
    
    public ReportDataException(String message, Throwable cause) {
        super(message, cause);
    }
}