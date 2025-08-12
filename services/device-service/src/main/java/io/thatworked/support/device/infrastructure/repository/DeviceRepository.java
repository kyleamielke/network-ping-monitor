package io.thatworked.support.device.infrastructure.repository;

import io.thatworked.support.device.infrastructure.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeviceRepository extends JpaRepository<Device, UUID>, JpaSpecificationExecutor<Device>, DeviceSearchRepository {
    Optional<Device> findByIpAddress(String ipAddress);
    boolean existsByIpAddress(String ipAddress);
    
    Optional<Device> findByMacAddress(String macAddress);
    boolean existsByMacAddress(String macAddress);

    // Find devices by site UUID  
    @Query("SELECT d FROM Device d WHERE d.site = :siteId")
    List<Device> findBySiteId(@Param("siteId") UUID siteId);
    
    @Query("SELECT COUNT(d) FROM Device d WHERE d.site = :siteId")
    long countBySiteId(@Param("siteId") UUID siteId);

    // Find devices by site UUID and device type
    @Query("SELECT d FROM Device d WHERE d.site = :siteId AND d.type = :type")
    List<Device> findBySiteIdAndDeviceType(@Param("siteId") UUID siteId, @Param("type") String type);

    // Find devices by type
    List<Device> findByType(String type);
    long countByType(String type);
    
    // Legacy method for compatibility
    default List<Device> findByDeviceType(String deviceType) {
        return findByType(deviceType);
    }

    // Find devices with no site association
    @Query("SELECT d FROM Device d WHERE d.site IS NULL")
    List<Device> findBySiteIdIsNull();

    // Legacy method for compatibility with tests
    default Optional<Device> findByUuid(UUID uuid) {
        return findById(uuid);
    }
    
    // Legacy methods for compatibility
    default List<Device> findBySite(UUID site) {
        return findBySiteId(site);
    }
    
    default List<Device> findBySiteAndDeviceType(UUID site, String type) {
        return findBySiteIdAndDeviceType(site, type);
    }
    
    @Query("SELECT d FROM Device d WHERE d.site IS NULL")
    default List<Device> findUnassignedDevices() {
        return findBySiteIdIsNull();
    }
}