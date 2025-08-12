package io.thatworked.support.report.infrastructure.mapper;

import io.thatworked.support.report.domain.model.*;
import io.thatworked.support.report.infrastructure.entity.ReportEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Mapper for converting between Report domain model and ReportEntity.
 */
@Component
public class ReportEntityMapper {
    
    private final ObjectMapper objectMapper;
    
    public ReportEntityMapper() {
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Converts domain model to entity.
     */
    public ReportEntity toEntity(Report report) {
        ReportEntity entity = new ReportEntity();
        
        entity.setId(report.getId());
        entity.setFilename(report.generateFilename());
        entity.setTitle(report.getTitle());
        entity.setReportType(mapReportType(report.getReportType()));
        entity.setFormat(mapReportFormat(report.getFormat()));
        entity.setStatus(ReportEntity.ReportStatus.COMPLETED); // Default to completed
        
        if (report.getMetadata() != null) {
            entity.setFilePath(report.getMetadata().getFilePath());
            entity.setFileSizeBytes(report.getMetadata().getFileSizeBytes());
            entity.setDownloadUrl(report.getMetadata().getDownloadUrl());
            entity.setErrorMessage(report.getMetadata().getError());
        }
        
        if (report.getTimeRange() != null) {
            entity.setTimeRangeStart(report.getTimeRange().getStartDate());
            entity.setTimeRangeEnd(report.getTimeRange().getEndDate());
        }
        
        if (report.getParameters() != null) {
            try {
                entity.setParameters(objectMapper.writeValueAsString(report.getParameters()));
            } catch (Exception e) {
                entity.setParameters("{}");
            }
        }
        
        entity.setGeneratedAt(report.getGeneratedAt());
        // createdAt and updatedAt are managed by @CreationTimestamp/@UpdateTimestamp
        
        return entity;
    }
    
    /**
     * Converts entity to domain model.
     */
    public Report toDomain(ReportEntity entity) {
        UUID reportId = entity.getId();
        ReportType reportType = mapReportTypeToDomain(entity.getReportType());
        ReportFormat format = mapReportFormatToDomain(entity.getFormat());
        
        ReportTimeRange timeRange = null;
        if (entity.getTimeRangeStart() != null && entity.getTimeRangeEnd() != null) {
            timeRange = ReportTimeRange.of(entity.getTimeRangeStart(), entity.getTimeRangeEnd());
        }
        
        List<UUID> deviceIds = List.of();
        if (entity.getParameters() != null) {
            try {
                Map<String, Object> parameters = objectMapper.readValue(entity.getParameters(), 
                    new TypeReference<Map<String, Object>>() {});
                
                // Extract device IDs if present
                if (parameters.containsKey("deviceIds")) {
                    List<String> ids = (List<String>) parameters.get("deviceIds");
                    deviceIds = ids.stream()
                        .map(UUID::fromString)
                        .collect(Collectors.toList());
                }
            } catch (Exception e) {
                // Ignore parsing errors
            }
        }
        
        // Create metadata from entity fields
        ReportMetadata metadata = ReportMetadata.empty();
        if (entity.getFileSizeBytes() != null) {
            metadata = metadata.withSize(entity.getFileSizeBytes());
        }
        if (entity.getDownloadUrl() != null) {
            metadata = metadata.withDownloadUrl(entity.getDownloadUrl());
        }
        
        // Create report with minimal constructor available
        Report report = new Report(
            reportId,
            reportType,
            format,
            timeRange,
            deviceIds,
            entity.getTitle(),
            entity.getGeneratedAt(),
            null, // Content will be null for loaded reports
            metadata
        );
        
        return report;
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