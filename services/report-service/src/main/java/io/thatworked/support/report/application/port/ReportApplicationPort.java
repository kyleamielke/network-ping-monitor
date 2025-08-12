package io.thatworked.support.report.application.port;

import io.thatworked.support.report.application.service.ReportApplicationService;

/**
 * Port interface defining the application service contract for report operations.
 */
public interface ReportApplicationPort {
    
    /**
     * Generates a report based on the provided request.
     */
    ReportApplicationService.ReportResponse generateReport(ReportApplicationService.ReportRequest request);
}