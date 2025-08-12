package io.thatworked.support.device.infrastructure.repository;

import io.thatworked.support.device.infrastructure.entity.Device;
import io.thatworked.support.device.api.dto.request.DeviceSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeviceSearchRepository {
    Page<Device> searchDevices(DeviceSearchCriteria criteria, Pageable pageable);
}