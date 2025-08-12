package io.thatworked.support.device.application.service;

import io.thatworked.support.device.domain.model.DeviceDomain;
import io.thatworked.support.device.domain.model.DeviceSearchResultDomain;
import io.thatworked.support.device.domain.model.PageInfo;
import io.thatworked.support.device.domain.model.PagedResult;
import io.thatworked.support.device.domain.port.DeviceQueryPort;
import io.thatworked.support.device.domain.port.DeviceSearchPort;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Application service for device queries.
 * Orchestrates query operations using domain ports.
 * No infrastructure dependencies - only domain interfaces.
 */
@Service
public class DeviceQueryApplicationService {
    
    private final StructuredLogger logger;
    private final DeviceQueryPort deviceQueryPort;
    private final DeviceSearchPort deviceSearchPort;
    
    public DeviceQueryApplicationService(StructuredLoggerFactory loggerFactory, DeviceQueryPort deviceQueryPort, DeviceSearchPort deviceSearchPort) {
        this.logger = loggerFactory.getLogger(DeviceQueryApplicationService.class);
        this.deviceQueryPort = deviceQueryPort;
        this.deviceSearchPort = deviceSearchPort;
    }
    
    public Optional<DeviceDomain> findByUuid(UUID uuid) {
        logger.with("operation", "findByUuid")
              .with("deviceUuid", uuid)
              .debug("Finding device by UUID");
        return deviceQueryPort.findByUuid(uuid);
    }
    
    public List<DeviceDomain> findByUuids(List<UUID> uuids) {
        logger.with("operation", "findByUuids")
              .with("deviceCount", uuids.size())
              .debug("Finding devices by UUIDs batch");
        return deviceQueryPort.findByUuids(uuids);
    }
    
    public List<DeviceDomain> findAll() {
        logger.with("operation", "findAll")
              .debug("Finding all devices");
        return deviceQueryPort.findAll();
    }
    
    public PagedResult<DeviceDomain> findAll(int page, int size) {
        logger.with("operation", "findAllPaged")
              .with("page", page)
              .with("size", size)
              .debug("Finding all devices with pagination");
        
        PageInfo pageInfo = PageInfo.of(page, size);
        return deviceSearchPort.findAll(pageInfo);
    }
    
    public PagedResult<DeviceDomain> findWithFilters(Map<String, Object> filters, int page, int size) {
        logger.with("operation", "findWithFilters")
              .with("filters", filters)
              .with("page", page)
              .with("size", size)
              .debug("Finding devices with filters");
        
        PageInfo pageInfo = PageInfo.of(page, size);
        return deviceSearchPort.findWithFilters(filters, pageInfo);
    }
    
    public List<DeviceDomain> findByType(String deviceType) {
        logger.with("operation", "findByType")
              .with("deviceType", deviceType)
              .debug("Finding devices by type");
        return deviceQueryPort.findByType(deviceType);
    }
    
    public List<DeviceDomain> findBySite(UUID siteId) {
        logger.with("operation", "findBySite")
              .with("siteId", siteId)
              .debug("Finding devices by site");
        return deviceQueryPort.findBySite(siteId);
    }
    
    public List<DeviceDomain> findBySiteAndType(UUID siteId, String deviceType) {
        logger.with("operation", "findBySiteAndType")
              .with("siteId", siteId)
              .with("deviceType", deviceType)
              .debug("Finding devices by site and type");
        return deviceQueryPort.findBySiteAndType(siteId, deviceType);
    }
    
    public List<DeviceDomain> findUnassigned() {
        logger.with("operation", "findUnassigned")
              .debug("Finding unassigned devices");
        return deviceQueryPort.findUnassigned();
    }
    
    public DeviceSearchResultDomain search(String name, String ipAddress, String deviceType, 
                                          int page, int size) {
        logger.with("operation", "search")
              .with("name", name)
              .with("ipAddress", ipAddress)
              .with("deviceType", deviceType)
              .with("page", page)
              .with("size", size)
              .debug("Searching devices");
        
        // Validate and cap page size
        int validatedSize = Math.min(size, 100);
        
        return deviceSearchPort.search(name, ipAddress, deviceType, page, validatedSize);
    }
    
    public long count() {
        return deviceQueryPort.count();
    }
    
    public long countByType(String deviceType) {
        return deviceQueryPort.countByType(deviceType);
    }
    
    public long countBySite(UUID siteId) {
        return deviceQueryPort.countBySite(siteId);
    }
    
    public boolean existsByUuid(UUID uuid) {
        return deviceQueryPort.existsByUuid(uuid);
    }
    
    public boolean existsByIpAddress(String ipAddress) {
        return deviceQueryPort.existsByIpAddress(ipAddress);
    }
}