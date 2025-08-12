package io.thatworked.support.device.infrastructure.service;

import io.thatworked.support.device.config.properties.DeviceServiceProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service to check feature flags.
 */
@Service
@RequiredArgsConstructor
public class FeatureFlagService {
    
    private final DeviceServiceProperties properties;
    
    public boolean isAsyncEventsEnabled() {
        return properties.getFeatures().isAsyncEvents();
    }
    
    public boolean isValidateOnUpdateEnabled() {
        return properties.getFeatures().isValidateOnUpdate();
    }
    
    public boolean isDuplicateIpCheckEnabled() {
        return properties.getFeatures().isDuplicateIpCheck();
    }
    
    public boolean isSoftDeleteEnabled() {
        return properties.getFeatures().isSoftDelete();
    }
    
    public boolean isAuditLoggingEnabled() {
        return properties.getFeatures().isAuditLogging();
    }
    
    public boolean isImportExportEnabled() {
        return properties.getFeatures().isImportExport();
    }
    
    public boolean isBulkOperationsEnabled() {
        return properties.getFeatures().isBulkOperations();
    }
    
    public boolean isAdvancedSearchEnabled() {
        return properties.getFeatures().isAdvancedSearch();
    }
    
    public boolean isDeviceRelationshipsEnabled() {
        return properties.getFeatures().isDeviceRelationships();
    }
    
    public boolean isTaggingEnabled() {
        return properties.getFeatures().isTagging();
    }
    
    public boolean isRateLimitingEnabled() {
        return properties.getFeatures().isRateLimiting();
    }
    
    public boolean isCompressionEnabled() {
        return properties.getFeatures().isCompression();
    }
}