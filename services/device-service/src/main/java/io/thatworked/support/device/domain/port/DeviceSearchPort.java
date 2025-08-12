package io.thatworked.support.device.domain.port;

import io.thatworked.support.device.domain.model.DeviceDomain;
import io.thatworked.support.device.domain.model.DeviceSearchResultDomain;
import io.thatworked.support.device.domain.model.PageInfo;
import io.thatworked.support.device.domain.model.PagedResult;

import java.util.Map;

/**
 * Domain port for advanced device search operations.
 * Defines complex query operations available in the domain layer.
 */
public interface DeviceSearchPort {
    
    /**
     * Search devices based on criteria.
     * 
     * @param name Device name pattern (partial match)
     * @param ipAddress IP address pattern
     * @param deviceType Device type filter
     * @param page Page number (0-based)
     * @param size Page size
     * @return Search results with pagination
     */
    DeviceSearchResultDomain search(String name, String ipAddress, String deviceType, 
                                   int page, int size);
    
    /**
     * Find devices with filtering and pagination.
     * 
     * @param filters Map of field names to filter values
     * @param pageInfo Pagination information
     * @return Paged result of devices
     */
    PagedResult<DeviceDomain> findWithFilters(Map<String, Object> filters, PageInfo pageInfo);
    
    /**
     * Find all devices with pagination.
     * 
     * @param pageInfo Pagination information
     * @return Paged result of devices
     */
    PagedResult<DeviceDomain> findAll(PageInfo pageInfo);
}