package io.thatworked.support.report.domain.service;

import io.thatworked.support.report.domain.model.*;
import io.thatworked.support.report.domain.port.*;
import io.thatworked.support.report.domain.exception.ReportGenerationException;
import io.thatworked.support.report.domain.exception.ReportDataException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Domain service containing core business logic for report generation.
 */
public class ReportDomainService {
    
    private final DeviceDataPort deviceDataPort;
    private final PingDataPort pingDataPort;
    private final AlertDataPort alertDataPort;
    private final ReportGeneratorPort reportGeneratorPort;
    private final FileStoragePort fileStoragePort;
    private final DomainLogger logger;
    
    public ReportDomainService(DeviceDataPort deviceDataPort,
                              PingDataPort pingDataPort,
                              AlertDataPort alertDataPort,
                              ReportGeneratorPort reportGeneratorPort,
                              FileStoragePort fileStoragePort,
                              DomainLogger logger) {
        this.deviceDataPort = deviceDataPort;
        this.pingDataPort = pingDataPort;
        this.alertDataPort = alertDataPort;
        this.reportGeneratorPort = reportGeneratorPort;
        this.fileStoragePort = fileStoragePort;
        this.logger = logger;
    }
    
    /**
     * Generates a complete report including data collection, generation, and storage.
     */
    public Report generateReport(ReportType reportType,
                                ReportFormat format,
                                ReportTimeRange timeRange,
                                List<UUID> deviceIds,
                                String title) {
        
        Instant startTime = Instant.now();
        Report report = Report.create(reportType, format, timeRange, deviceIds, title);
        
        logger.logBusinessEvent(
            "Report generation started",
            java.util.Map.of(
                "reportId", report.getId().toString(),
                "reportType", reportType.name(),
                "format", format.name(),
                "timeRange", timeRange.toString()
            )
        );
        
        try {
            // Validate report can be generated
            if (!report.canGenerate()) {
                throw new ReportGenerationException(
                    report.getId(), 
                    reportType, 
                    "Report validation failed - missing required parameters"
                );
            }
            
            // Collect data
            ReportGeneratorPort.ReportData data = collectReportData(report);
            
            // Generate content
            if (!reportGeneratorPort.supports(format)) {
                throw new ReportGenerationException(
                    report.getId(),
                    reportType,
                    "Unsupported report format: " + format.name()
                );
            }
            
            ReportContent content = reportGeneratorPort.generateReport(report, data);
            
            // Store file
            String downloadUrl = fileStoragePort.storeReportFile(
                report.getId(),
                report.generateFilename(),
                content
            );
            
            // Calculate generation time
            String duration = calculateDuration(startTime, Instant.now());
            
            // Create final report with metadata
            ReportMetadata metadata = ReportMetadata.of(
                content.getSize(),
                downloadUrl,
                calculateRecordCount(data),
                duration
            );
            
            Report finalReport = report.withContent(content)
                .withMetadata(metadata);
            
            logger.logBusinessEvent(
                "Report generation completed",
                java.util.Map.of(
                    "reportId", report.getId().toString(),
                    "fileSize", content.getSize(),
                    "duration", duration,
                    "recordCount", calculateRecordCount(data)
                )
            );
            
            return finalReport;
            
        } catch (ReportGenerationException e) {
            logger.logBusinessWarning(
                "Report generation failed",
                java.util.Map.of(
                    "reportId", report.getId().toString(),
                    "reportType", reportType.name(),
                    "error", e.getMessage()
                )
            );
            throw e;
        } catch (Exception e) {
            logger.logBusinessWarning(
                "Unexpected error during report generation",
                java.util.Map.of(
                    "reportId", report.getId().toString(),
                    "reportType", reportType.name(),
                    "error", e.getMessage()
                )
            );
            throw new ReportGenerationException(
                report.getId(),
                reportType,
                "Unexpected error: " + e.getMessage(),
                e
            );
        }
    }
    
    /**
     * Collects all data needed for report generation.
     */
    private ReportGeneratorPort.ReportData collectReportData(Report report) {
        try {
            // Collect device data
            List<DeviceDataPort.DeviceData> devices = collectDeviceData(report);
            
            // Enrich devices with monitoring status
            devices = enrichDevicesWithMonitoringStatus(devices);
            
            // Collect ping data if needed
            List<PingDataPort.PingStatistics> pingStatistics = 
                report.getReportType().requiresPingData() 
                    ? collectPingData(report) 
                    : List.of();
            
            // Collect alert data if needed
            List<AlertDataPort.AlertData> alerts = 
                report.getReportType() == ReportType.ALERT_HISTORY
                    ? collectAlertData(report)
                    : List.of();
            
            return new ReportGeneratorPort.ReportData(devices, pingStatistics, alerts);
            
        } catch (Exception e) {
            throw new ReportDataException("Failed to collect report data: " + e.getMessage(), e);
        }
    }
    
    private List<DeviceDataPort.DeviceData> collectDeviceData(Report report) {
        if (report.getDeviceIds() != null && !report.getDeviceIds().isEmpty()) {
            return deviceDataPort.getDevices(report.getDeviceIds());
        } else {
            return deviceDataPort.getAllDevices();
        }
    }
    
    private List<PingDataPort.PingStatistics> collectPingData(Report report) {
        if (report.getDeviceIds() != null && !report.getDeviceIds().isEmpty()) {
            return pingDataPort.getDeviceStatistics(report.getDeviceIds(), report.getTimeRange());
        } else {
            return pingDataPort.getAllDeviceStatistics(report.getTimeRange());
        }
    }
    
    private List<AlertDataPort.AlertData> collectAlertData(Report report) {
        if (report.getDeviceIds() != null && !report.getDeviceIds().isEmpty()) {
            return alertDataPort.getDeviceAlertsInTimeRange(report.getDeviceIds(), report.getTimeRange());
        } else {
            return alertDataPort.getAlertsInTimeRange(report.getTimeRange());
        }
    }
    
    private List<DeviceDataPort.DeviceData> enrichDevicesWithMonitoringStatus(List<DeviceDataPort.DeviceData> devices) {
        try {
            // Get all ping targets to determine monitoring status
            List<PingDataPort.PingTarget> pingTargets = pingDataPort.getAllPingTargets();
            
            // Create a map of device ID to ping target for quick lookup
            java.util.Map<UUID, PingDataPort.PingTarget> deviceIdToPingTarget = pingTargets.stream()
                .collect(java.util.stream.Collectors.toMap(
                    PingDataPort.PingTarget::deviceId,
                    target -> target,
                    (existing, replacement) -> existing
                ));
            
            // Enrich each device with monitoring status
            return devices.stream()
                .map(device -> {
                    PingDataPort.PingTarget pingTarget = deviceIdToPingTarget.get(device.deviceId());
                    boolean isMonitored = pingTarget != null && pingTarget.isMonitored();
                    
                    // If monitored, check recent ping results to determine up/down status
                    boolean isUp = false;
                    if (isMonitored) {
                        try {
                            List<PingDataPort.PingResult> recentResults = pingDataPort.getRecentPingResults(device.deviceId(), 1);
                            if (!recentResults.isEmpty()) {
                                isUp = recentResults.get(0).isSuccess();
                            }
                        } catch (Exception e) {
                            logger.logBusinessWarning("Failed to fetch ping status for device", 
                                java.util.Map.of("deviceId", device.deviceId().toString(), "error", e.getMessage()));
                        }
                    }
                    
                    // Create enriched device data
                    return new DeviceDataPort.DeviceData(
                        device.deviceId(),
                        device.name(),
                        device.ipAddress(),
                        device.hostname(),
                        device.type(),
                        isMonitored,  // Use actual monitoring status from ping target
                        isUp,         // Use actual up/down status from ping results
                        device.location()
                    );
                })
                .collect(java.util.stream.Collectors.toList());
                
        } catch (Exception e) {
            logger.logBusinessWarning("Failed to enrich devices with monitoring status", 
                java.util.Map.of("error", e.getMessage()));
            // Return original devices if enrichment fails
            return devices;
        }
    }
    
    private int calculateRecordCount(ReportGeneratorPort.ReportData data) {
        return data.devices().size() + data.pingStatistics().size() + data.alerts().size();
    }
    
    private String calculateDuration(Instant start, Instant end) {
        long millis = java.time.Duration.between(start, end).toMillis();
        if (millis < 1000) {
            return millis + "ms";
        } else {
            return String.format("%.2fs", millis / 1000.0);
        }
    }
}