package io.thatworked.support.report.domain.service;

import io.thatworked.support.report.domain.model.*;
import io.thatworked.support.report.domain.port.*;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Builds structured report documents from raw data.
 * This centralizes all report logic in one place, following DRY principles.
 */
public class ReportDocumentBuilder {
    
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Builds a report document based on the report type and data.
     */
    public ReportDocument buildDocument(Report report, ReportGeneratorPort.ReportData data) {
        return switch (report.getReportType()) {
            case UPTIME_SUMMARY -> buildUptimeSummaryDocument(report, data);
            case DEVICE_STATUS -> buildDeviceStatusDocument(report, data);
            case PING_PERFORMANCE -> buildPingPerformanceDocument(report, data);
            case ALERT_HISTORY -> buildAlertHistoryDocument(report, data);
        };
    }
    
    private ReportDocument buildUptimeSummaryDocument(Report report, ReportGeneratorPort.ReportData data) {
        var builder = new ReportDocument.Builder()
            .title(report.getTitle() != null ? report.getTitle() : "Uptime Summary Report")
            .subtitle("Network Device Availability Report")
            .timeRange(report.getTimeRange());
        
        if (data.pingStatistics() == null || data.pingStatistics().isEmpty()) {
            builder.addSection(new ReportDocument.Section(
                "No Data Available",
                ReportDocument.SectionType.SUMMARY,
                List.of(),
                List.of(),
                "No uptime data available for the selected period."
            ));
        } else {
            var headers = List.of("Device Name", "Monitored Address", "Total Pings", "Uptime %", "Avg Response (ms)", "Success Rate %");
            var rows = new ArrayList<ReportDocument.Row>();
            
            for (var stats : data.pingStatistics()) {
                // Use hostname if available, otherwise fall back to IP address
                String monitoredAddress = stats.hostname() != null && !stats.hostname().isEmpty() 
                    ? stats.hostname() 
                    : (stats.ipAddress() != null ? stats.ipAddress() : "N/A");
                
                rows.add(new ReportDocument.Row(List.of(
                    new ReportDocument.Cell(
                        stats.deviceName() != null ? stats.deviceName() : "Unknown",
                        ReportDocument.CellType.TEXT,
                        null
                    ),
                    new ReportDocument.Cell(
                        monitoredAddress,
                        ReportDocument.CellType.TEXT,
                        null
                    ),
                    new ReportDocument.Cell(
                        String.valueOf(stats.totalPings()),
                        ReportDocument.CellType.NUMBER,
                        "0"
                    ),
                    new ReportDocument.Cell(
                        String.valueOf(stats.uptimePercentage()),
                        ReportDocument.CellType.PERCENTAGE,
                        "0.00"
                    ),
                    new ReportDocument.Cell(
                        String.valueOf(stats.averageResponseTime()),
                        ReportDocument.CellType.NUMBER,
                        "0.00"
                    ),
                    new ReportDocument.Cell(
                        String.valueOf((double) stats.successfulPings() / stats.totalPings() * 100),
                        ReportDocument.CellType.PERCENTAGE,
                        "0.00"
                    )
                )));
            }
            
            builder.addSection(new ReportDocument.Section(
                "Device Uptime Statistics",
                ReportDocument.SectionType.TABLE,
                headers,
                rows,
                null
            ));
        }
        
        return builder.build();
    }
    
    private ReportDocument buildDeviceStatusDocument(Report report, ReportGeneratorPort.ReportData data) {
        var builder = new ReportDocument.Builder()
            .title(report.getTitle() != null ? report.getTitle() : "Device Status Report")
            .subtitle("Current Device Monitoring Status")
            .timeRange(report.getTimeRange());
        
        if (data.devices() == null || data.devices().isEmpty()) {
            builder.addSection(new ReportDocument.Section(
                "No Devices Found",
                ReportDocument.SectionType.SUMMARY,
                List.of(),
                List.of(),
                "No devices found in the system."
            ));
        } else {
            var headers = List.of("Device Name", "Monitored Address", "Type", "Monitoring Status", "Status");
            var rows = new ArrayList<ReportDocument.Row>();
            
            for (var device : data.devices()) {
                // Use hostname if available, otherwise fall back to IP address
                String monitoredAddress = device.hostname() != null && !device.hostname().isEmpty() 
                    ? device.hostname() 
                    : (device.ipAddress() != null ? device.ipAddress() : "N/A");
                
                rows.add(new ReportDocument.Row(List.of(
                    new ReportDocument.Cell(
                        device.name() != null ? device.name() : "Unnamed",
                        ReportDocument.CellType.TEXT,
                        null
                    ),
                    new ReportDocument.Cell(
                        monitoredAddress,
                        ReportDocument.CellType.TEXT,
                        null
                    ),
                    new ReportDocument.Cell(
                        device.type() != null ? device.type() : "N/A",
                        ReportDocument.CellType.TEXT,
                        null
                    ),
                    new ReportDocument.Cell(
                        device.isActive() ? "Monitored" : "Not Monitored",
                        ReportDocument.CellType.TEXT,
                        null
                    ),
                    new ReportDocument.Cell(
                        device.isActive() ? (device.isUp() ? "Up" : "Down") : "N/A",
                        ReportDocument.CellType.TEXT,
                        null
                    )
                )));
            }
            
            builder.addSection(new ReportDocument.Section(
                "Device Status",
                ReportDocument.SectionType.TABLE,
                headers,
                rows,
                null
            ));
        }
        
        return builder.build();
    }
    
    private ReportDocument buildPingPerformanceDocument(Report report, ReportGeneratorPort.ReportData data) {
        var builder = new ReportDocument.Builder()
            .title(report.getTitle() != null ? report.getTitle() : "Ping Performance Report")
            .subtitle("Network Performance Metrics")
            .timeRange(report.getTimeRange());
        
        if (data.pingStatistics() == null || data.pingStatistics().isEmpty()) {
            builder.addSection(new ReportDocument.Section(
                "No Performance Data",
                ReportDocument.SectionType.SUMMARY,
                List.of(),
                List.of(),
                "No ping performance data available for the selected period."
            ));
        } else {
            var headers = List.of("Device Name", "Monitored Address", "Total Pings", "Avg Response (ms)", "Min Response (ms)", "Max Response (ms)", "Success Rate %");
            var rows = new ArrayList<ReportDocument.Row>();
            
            for (var stats : data.pingStatistics()) {
                rows.add(new ReportDocument.Row(List.of(
                    new ReportDocument.Cell(
                        stats.deviceName() != null ? stats.deviceName() : "Unknown",
                        ReportDocument.CellType.TEXT,
                        null
                    ),
                    new ReportDocument.Cell(
                        stats.hostname() != null && !stats.hostname().isEmpty() 
                            ? stats.hostname() 
                            : (stats.ipAddress() != null ? stats.ipAddress() : "N/A"),
                        ReportDocument.CellType.TEXT,
                        null
                    ),
                    new ReportDocument.Cell(
                        String.valueOf(stats.totalPings()),
                        ReportDocument.CellType.NUMBER,
                        "0"
                    ),
                    new ReportDocument.Cell(
                        String.valueOf(stats.averageResponseTime()),
                        ReportDocument.CellType.NUMBER,
                        "0.00"
                    ),
                    new ReportDocument.Cell(
                        String.valueOf(stats.minResponseTime()),
                        ReportDocument.CellType.NUMBER,
                        "0.00"
                    ),
                    new ReportDocument.Cell(
                        String.valueOf(stats.maxResponseTime()),
                        ReportDocument.CellType.NUMBER,
                        "0.00"
                    ),
                    new ReportDocument.Cell(
                        String.valueOf((double) stats.successfulPings() / stats.totalPings() * 100),
                        ReportDocument.CellType.PERCENTAGE,
                        "0.00"
                    )
                )));
            }
            
            builder.addSection(new ReportDocument.Section(
                "Ping Performance Metrics",
                ReportDocument.SectionType.TABLE,
                headers,
                rows,
                null
            ));
        }
        
        return builder.build();
    }
    
    private ReportDocument buildAlertHistoryDocument(Report report, ReportGeneratorPort.ReportData data) {
        var builder = new ReportDocument.Builder()
            .title(report.getTitle() != null ? report.getTitle() : "Alert History Report")
            .subtitle("System Alert History")
            .timeRange(report.getTimeRange());
        
        if (data.alerts() == null || data.alerts().isEmpty()) {
            builder.addSection(new ReportDocument.Section(
                "No Alerts",
                ReportDocument.SectionType.SUMMARY,
                List.of(),
                List.of(),
                "No alerts found for the selected period."
            ));
        } else {
            var headers = List.of("Timestamp", "Device", "Alert Type", "Message", "Severity", "Status");
            var rows = new ArrayList<ReportDocument.Row>();
            
            for (var alert : data.alerts()) {
                String status = alert.status() != null ? alert.status().name() : "Active";
                if (alert.resolvedAt() != null) {
                    status = "Resolved";
                }
                
                rows.add(new ReportDocument.Row(List.of(
                    new ReportDocument.Cell(
                        DISPLAY_FORMAT.format(alert.timestamp().atOffset(ZoneOffset.UTC)),
                        ReportDocument.CellType.DATE,
                        "yyyy-MM-dd HH:mm:ss"
                    ),
                    new ReportDocument.Cell(
                        alert.deviceName() != null ? alert.deviceName() : "Unknown",
                        ReportDocument.CellType.TEXT,
                        null
                    ),
                    new ReportDocument.Cell(
                        alert.alertType().name(),
                        ReportDocument.CellType.TEXT,
                        null
                    ),
                    new ReportDocument.Cell(
                        alert.message(),
                        ReportDocument.CellType.TEXT,
                        null
                    ),
                    new ReportDocument.Cell(
                        alert.severity().name(),
                        ReportDocument.CellType.TEXT,
                        null
                    ),
                    new ReportDocument.Cell(
                        status,
                        ReportDocument.CellType.TEXT,
                        null
                    )
                )));
            }
            
            builder.addSection(new ReportDocument.Section(
                "Alert History",
                ReportDocument.SectionType.TABLE,
                headers,
                rows,
                null
            ));
        }
        
        return builder.build();
    }
}