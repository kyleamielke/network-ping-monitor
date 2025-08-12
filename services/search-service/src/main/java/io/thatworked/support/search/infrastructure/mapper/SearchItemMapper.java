package io.thatworked.support.search.infrastructure.mapper;

import io.thatworked.support.search.domain.model.SearchItem;
import io.thatworked.support.search.domain.model.SearchQuery;
import io.thatworked.support.search.domain.model.SearchType;
import io.thatworked.support.search.infrastructure.dto.DeviceDTO;
import io.thatworked.support.search.infrastructure.dto.DeviceSearchCriteria;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mapper for converting between domain models and infrastructure DTOs.
 */
@Component
public class SearchItemMapper {
    
    /**
     * Converts a DeviceDTO to a domain SearchItem.
     */
    public SearchItem fromDeviceDTO(DeviceDTO device) {
        if (device == null) {
            return null;
        }
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("ipAddress", device.getIpAddress());
        metadata.put("deviceType", device.getDeviceType());
        metadata.put("os", device.getOs());
        metadata.put("location", device.getSite());
        metadata.put("assetTag", device.getAssetTag());
        metadata.put("endpointId", device.getEndpointId());
        metadata.put("macAddress", device.getMacAddress());
        
        String summary = String.format("%s • %s", 
            device.getIpAddress(), 
            device.getDeviceType() != null ? device.getDeviceType() : "Device"
        );
        
        String description = buildDeviceDescription(device);
        
        return SearchItem.builder()
            .id(device.getId())
            .type(SearchType.DEVICE)
            .title(device.getName())
            .summary(summary)
            .description(description)
            .metadata(metadata)
            .lastUpdated(device.getUpdatedAt() != null ? 
                device.getUpdatedAt() : Instant.now())
            .relevanceScore(1.0) // Base score, can be enhanced by domain service
            .build();
    }
    
    /**
     * Converts a SearchQuery to DeviceSearchCriteria.
     */
    public DeviceSearchCriteria toDeviceSearchCriteria(SearchQuery query) {
        DeviceSearchCriteria criteria = new DeviceSearchCriteria();
        criteria.setSize(query.getLimit());
        criteria.setPage(0);
        criteria.setSortBy("name");
        criteria.setSortDirection("ASC");
        
        // Detect query type and search appropriate field
        if (query.isIPAddressPattern()) {
            criteria.setIpAddress(query.getQuery());
        } else if (query.isMACAddressPattern()) {
            criteria.setMacAddress(query.getQuery());
        } else if (query.isAssetTagPattern()) {
            criteria.setAssetTag(query.getQuery());
        } else {
            // Default to name search
            criteria.setName(query.getQuery());
        }
        
        return criteria;
    }
    
    private String buildDeviceDescription(DeviceDTO device) {
        List<String> parts = new ArrayList<>();
        
        if (device.getOs() != null) {
            parts.add(device.getOs());
        }
        if (device.getSite() != null) {
            parts.add("Location: " + device.getSite());
        }
        if (device.getAssetTag() != null) {
            parts.add("Asset: " + device.getAssetTag());
        }
        if (device.getMacAddress() != null) {
            parts.add("MAC: " + device.getMacAddress());
        }
        
        return String.join(" • ", parts);
    }
}