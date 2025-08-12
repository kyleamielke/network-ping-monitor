package io.thatworked.support.report.domain.port;

import io.thatworked.support.report.domain.model.*;

/**
 * Port for report generation capabilities.
 */
public interface ReportGeneratorPort {
    
    /**
     * Generates a report in the specified format with the given data.
     */
    ReportContent generateReport(Report report, ReportData data);
    
    /**
     * Checks if this generator supports the specified format.
     */
    boolean supports(ReportFormat format);
    
    /**
     * Data transfer object containing all data needed for report generation.
     */
    record ReportData(
        java.util.List<DeviceDataPort.DeviceData> devices,
        java.util.List<PingDataPort.PingStatistics> pingStatistics,
        java.util.List<AlertDataPort.AlertData> alerts
    ) {}
}