package io.thatworked.support.report.domain.exception;

/**
 * Base exception for all domain-level exceptions in the report service.
 */
public class ReportDomainException extends RuntimeException {
    
    public ReportDomainException(String message) {
        super(message);
    }
    
    public ReportDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}