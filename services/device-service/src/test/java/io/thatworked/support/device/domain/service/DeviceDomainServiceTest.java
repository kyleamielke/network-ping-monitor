package io.thatworked.support.device.domain.service;

import io.thatworked.support.device.domain.exception.DeviceNotFoundDomainException;
import io.thatworked.support.device.domain.exception.DuplicateDeviceDomainException;
import io.thatworked.support.device.domain.exception.InvalidDeviceStateDomainException;
import io.thatworked.support.device.domain.exception.OptimisticLockingDomainException;
import io.thatworked.support.device.domain.model.DeviceDomain;
import io.thatworked.support.device.domain.model.DeviceStatus;
import io.thatworked.support.device.domain.port.DeviceRepository;
import io.thatworked.support.device.domain.port.DomainLogger;
import io.thatworked.support.device.domain.port.EventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeviceDomainService Tests")
class DeviceDomainServiceTest {

    @Mock
    private DeviceRepository repository;
    
    @Mock
    private EventPublisher eventPublisher;
    
    @Mock
    private DomainLogger domainLogger;
    
    private DeviceDomainService service;
    
    @BeforeEach
    void setUp() {
        service = new DeviceDomainService(repository, eventPublisher, domainLogger);
    }
    
    // Helper method to create a DeviceDomain with ID for testing
    private DeviceDomain createDeviceWithId(UUID id, String name, String ipAddress) {
        return new DeviceDomain(
            id, name, ipAddress, null, null,              // id, name, ipAddress, hostname, macAddress
            null, null, null, null,                        // os, osType, make, model
            null, null, null,                              // type, endpointId, assetTag
            null, null, DeviceStatus.ACTIVE,               // description, location, status
            null, null,                                    // metadata, siteId
            null, 1L, Instant.now(), Instant.now()        // roles, version, createdAt, updatedAt
        );
    }
    
    @Test
    @DisplayName("Should create device successfully with all fields")
    void testCreateDeviceSuccess() {
        // Given
        String name = "test-server";
        String ipAddress = "192.168.1.100";
        String hostname = "server01.example.com";
        String macAddress = "00:11:22:33:44:55";
        
        when(repository.existsByIpAddress(ipAddress)).thenReturn(false);
        when(repository.existsByMacAddress(macAddress)).thenReturn(false);
        
        DeviceDomain savedDevice = new DeviceDomain(name, ipAddress);
        savedDevice.updateDetails(name, ipAddress, hostname, macAddress);
        when(repository.save(any(DeviceDomain.class))).thenReturn(savedDevice);
        
        // When
        DeviceDomain result = service.createDevice(name, ipAddress, hostname, macAddress);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getIpAddress()).isEqualTo(ipAddress);
        assertThat(result.getHostname()).isEqualTo(hostname);
        assertThat(result.getMacAddress()).isEqualTo(macAddress);
        
        verify(repository).existsByIpAddress(ipAddress);
        verify(repository).existsByMacAddress(macAddress);
        verify(repository).save(any(DeviceDomain.class));
        verify(eventPublisher).publishDeviceCreated(savedDevice);
        verify(domainLogger, times(2)).logBusinessEvent(anyString(), any(Map.class));
    }
    
    @Test
    @DisplayName("Should throw exception when creating device with duplicate IP address")
    void testCreateDeviceWithDuplicateIpAddress() {
        // Given
        String name = "test-server";
        String ipAddress = "192.168.1.100";
        
        when(repository.existsByIpAddress(ipAddress)).thenReturn(true);
        
        // When & Then
        assertThatThrownBy(() -> service.createDevice(name, ipAddress, null, null))
            .isInstanceOf(DuplicateDeviceDomainException.class)
            .hasMessageContaining("ipAddress")
            .hasMessageContaining(ipAddress);
        
        verify(repository).existsByIpAddress(ipAddress);
        verify(repository, never()).save(any());
        verify(eventPublisher, never()).publishDeviceCreated(any());
    }
    
    @Test
    @DisplayName("Should throw exception when creating device with duplicate MAC address")
    void testCreateDeviceWithDuplicateMacAddress() {
        // Given
        String name = "test-server";
        String ipAddress = "192.168.1.100";
        String macAddress = "00:11:22:33:44:55";
        
        when(repository.existsByIpAddress(ipAddress)).thenReturn(false);
        when(repository.existsByMacAddress(macAddress)).thenReturn(true);
        
        // When & Then
        assertThatThrownBy(() -> service.createDevice(name, ipAddress, null, macAddress))
            .isInstanceOf(DuplicateDeviceDomainException.class)
            .hasMessageContaining("macAddress")
            .hasMessageContaining(macAddress);
        
        verify(repository).existsByMacAddress(macAddress);
        verify(repository, never()).save(any());
        verify(eventPublisher, never()).publishDeviceCreated(any());
    }
    
    @Test
    @DisplayName("Should update device successfully with partial fields")
    void testUpdateDevicePartialUpdate() {
        // Given
        UUID deviceId = UUID.randomUUID();
        DeviceDomain existingDevice = new DeviceDomain(
            deviceId, "old-name", "192.168.1.100", null, null,
            "Windows", "WINDOWS", "Dell", "PowerEdge R750",
            null, null, null,
            null, null, DeviceStatus.ACTIVE,
            null, null,
            null, 1L, Instant.now(), Instant.now()
        );
        
        when(repository.findById(deviceId)).thenReturn(Optional.of(existingDevice));
        when(repository.save(any(DeviceDomain.class))).thenAnswer(i -> i.getArgument(0));
        
        String newName = "new-name";
        String newOs = "Linux";
        
        // When
        DeviceDomain result = service.updateDevice(deviceId, newName, null, null, null, 
            null, null, null, newOs, null, null, null, null, null, null);
        
        // Then
        assertThat(result.getName()).isEqualTo(newName);
        assertThat(result.getIpAddress()).isEqualTo("192.168.1.100"); // unchanged
        assertThat(result.getOs()).isEqualTo(newOs);
        // Note: Make is preserved because updateSystemInfo only updates if new value is provided
        
        verify(repository).findById(deviceId);
        verify(repository).save(existingDevice);
        verify(eventPublisher).publishDeviceUpdated(eq(existingDevice), any(Map.class));
        
        ArgumentCaptor<Map<String, Object>> changesCaptor = ArgumentCaptor.forClass(Map.class);
        verify(eventPublisher).publishDeviceUpdated(any(), changesCaptor.capture());
        Map<String, Object> changes = changesCaptor.getValue();
        assertThat(changes).containsKeys("name", "os");
    }
    
    @Test
    @DisplayName("Should handle optimistic locking correctly")
    void testUpdateDeviceOptimisticLocking() {
        // Given
        UUID deviceId = UUID.randomUUID();
        DeviceDomain existingDevice = new DeviceDomain(
            deviceId, "test-device", "192.168.1.100", null, null,
            null, null, null, null,
            null, null, null,
            null, null, DeviceStatus.ACTIVE,
            null, null,
            null, 5L, Instant.now(), Instant.now()
        );
        
        when(repository.findById(deviceId)).thenReturn(Optional.of(existingDevice));
        
        Long expectedVersion = 3L; // Wrong version
        
        // When & Then
        assertThatThrownBy(() -> service.updateDevice(deviceId, "new-name", null, null, 
            null, null, null, null, null, null, null, null, null, null, null, expectedVersion))
            .isInstanceOf(OptimisticLockingDomainException.class)
            .hasMessageContaining("Device")
            .hasMessageContaining(deviceId.toString());
        
        verify(repository).findById(deviceId);
        verify(repository, never()).save(any());
        verify(eventPublisher, never()).publishDeviceUpdated(any(), any());
    }
    
    @Test
    @DisplayName("Should throw exception when updating non-existent device")
    void testUpdateDeviceNotFound() {
        // Given
        UUID deviceId = UUID.randomUUID();
        when(repository.findById(deviceId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> service.updateDevice(deviceId, "new-name", null, null, 
            null, null, null, null, null, null, null, null, null, null, null))
            .isInstanceOf(DeviceNotFoundDomainException.class)
            .hasMessageContaining(deviceId.toString());
        
        verify(repository).findById(deviceId);
        verify(repository, never()).save(any());
    }
    
    @Test
    @DisplayName("Should prevent duplicate IP address during update")
    void testUpdateDevicePreventsDuplicateIp() {
        // Given
        UUID deviceId = UUID.randomUUID();
        UUID otherDeviceId = UUID.randomUUID();
        String newIpAddress = "192.168.1.200";
        
        DeviceDomain existingDevice = createDeviceWithId(deviceId, "test-device", "192.168.1.100");
        DeviceDomain otherDevice = createDeviceWithId(otherDeviceId, "other-device", newIpAddress);
        
        when(repository.findById(deviceId)).thenReturn(Optional.of(existingDevice));
        when(repository.findByIpAddress(newIpAddress)).thenReturn(Optional.of(otherDevice));
        
        // When & Then
        assertThatThrownBy(() -> service.updateDevice(deviceId, null, newIpAddress, null, 
            null, null, null, null, null, null, null, null, null, null, null))
            .isInstanceOf(DuplicateDeviceDomainException.class)
            .hasMessageContaining("ipAddress")
            .hasMessageContaining(newIpAddress);
        
        verify(repository).findById(deviceId);
        verify(repository).findByIpAddress(newIpAddress);
        verify(repository, never()).save(any());
    }
    
    @Test
    @DisplayName("Should delete device successfully")
    void testDeleteDeviceSuccess() {
        // Given
        UUID deviceId = UUID.randomUUID();
        DeviceDomain device = createDeviceWithId(deviceId, "test-device", "192.168.1.100");
        
        when(repository.findById(deviceId)).thenReturn(Optional.of(device));
        
        // When
        service.deleteDevice(deviceId);
        
        // Then
        verify(repository).findById(deviceId);
        verify(repository).deleteById(deviceId);
        verify(eventPublisher).publishDeviceDeleted(device);
        verify(domainLogger, times(2)).logBusinessEvent(anyString(), any(Map.class));
    }
    
    @Test
    @DisplayName("Should throw exception when deleting non-existent device")
    void testDeleteDeviceNotFound() {
        // Given
        UUID deviceId = UUID.randomUUID();
        when(repository.findById(deviceId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> service.deleteDevice(deviceId))
            .isInstanceOf(DeviceNotFoundDomainException.class)
            .hasMessageContaining(deviceId.toString());
        
        verify(repository).findById(deviceId);
        verify(repository, never()).deleteById(any());
        verify(eventPublisher, never()).publishDeviceDeleted(any());
    }
    
    @Test
    @DisplayName("Should activate device successfully")
    void testActivateDevice() {
        // Given
        UUID deviceId = UUID.randomUUID();
        DeviceDomain device = createDeviceWithId(deviceId, "test-device", "192.168.1.100");
        device.deactivate(); // Start with inactive device
        
        when(repository.findById(deviceId)).thenReturn(Optional.of(device));
        when(repository.save(device)).thenReturn(device);
        
        // When
        DeviceDomain result = service.activateDevice(deviceId);
        
        // Then
        assertThat(result.getStatus()).isEqualTo(DeviceStatus.ACTIVE);
        verify(repository).save(device);
        verify(eventPublisher).publishDeviceActivated(device);
        verify(domainLogger).logDomainStateChange(eq("Device"), eq(deviceId.toString()), 
            eq("INACTIVE"), eq("ACTIVE"), any(Map.class));
    }
    
    @Test
    @DisplayName("Should deactivate device successfully")
    void testDeactivateDevice() {
        // Given
        UUID deviceId = UUID.randomUUID();
        DeviceDomain device = createDeviceWithId(deviceId, "test-device", "192.168.1.100");
        // Device starts as ACTIVE by default
        
        when(repository.findById(deviceId)).thenReturn(Optional.of(device));
        when(repository.save(device)).thenReturn(device);
        
        // When
        DeviceDomain result = service.deactivateDevice(deviceId);
        
        // Then
        assertThat(result.getStatus()).isEqualTo(DeviceStatus.INACTIVE);
        verify(repository).save(device);
        verify(eventPublisher).publishDeviceDeactivated(device);
        verify(domainLogger).logDomainStateChange(eq("Device"), eq(deviceId.toString()), 
            eq("ACTIVE"), eq("INACTIVE"), any(Map.class));
    }
    
    @Test
    @DisplayName("Should assign device to site successfully")
    void testAssignToSite() {
        // Given
        UUID deviceId = UUID.randomUUID();
        UUID siteId = UUID.randomUUID();
        DeviceDomain device = createDeviceWithId(deviceId, "test-device", "192.168.1.100");
        
        when(repository.findById(deviceId)).thenReturn(Optional.of(device));
        when(repository.save(device)).thenReturn(device);
        
        // When
        DeviceDomain result = service.assignToSite(deviceId, siteId);
        
        // Then
        assertThat(result.getSiteId()).isEqualTo(siteId);
        assertThat(result.isAssignedToSite()).isTrue();
        verify(repository).save(device);
        verify(eventPublisher).publishDeviceAssignedToSite(device, siteId);
    }
    
    @Test
    @DisplayName("Should remove device from site successfully")
    void testRemoveFromSite() {
        // Given
        UUID deviceId = UUID.randomUUID();
        UUID siteId = UUID.randomUUID();
        DeviceDomain device = createDeviceWithId(deviceId, "test-device", "192.168.1.100");
        device.assignToSite(siteId);
        
        when(repository.findById(deviceId)).thenReturn(Optional.of(device));
        when(repository.save(device)).thenReturn(device);
        
        // When
        DeviceDomain result = service.removeFromSite(deviceId);
        
        // Then
        assertThat(result.getSiteId()).isNull();
        assertThat(result.isAssignedToSite()).isFalse();
        verify(repository).save(device);
        verify(eventPublisher).publishDeviceRemovedFromSite(device, siteId);
    }
    
    @Test
    @DisplayName("Should throw exception when removing device not assigned to site")
    void testRemoveFromSiteNotAssigned() {
        // Given
        UUID deviceId = UUID.randomUUID();
        DeviceDomain device = createDeviceWithId(deviceId, "test-device", "192.168.1.100");
        // Device not assigned to any site
        
        when(repository.findById(deviceId)).thenReturn(Optional.of(device));
        
        // When & Then
        assertThatThrownBy(() -> service.removeFromSite(deviceId))
            .isInstanceOf(InvalidDeviceStateDomainException.class)
            .hasMessageContaining("not assigned to any site");
        
        verify(repository, never()).save(any());
        verify(eventPublisher, never()).publishDeviceRemovedFromSite(any(), any());
    }
    
    @Test
    @DisplayName("Should assign roles to device successfully")
    void testAssignRoles() {
        // Given
        UUID deviceId = UUID.randomUUID();
        DeviceDomain device = createDeviceWithId(deviceId, "test-device", "192.168.1.100");
        device.updateAdditionalInfo("SERVER", null, null);
        
        List<String> roleNames = Arrays.asList("WebServer", "DatabaseServer");
        
        when(repository.findById(deviceId)).thenReturn(Optional.of(device));
        when(repository.save(device)).thenReturn(device);
        
        // When
        DeviceDomain result = service.assignRoles(deviceId, roleNames);
        
        // Then
        assertThat(result.getRoles()).hasSize(2);
        assertThat(result.hasRole("WebServer")).isTrue();
        assertThat(result.hasRole("DatabaseServer")).isTrue();
        verify(repository).save(device);
        verify(eventPublisher).publishDeviceRolesUpdated(device, roleNames);
    }
    
    @Test
    @DisplayName("Should validate incompatible role assignments")
    void testAssignRolesWithIncompatibleRoles() {
        // Given
        UUID deviceId = UUID.randomUUID();
        DeviceDomain device = createDeviceWithId(deviceId, "test-device", "192.168.1.100");
        device.updateAdditionalInfo("ROUTER", null, null);
        
        List<String> roleNames = Arrays.asList("Server"); // Incompatible with ROUTER
        
        when(repository.findById(deviceId)).thenReturn(Optional.of(device));
        
        // When & Then
        assertThatThrownBy(() -> service.assignRoles(deviceId, roleNames))
            .isInstanceOf(InvalidDeviceStateDomainException.class)
            .hasMessageContaining("Cannot assign Server role to Router device type");
        
        verify(repository, never()).save(any());
        verify(eventPublisher, never()).publishDeviceRolesUpdated(any(), any());
    }
    
    @Test
    @DisplayName("Should validate conflicting roles")
    void testAssignConflictingRoles() {
        // Given
        UUID deviceId = UUID.randomUUID();
        DeviceDomain device = createDeviceWithId(deviceId, "test-device", "192.168.1.100");
        device.updateAdditionalInfo("OTHER", null, null);
        
        List<String> roleNames = Arrays.asList("Router", "Switch"); // Conflicting roles
        
        when(repository.findById(deviceId)).thenReturn(Optional.of(device));
        
        // When & Then
        assertThatThrownBy(() -> service.assignRoles(deviceId, roleNames))
            .isInstanceOf(InvalidDeviceStateDomainException.class)
            .hasMessageContaining("cannot have both Router and Switch roles");
        
        verify(repository, never()).save(any());
        verify(eventPublisher, never()).publishDeviceRolesUpdated(any(), any());
    }
    
    @Test
    @DisplayName("Should find devices by site")
    void testFindBySite() {
        // Given
        UUID siteId = UUID.randomUUID();
        List<DeviceDomain> devices = Arrays.asList(
            new DeviceDomain("device1", "192.168.1.1"),
            new DeviceDomain("device2", "192.168.1.2")
        );
        
        when(repository.findBySiteId(siteId)).thenReturn(devices);
        
        // When
        List<DeviceDomain> result = service.findBySite(siteId);
        
        // Then
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(devices);
        verify(repository).findBySiteId(siteId);
    }
    
    @Test
    @DisplayName("Should check if IP address is taken")
    void testIsIpAddressTaken() {
        // Given
        String ipAddress = "192.168.1.100";
        UUID deviceId = UUID.randomUUID();
        UUID otherDeviceId = UUID.randomUUID();
        
        DeviceDomain otherDevice = createDeviceWithId(otherDeviceId, "other-device", ipAddress);
        
        when(repository.findByIpAddress(ipAddress)).thenReturn(Optional.of(otherDevice));
        
        // When
        boolean result = service.isIpAddressTaken(ipAddress, deviceId);
        
        // Then
        assertThat(result).isTrue();
        verify(repository).findByIpAddress(ipAddress);
    }
    
    @Test
    @DisplayName("Should return false when IP address is not taken")
    void testIsIpAddressNotTaken() {
        // Given
        String ipAddress = "192.168.1.100";
        UUID deviceId = UUID.randomUUID();
        
        when(repository.findByIpAddress(ipAddress)).thenReturn(Optional.empty());
        
        // When
        boolean result = service.isIpAddressTaken(ipAddress, deviceId);
        
        // Then
        assertThat(result).isFalse();
        verify(repository).findByIpAddress(ipAddress);
    }
}