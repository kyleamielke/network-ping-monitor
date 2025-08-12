package io.thatworked.support.device.domain.port;

import io.thatworked.support.device.domain.model.DeviceDomain;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Domain port for publishing events.
 * Pure interface with no framework dependencies.
 * Infrastructure layer will provide the implementation.
 */
public interface EventPublisher {
    
    /**
     * Publish a device created event.
     * @param device the created device
     */
    void publishDeviceCreated(DeviceDomain device);
    
    /**
     * Publish a device updated event.
     * @param device the updated device
     * @param changes map of changed fields
     */
    void publishDeviceUpdated(DeviceDomain device, Map<String, Object> changes);
    
    /**
     * Publish a device deleted event.
     * @param device the device being deleted
     */
    void publishDeviceDeleted(DeviceDomain device);
    
    /**
     * Publish a device activated event.
     * @param device the activated device
     */
    void publishDeviceActivated(DeviceDomain device);
    
    /**
     * Publish a device deactivated event.
     * @param device the deactivated device
     */
    void publishDeviceDeactivated(DeviceDomain device);
    
    /**
     * Publish a device assigned to site event.
     * @param device the device
     * @param siteId the site ID
     */
    void publishDeviceAssignedToSite(DeviceDomain device, UUID siteId);
    
    /**
     * Publish a device removed from site event.
     * @param device the device
     * @param previousSiteId the previous site ID
     */
    void publishDeviceRemovedFromSite(DeviceDomain device, UUID previousSiteId);
    
    /**
     * Publish a device roles updated event.
     * @param device the device
     * @param roleNames the assigned role names
     */
    void publishDeviceRolesUpdated(DeviceDomain device, List<String> roleNames);
    
    /**
     * Publish a device roles cleared event.
     * @param device the device
     * @param previousRoleNames the previous role names
     */
    void publishDeviceRolesCleared(DeviceDomain device, List<String> previousRoleNames);
}