package io.thatworked.support.device.api.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceSearchCriteria {
    // UUID and ID searches
    private UUID uuid;
    private String endpointId;
    private String assetTag;
    
    // Basic device info
    private String name;
    private String ipAddress;
    private String macAddress;
    
    // Device specifications
    private String deviceType;
    private String os;
    private String osType;
    private String make;
    private String model;
    
    // Site assignment
    private UUID site;
    private Boolean isAssigned; // true = has site, false = no site, null = all
    
    // Date range filtering
    private LocalDateTime createdAfter;
    private LocalDateTime createdBefore;
    
    // Pagination
    @Builder.Default
    private Integer page = 0;
    
    @Builder.Default
    private Integer size = 20;
    
    // Sorting
    @Builder.Default
    private String sortBy = "name";
    
    @Builder.Default
    private String sortDirection = "ASC";
}