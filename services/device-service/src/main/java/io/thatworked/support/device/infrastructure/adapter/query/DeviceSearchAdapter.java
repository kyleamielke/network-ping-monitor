package io.thatworked.support.device.infrastructure.adapter.query;

import io.thatworked.support.device.api.dto.request.DeviceSearchCriteria;
import io.thatworked.support.device.api.dto.request.DeviceFilter;
import io.thatworked.support.device.infrastructure.persistence.specification.DeviceSpecifications;
import io.thatworked.support.device.domain.model.DeviceDomain;
import io.thatworked.support.device.domain.model.DeviceSearchResultDomain;
import io.thatworked.support.device.domain.model.PageInfo;
import io.thatworked.support.device.domain.model.PagedResult;
import io.thatworked.support.device.domain.port.DeviceSearchPort;
import io.thatworked.support.device.infrastructure.entity.Device;
import io.thatworked.support.device.infrastructure.repository.DeviceRepository;
import io.thatworked.support.device.api.mapper.DeviceMapper;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Infrastructure adapter for device search operations.
 * Implements domain search port using JPA repositories.
 */
@Component
@Transactional(readOnly = true)
public class DeviceSearchAdapter implements DeviceSearchPort {
    
    private final StructuredLogger logger;
    private final DeviceRepository deviceRepository;
    private final DeviceMapper deviceMapper;
    
    public DeviceSearchAdapter(StructuredLoggerFactory loggerFactory, DeviceRepository deviceRepository, DeviceMapper deviceMapper) {
        this.logger = loggerFactory.getLogger(DeviceSearchAdapter.class);
        this.deviceRepository = deviceRepository;
        this.deviceMapper = deviceMapper;
    }
    
    @Override
    public DeviceSearchResultDomain search(String name, String ipAddress, String deviceType, 
                                          int page, int size) {
        logger.with("operation", "search")
              .with("name", name)
              .with("ipAddress", ipAddress)
              .with("deviceType", deviceType)
              .with("page", page)
              .with("size", size)
              .debug("Executing device search");
        
        // Build criteria from primitives
        DeviceSearchCriteria criteria = DeviceSearchCriteria.builder()
            .name(name)
            .ipAddress(ipAddress)
            .deviceType(deviceType)
            .page(page)
            .size(size)
            .build();
        
        PageRequest pageRequest = PageRequest.of(page, size);
        
        // Perform search using repository
        Page<Device> devicePage = deviceRepository.searchDevices(criteria, pageRequest);
        
        // Map to domain result
        return DeviceSearchResultDomain.builder()
            .devices(devicePage.getContent().stream()
                .map(deviceMapper::toDomain)
                .collect(Collectors.toList()))
            .currentPage(devicePage.getNumber())
            .pageSize(devicePage.getSize())
            .totalElements(devicePage.getTotalElements())
            .totalPages(devicePage.getTotalPages())
            .hasNext(devicePage.hasNext())
            .hasPrevious(devicePage.hasPrevious())
            .build();
    }
    
    @Override
    public PagedResult<DeviceDomain> findWithFilters(Map<String, Object> filters, PageInfo pageInfo) {
        logger.with("operation", "findWithFilters")
              .with("filters", filters)
              .with("page", pageInfo.getPageNumber())
              .with("size", pageInfo.getPageSize())
              .debug("Finding devices with filters");
        
        // Convert map to DeviceFilter
        DeviceFilter filter = buildFilterFromMap(filters);
        
        Specification<Device> spec = DeviceSpecifications.withFilter(filter);
        PageRequest pageRequest = PageRequest.of(pageInfo.getPageNumber(), pageInfo.getPageSize());
        Page<Device> page = deviceRepository.findAll(spec, pageRequest);
        
        List<DeviceDomain> content = page.getContent().stream()
            .map(deviceMapper::toDomain)
            .collect(Collectors.toList());
            
        return PagedResult.of(content, pageInfo, page.getTotalElements());
    }
    
    @Override
    public PagedResult<DeviceDomain> findAll(PageInfo pageInfo) {
        logger.with("operation", "findAllPaged")
              .with("page", pageInfo.getPageNumber())
              .with("size", pageInfo.getPageSize())
              .debug("Finding all devices with pagination");
        
        PageRequest pageRequest = PageRequest.of(pageInfo.getPageNumber(), pageInfo.getPageSize());
        Page<Device> page = deviceRepository.findAll(pageRequest);
        
        List<DeviceDomain> content = page.getContent().stream()
            .map(deviceMapper::toDomain)
            .collect(Collectors.toList());
            
        return PagedResult.of(content, pageInfo, page.getTotalElements());
    }
    
    private DeviceFilter buildFilterFromMap(Map<String, Object> filters) {
        DeviceFilter filter = new DeviceFilter();
        
        if (filters.containsKey("name")) {
            filter.setName((String) filters.get("name"));
        }
        if (filters.containsKey("ipAddress")) {
            filter.setIpAddress((String) filters.get("ipAddress"));
        }
        if (filters.containsKey("deviceType")) {
            filter.setDeviceType((String) filters.get("deviceType"));
        }
        if (filters.containsKey("siteId")) {
            Object siteIdObj = filters.get("siteId");
            if (siteIdObj instanceof UUID) {
                filter.setSiteId((UUID) siteIdObj);
            } else if (siteIdObj instanceof String) {
                try {
                    filter.setSiteId(UUID.fromString((String) siteIdObj));
                } catch (IllegalArgumentException e) {
                    logger.with("siteId", siteIdObj)
                          .with("operation", "parseUUID")
                          .with("field", "siteId")
                          .warn("Invalid UUID format for siteId");
                }
            }
        }
        
        return filter;
    }
}