package io.thatworked.support.report.domain.service;

import io.thatworked.support.report.domain.exception.ReportDataException;
import io.thatworked.support.report.domain.exception.ReportGenerationException;
import io.thatworked.support.report.domain.model.*;
import io.thatworked.support.report.domain.port.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("ReportDomainService Tests")
class ReportDomainServiceTest {
    
    @Mock
    private DeviceDataPort deviceDataPort;
    
    @Mock
    private PingDataPort pingDataPort;
    
    @Mock
    private AlertDataPort alertDataPort;
    
    @Mock
    private ReportGeneratorPort reportGeneratorPort;
    
    @Mock
    private FileStoragePort fileStoragePort;
    
    @Mock
    private DomainLogger logger;
    
    private ReportDomainService service;
    
    @BeforeEach
    void setUp() {
        service = new ReportDomainService(
            deviceDataPort,
            pingDataPort,
            alertDataPort,
            reportGeneratorPort,
            fileStoragePort,
            logger
        );
    }
    
    @Test
    @DisplayName("Should generate device status report successfully")
    void testGenerateDeviceStatusReport() {
        // Given
        ReportType reportType = ReportType.DEVICE_STATUS;
        ReportFormat format = ReportFormat.PDF;
        Instant now = Instant.now();
        ReportTimeRange timeRange = ReportTimeRange.of(
            now.minus(24, ChronoUnit.HOURS),
            now
        );
        List<UUID> deviceIds = List.of(UUID.randomUUID(), UUID.randomUUID());
        String title = "Device Status Report";
        
        // Mock device data
        List<DeviceDataPort.DeviceData> devices = List.of(
            new DeviceDataPort.DeviceData(
                deviceIds.get(0),
                "device-1",
                "192.168.1.100",
                "host1.example.com",
                "SERVER",
                true,
                true,
                "DataCenter A"
            ),
            new DeviceDataPort.DeviceData(
                deviceIds.get(1),
                "device-2",
                "192.168.1.101",
                "host2.example.com",
                "ROUTER",
                true,
                false,
                "DataCenter B"
            )
        );
        
        // Mock ping targets
        List<PingDataPort.PingTarget> pingTargets = List.of(
            new PingDataPort.PingTarget(deviceIds.get(0), true, 5000, 30),
            new PingDataPort.PingTarget(deviceIds.get(1), true, 5000, 30)
        );
        
        // Mock recent ping results
        List<PingDataPort.PingResult> recentResults = List.of(
            new PingDataPort.PingResult(deviceIds.get(0), Instant.now(), true, 15.5, null)
        );
        
        // Mock report content
        byte[] pdfContent = "PDF content".getBytes();
        ReportContent content = ReportContent.of(pdfContent);
        
        when(deviceDataPort.getDevices(deviceIds)).thenReturn(devices);
        when(pingDataPort.getAllPingTargets()).thenReturn(pingTargets);
        when(pingDataPort.getRecentPingResults(eq(deviceIds.get(0)), eq(1))).thenReturn(recentResults);
        when(pingDataPort.getRecentPingResults(eq(deviceIds.get(1)), eq(1))).thenReturn(List.of());
        when(reportGeneratorPort.supports(format)).thenReturn(true);
        when(reportGeneratorPort.generateReport(any(Report.class), any(ReportGeneratorPort.ReportData.class)))
            .thenReturn(content);
        when(fileStoragePort.storeReportFile(any(UUID.class), anyString(), eq(content)))
            .thenReturn("https://storage.example.com/reports/report-123.pdf");
        
        // When
        Report report = service.generateReport(reportType, format, timeRange, deviceIds, title);
        
        // Then
        assertThat(report).isNotNull();
        assertThat(report.getReportType()).isEqualTo(reportType);
        assertThat(report.getFormat()).isEqualTo(format);
        assertThat(report.getTitle()).isEqualTo(title);
        assertThat(report.getContent()).isEqualTo(content);
        assertThat(report.getMetadata()).isNotNull();
        assertThat(report.getMetadata().getDownloadUrl()).isEqualTo("https://storage.example.com/reports/report-123.pdf");
        
        verify(deviceDataPort).getDevices(deviceIds);
        verify(reportGeneratorPort).generateReport(any(Report.class), any(ReportGeneratorPort.ReportData.class));
        verify(fileStoragePort).storeReportFile(any(UUID.class), anyString(), eq(content));
        verify(logger).logBusinessEvent(eq("Report generation started"), any(Map.class));
        verify(logger).logBusinessEvent(eq("Report generation completed"), any(Map.class));
    }
    
    @Test
    @DisplayName("Should generate uptime report with ping statistics")
    void testGenerateUptimeReport() {
        // Given
        ReportType reportType = ReportType.UPTIME_SUMMARY;
        ReportFormat format = ReportFormat.CSV;
        Instant now = Instant.now();
        ReportTimeRange timeRange = ReportTimeRange.of(
            now.minus(7, ChronoUnit.DAYS),
            now
        );
        String title = "Weekly Uptime Report";
        
        // Mock data
        List<DeviceDataPort.DeviceData> devices = List.of(
            new DeviceDataPort.DeviceData(
                UUID.randomUUID(),
                "device-1",
                "192.168.1.100",
                null,
                "SERVER",
                true,
                true,
                null
            )
        );
        
        List<PingDataPort.PingStatistics> pingStats = List.of(
            new PingDataPort.PingStatistics(
                devices.get(0).deviceId(),
                "device-1",
                "192.168.1.100",
                null,
                1000L,
                950L,
                50L,
                95.0,
                15.5,
                5.0,
                100.0,
                Instant.now().minusSeconds(3600),
                Instant.now()
            )
        );
        
        byte[] csvContent = "CSV content".getBytes();
        ReportContent content = ReportContent.of(csvContent);
        
        when(deviceDataPort.getAllDevices()).thenReturn(devices);
        when(pingDataPort.getAllPingTargets()).thenReturn(List.of());
        when(pingDataPort.getAllDeviceStatistics(timeRange)).thenReturn(pingStats);
        when(reportGeneratorPort.supports(format)).thenReturn(true);
        when(reportGeneratorPort.generateReport(any(Report.class), any(ReportGeneratorPort.ReportData.class)))
            .thenReturn(content);
        when(fileStoragePort.storeReportFile(any(UUID.class), anyString(), eq(content)))
            .thenReturn("https://storage.example.com/reports/uptime.csv");
        
        // When
        Report report = service.generateReport(reportType, format, timeRange, null, title);
        
        // Then
        assertThat(report).isNotNull();
        assertThat(report.getReportType()).isEqualTo(reportType);
        assertThat(report.getFormat()).isEqualTo(format);
        
        verify(deviceDataPort).getAllDevices();
        verify(pingDataPort).getAllDeviceStatistics(timeRange);
        verify(reportGeneratorPort).generateReport(any(Report.class), any(ReportGeneratorPort.ReportData.class));
    }
    
    @Test
    @DisplayName("Should generate alert history report")
    void testGenerateAlertHistoryReport() {
        // Given
        ReportType reportType = ReportType.ALERT_HISTORY;
        ReportFormat format = ReportFormat.PDF;
        Instant now = Instant.now();
        ReportTimeRange timeRange = ReportTimeRange.of(
            now.minus(30, ChronoUnit.DAYS),
            now
        );
        UUID deviceId = UUID.randomUUID();
        List<UUID> deviceIds = List.of(deviceId);
        String title = "Alert History Report";
        
        // Mock data
        List<DeviceDataPort.DeviceData> devices = List.of(
            new DeviceDataPort.DeviceData(
                deviceId,
                "device-1",
                "192.168.1.100",
                null,
                "SERVER",
                true,
                false,
                null
            )
        );
        
        List<AlertDataPort.AlertData> alerts = List.of(
            new AlertDataPort.AlertData(
                UUID.randomUUID().toString(),
                deviceId,
                "device-1",
                AlertDataPort.AlertType.DEVICE_DOWN,
                AlertDataPort.AlertSeverity.CRITICAL,
                "Device not responding",
                now.minus(2, ChronoUnit.HOURS),
                AlertDataPort.AlertStatus.RESOLVED,
                now.minus(1, ChronoUnit.HOURS)
            )
        );
        
        byte[] pdfContent = "PDF content".getBytes();
        ReportContent content = ReportContent.of(pdfContent);
        
        when(deviceDataPort.getDevices(deviceIds)).thenReturn(devices);
        when(pingDataPort.getAllPingTargets()).thenReturn(List.of());
        when(alertDataPort.getDeviceAlertsInTimeRange(deviceIds, timeRange)).thenReturn(alerts);
        when(reportGeneratorPort.supports(format)).thenReturn(true);
        when(reportGeneratorPort.generateReport(any(Report.class), any(ReportGeneratorPort.ReportData.class)))
            .thenReturn(content);
        when(fileStoragePort.storeReportFile(any(UUID.class), anyString(), eq(content)))
            .thenReturn("https://storage.example.com/reports/alerts.pdf");
        
        // When
        Report report = service.generateReport(reportType, format, timeRange, deviceIds, title);
        
        // Then
        assertThat(report).isNotNull();
        assertThat(report.getReportType()).isEqualTo(reportType);
        
        verify(alertDataPort).getDeviceAlertsInTimeRange(deviceIds, timeRange);
        verify(reportGeneratorPort).generateReport(any(Report.class), any(ReportGeneratorPort.ReportData.class));
    }
    
    @Test
    @DisplayName("Should throw exception for unsupported format")
    void testUnsupportedFormat() {
        // Given
        ReportType reportType = ReportType.DEVICE_STATUS;
        ReportFormat format = ReportFormat.PDF;
        ReportTimeRange timeRange = ReportTimeRange.of(
            Instant.now().minus(1, ChronoUnit.HOURS),
            Instant.now()
        );
        String title = "Test Report";
        
        when(deviceDataPort.getAllDevices()).thenReturn(List.of());
        when(pingDataPort.getAllPingTargets()).thenReturn(List.of());
        when(reportGeneratorPort.supports(format)).thenReturn(false);
        
        // When & Then
        assertThatThrownBy(() -> service.generateReport(reportType, format, timeRange, null, title))
            .isInstanceOf(ReportGenerationException.class)
            .hasMessageContaining("Unsupported report format: PDF");
        
        verify(logger).logBusinessWarning(eq("Report generation failed"), any(Map.class));
    }
    
    @Test
    @DisplayName("Should handle data collection failure")
    void testDataCollectionFailure() {
        // Given
        ReportType reportType = ReportType.DEVICE_STATUS;
        ReportFormat format = ReportFormat.CSV;
        ReportTimeRange timeRange = ReportTimeRange.of(
            Instant.now().minus(1, ChronoUnit.HOURS),
            Instant.now()
        );
        String title = "Test Report";
        
        when(deviceDataPort.getAllDevices()).thenThrow(new RuntimeException("Database error"));
        
        // When & Then
        assertThatThrownBy(() -> service.generateReport(reportType, format, timeRange, null, title))
            .isInstanceOf(ReportGenerationException.class)
            .hasMessageContaining("Unexpected error");
        
        verify(logger).logBusinessWarning(eq("Unexpected error during report generation"), any(Map.class));
    }
    
    @Test
    @DisplayName("Should enrich devices with monitoring status")
    void testEnrichDevicesWithMonitoringStatus() {
        // Given
        ReportType reportType = ReportType.DEVICE_STATUS;
        ReportFormat format = ReportFormat.CSV;
        ReportTimeRange timeRange = ReportTimeRange.of(
            Instant.now().minus(1, ChronoUnit.HOURS),
            Instant.now()
        );
        UUID deviceId = UUID.randomUUID();
        String title = "Status Report";
        
        List<DeviceDataPort.DeviceData> devices = List.of(
            new DeviceDataPort.DeviceData(
                deviceId,
                "device-1",
                "192.168.1.100",
                null,
                "SERVER",
                false, // Will be enriched
                false, // Will be enriched
                null
            )
        );
        
        List<PingDataPort.PingTarget> pingTargets = List.of(
            new PingDataPort.PingTarget(deviceId, true, 5000, 30)
        );
        
        List<PingDataPort.PingResult> recentResults = List.of(
            new PingDataPort.PingResult(deviceId, Instant.now(), true, 15.5, null)
        );
        
        byte[] csvContent = "CSV content".getBytes();
        ReportContent content = ReportContent.of(csvContent);
        
        when(deviceDataPort.getAllDevices()).thenReturn(devices);
        when(pingDataPort.getAllPingTargets()).thenReturn(pingTargets);
        when(pingDataPort.getRecentPingResults(deviceId, 1)).thenReturn(recentResults);
        when(reportGeneratorPort.supports(format)).thenReturn(true);
        when(reportGeneratorPort.generateReport(any(Report.class), any(ReportGeneratorPort.ReportData.class)))
            .thenReturn(content);
        when(fileStoragePort.storeReportFile(any(UUID.class), anyString(), eq(content)))
            .thenReturn("https://storage.example.com/reports/status.csv");
        
        // When
        Report report = service.generateReport(reportType, format, timeRange, null, title);
        
        // Then
        assertThat(report).isNotNull();
        verify(pingDataPort).getAllPingTargets();
        verify(pingDataPort).getRecentPingResults(deviceId, 1);
    }
    
    @Test
    @DisplayName("Should handle file storage failure")
    void testFileStorageFailure() {
        // Given
        ReportType reportType = ReportType.DEVICE_STATUS;
        ReportFormat format = ReportFormat.PDF;
        ReportTimeRange timeRange = ReportTimeRange.of(
            Instant.now().minus(1, ChronoUnit.HOURS),
            Instant.now()
        );
        String title = "Test Report";
        
        byte[] pdfContent = "PDF content".getBytes();
        ReportContent content = ReportContent.of(pdfContent);
        
        when(deviceDataPort.getAllDevices()).thenReturn(List.of());
        when(pingDataPort.getAllPingTargets()).thenReturn(List.of());
        when(reportGeneratorPort.supports(format)).thenReturn(true);
        when(reportGeneratorPort.generateReport(any(Report.class), any(ReportGeneratorPort.ReportData.class)))
            .thenReturn(content);
        when(fileStoragePort.storeReportFile(any(UUID.class), anyString(), eq(content)))
            .thenThrow(new RuntimeException("Storage error"));
        
        // When & Then
        assertThatThrownBy(() -> service.generateReport(reportType, format, timeRange, null, title))
            .isInstanceOf(ReportGenerationException.class)
            .hasMessageContaining("Storage error");
    }
    
    @Test
    @DisplayName("Should handle ReportDataException when collecting data")
    void testReportDataException() {
        // Given
        ReportType reportType = ReportType.DEVICE_STATUS;
        ReportFormat format = ReportFormat.PDF;
        ReportTimeRange timeRange = ReportTimeRange.of(
            Instant.now().minus(1, ChronoUnit.HOURS),
            Instant.now()
        );
        String title = "Test Report";
        
        when(deviceDataPort.getAllDevices())
            .thenThrow(new ReportDataException("Failed to fetch devices", null));
        
        // When & Then
        assertThatThrownBy(() -> service.generateReport(reportType, format, timeRange, null, title))
            .isInstanceOf(ReportGenerationException.class)
            .hasMessageContaining("Failed to fetch devices");
    }
    
    @Test
    @DisplayName("Should calculate duration correctly")
    void testDurationCalculation() {
        // Given
        ReportType reportType = ReportType.DEVICE_STATUS;
        ReportFormat format = ReportFormat.CSV;
        ReportTimeRange timeRange = ReportTimeRange.of(
            Instant.now().minus(1, ChronoUnit.HOURS),
            Instant.now()
        );
        String title = "Quick Report";
        
        byte[] csvContent = "CSV content".getBytes();
        ReportContent content = ReportContent.of(csvContent);
        
        when(deviceDataPort.getAllDevices()).thenReturn(List.of());
        when(pingDataPort.getAllPingTargets()).thenReturn(List.of());
        when(reportGeneratorPort.supports(format)).thenReturn(true);
        when(reportGeneratorPort.generateReport(any(Report.class), any(ReportGeneratorPort.ReportData.class)))
            .thenReturn(content);
        when(fileStoragePort.storeReportFile(any(UUID.class), anyString(), eq(content)))
            .thenReturn("https://storage.example.com/reports/quick.csv");
        
        // When
        Report report = service.generateReport(reportType, format, timeRange, null, title);
        
        // Then
        assertThat(report).isNotNull();
        assertThat(report.getMetadata()).isNotNull();
        assertThat(report.getMetadata().getGenerationDuration()).isNotNull();
        // Duration should be in format like "123ms" or "1.23s"
        assertThat(report.getMetadata().getGenerationDuration()).matches("\\d+(\\.\\d+)?[ms]s?");
    }
    
    @Test
    @DisplayName("Should handle enrichment failure gracefully")
    void testEnrichmentFailureHandledGracefully() {
        // Given
        ReportType reportType = ReportType.DEVICE_STATUS;
        ReportFormat format = ReportFormat.CSV;
        ReportTimeRange timeRange = ReportTimeRange.of(
            Instant.now().minus(1, ChronoUnit.HOURS),
            Instant.now()
        );
        String title = "Status Report";
        
        List<DeviceDataPort.DeviceData> devices = List.of(
            new DeviceDataPort.DeviceData(
                UUID.randomUUID(),
                "device-1",
                "192.168.1.100",
                null,
                "SERVER",
                false,
                false,
                null
            )
        );
        
        byte[] csvContent = "CSV content".getBytes();
        ReportContent content = ReportContent.of(csvContent);
        
        when(deviceDataPort.getAllDevices()).thenReturn(devices);
        when(pingDataPort.getAllPingTargets()).thenThrow(new RuntimeException("Failed to get ping targets"));
        when(reportGeneratorPort.supports(format)).thenReturn(true);
        when(reportGeneratorPort.generateReport(any(Report.class), any(ReportGeneratorPort.ReportData.class)))
            .thenReturn(content);
        when(fileStoragePort.storeReportFile(any(UUID.class), anyString(), eq(content)))
            .thenReturn("https://storage.example.com/reports/status.csv");
        
        // When
        Report report = service.generateReport(reportType, format, timeRange, null, title);
        
        // Then - should still generate report even if enrichment fails
        assertThat(report).isNotNull();
        verify(logger).logBusinessWarning(eq("Failed to enrich devices with monitoring status"), any(Map.class));
    }
}