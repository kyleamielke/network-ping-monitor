package io.thatworked.support.report.infrastructure.mapper;

import io.thatworked.support.report.domain.model.*;
import io.thatworked.support.report.infrastructure.entity.ReportEntity;
import io.thatworked.support.report.infrastructure.entity.ScheduledReportEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Mapper for converting between ScheduledReport domain model and ScheduledReportEntity.
 */
@Component
public class ScheduledReportEntityMapper {
    
    private final ObjectMapper objectMapper;
    private final ReportEntityMapper reportEntityMapper;
    
    public ScheduledReportEntityMapper(ReportEntityMapper reportEntityMapper) {
        this.reportEntityMapper = reportEntityMapper;
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Converts domain model to entity.
     */
    public ScheduledReportEntity toEntity(ScheduledReport scheduledReport) {
        ScheduledReportEntity entity = new ScheduledReportEntity();
        
        entity.setId(scheduledReport.getId());
        entity.setTitle(scheduledReport.getTitle());
        entity.setReportType(mapReportType(scheduledReport.getReportType()));
        entity.setFormat(mapReportFormat(scheduledReport.getFormat()));
        entity.setSchedule(scheduledReport.getSchedule());
        
        if (scheduledReport.getDeviceIds() != null) {
            try {
                entity.setDeviceIds(objectMapper.writeValueAsString(scheduledReport.getDeviceIds()));
            } catch (Exception e) {
                entity.setDeviceIds("[]");
            }
        }
        
        entity.setActive(scheduledReport.isActive());
        entity.setLastRunTime(scheduledReport.getLastRunTime());
        entity.setNextRunTime(scheduledReport.getNextRunTime());
        entity.setLastRunStatus(null); // ScheduledReport doesn't have lastRunStatus
        // createdAt and updatedAt are managed by @CreationTimestamp/@UpdateTimestamp
        
        return entity;
    }
    
    /**
     * Converts entity to domain model.
     */
    public ScheduledReport toDomain(ScheduledReportEntity entity) {
        UUID id = entity.getId();
        ReportType reportType = mapReportTypeToDomain(entity.getReportType());
        ReportFormat format = mapReportFormatToDomain(entity.getFormat());
        
        List<String> deviceIds = List.of();
        if (entity.getDeviceIds() != null) {
            try {
                deviceIds = objectMapper.readValue(entity.getDeviceIds(), 
                    new TypeReference<List<String>>() {});
            } catch (Exception e) {
                // Ignore parsing errors
            }
        }
        
        // ScheduledReport constructor doesn't match - need to use static factory method
        // Since we can't directly reconstruct, we'll create a new one
        ScheduledReport scheduledReport = ScheduledReport.create(
            reportType,
            format,
            entity.getSchedule(),
            entity.getTitle(),
            deviceIds
        );
        
        // If it's inactive, deactivate it
        if (!entity.isActive()) {
            scheduledReport = scheduledReport.deactivate();
        }
        
        return scheduledReport;
    }
    
    // Type mappings
    private ReportEntity.ReportType mapReportType(ReportType domainType) {
        return switch (domainType) {
            case DEVICE_STATUS -> ReportEntity.ReportType.DEVICE_STATUS;
            case UPTIME_SUMMARY -> ReportEntity.ReportType.UPTIME_SUMMARY;
            case PING_PERFORMANCE -> ReportEntity.ReportType.PING_PERFORMANCE;
            case ALERT_HISTORY -> ReportEntity.ReportType.ALERT_HISTORY;
        };
    }
    
    private ReportType mapReportTypeToDomain(ReportEntity.ReportType entityType) {
        return switch (entityType) {
            case DEVICE_STATUS -> ReportType.DEVICE_STATUS;
            case UPTIME_SUMMARY -> ReportType.UPTIME_SUMMARY;
            case PING_PERFORMANCE -> ReportType.PING_PERFORMANCE;
            case ALERT_HISTORY -> ReportType.ALERT_HISTORY;
        };
    }
    
    private ReportEntity.ReportFormat mapReportFormat(ReportFormat domainFormat) {
        return switch (domainFormat) {
            case PDF -> ReportEntity.ReportFormat.PDF;
            case CSV -> ReportEntity.ReportFormat.CSV;
        };
    }
    
    private ReportFormat mapReportFormatToDomain(ReportEntity.ReportFormat entityFormat) {
        return switch (entityFormat) {
            case PDF -> ReportFormat.PDF;
            case CSV -> ReportFormat.CSV;
        };
    }
}