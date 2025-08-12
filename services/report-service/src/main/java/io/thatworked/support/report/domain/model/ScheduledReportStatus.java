package io.thatworked.support.report.domain.model;

/**
 * Enumeration of possible scheduled report execution statuses.
 */
public enum ScheduledReportStatus {
    /**
     * Report has never been executed.
     */
    NEVER_RUN,
    
    /**
     * Report execution completed successfully.
     */
    SUCCESS,
    
    /**
     * Report execution failed.
     */
    FAILED,
    
    /**
     * Report execution was cancelled.
     */
    CANCELLED,
    
    /**
     * Report execution timed out.
     */
    TIMEOUT
}