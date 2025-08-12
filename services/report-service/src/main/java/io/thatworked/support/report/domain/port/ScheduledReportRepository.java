package io.thatworked.support.report.domain.port;

import io.thatworked.support.report.domain.model.ScheduledReport;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for scheduled report persistence operations.
 */
public interface ScheduledReportRepository {
    
    /**
     * Saves a scheduled report.
     */
    ScheduledReport save(ScheduledReport scheduledReport);
    
    /**
     * Finds a scheduled report by ID.
     */
    Optional<ScheduledReport> findById(UUID id);
    
    /**
     * Finds all active scheduled reports.
     */
    List<ScheduledReport> findActiveReports();
    
    /**
     * Finds scheduled reports due for execution.
     */
    List<ScheduledReport> findReportsDueForExecution(Instant beforeTime);
    
    /**
     * Deletes a scheduled report.
     */
    void delete(UUID id);
}