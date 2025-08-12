package io.thatworked.support.report.domain.port;

import io.thatworked.support.report.domain.model.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for report persistence operations.
 */
public interface ReportRepository {
    
    /**
     * Saves a report.
     */
    Report save(Report report);
    
    /**
     * Finds a report by ID.
     */
    Optional<Report> findById(UUID id);
    
    /**
     * Finds reports with optional filtering.
     */
    List<Report> findReports(ReportType type, ReportFormat format, 
                           Instant startDate, Instant endDate, int limit);
    
    /**
     * Finds reports older than the given date.
     */
    List<Report> findReportsOlderThan(Instant cutoffDate);
    
    /**
     * Deletes a report.
     */
    void delete(UUID id);
    
    /**
     * Counts total number of reports.
     */
    long count();
}