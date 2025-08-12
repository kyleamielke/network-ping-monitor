package io.thatworked.support.gateway.resolver;

import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.gateway.client.*;
import io.thatworked.support.gateway.dto.alert.AlertDTO;
import io.thatworked.support.gateway.dto.alert.CreateAlertRequest;
import io.thatworked.support.gateway.dto.device.DeviceDTO;
import io.thatworked.support.gateway.dto.ping.PingTargetDTO;
import io.thatworked.support.gateway.exception.ValidationException;
import io.thatworked.support.gateway.exception.OptimisticLockingException;
import io.thatworked.support.gateway.exception.ResourceNotFoundException;
import io.thatworked.support.gateway.exception.OperationFailedException;
import io.thatworked.support.gateway.exception.InvalidInputException;
import io.thatworked.support.gateway.util.DeviceTypeMapper;
import io.thatworked.support.gateway.validation.InputValidator;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class MutationResolver {
    
    private final StructuredLogger logger;
    private final DeviceServiceClient deviceServiceClient;
    private final PingServiceClient pingServiceClient;
    private final AlertServiceClient alertServiceClient;
    private final ReportServiceClient reportServiceClient;
    
    public MutationResolver(StructuredLoggerFactory loggerFactory,
                           DeviceServiceClient deviceServiceClient,
                           PingServiceClient pingServiceClient,
                           AlertServiceClient alertServiceClient,
                           ReportServiceClient reportServiceClient) {
        this.logger = loggerFactory.getLogger(MutationResolver.class);
        this.deviceServiceClient = deviceServiceClient;
        this.pingServiceClient = pingServiceClient;
        this.alertServiceClient = alertServiceClient;
        this.reportServiceClient = reportServiceClient;
    }
    
    @MutationMapping
    public DeviceDTO createDevice(@Argument Map<String, Object> input) {
        long startTime = System.currentTimeMillis();
        logger.with("operation", "createDevice")
              .with("mutation", "createDevice")
              .with("deviceName", input.get("name"))
              .with("ipAddress", input.get("ipAddress"))
              .debug("Starting GraphQL mutation");
        
        try {
            // Basic format validation
            validateDeviceInput(input);
            
            DeviceDTO device = DeviceDTO.builder()
                .name((String) input.get("name"))
                .ipAddress((String) input.get("ipAddress"))
                .hostname((String) input.get("hostname"))
                .macAddress((String) input.get("macAddress"))
                .type((String) input.get("type"))  // Device service expects display names like "Server", not enum names
                .os((String) input.get("os"))
                .osType((String) input.get("osType"))
                .make((String) input.get("make"))
                .model((String) input.get("model"))
                .endpointId((String) input.get("endpointId"))
                .assetTag((String) input.get("assetTag"))
                .description((String) input.get("description"))
                .location((String) input.get("location"))
                .site(input.get("site") != null ? UUID.fromString((String) input.get("site")) : null)
                .metadata(new HashMap<>())  // Initialize with empty map to avoid null
                .build();
        
            DeviceDTO created = deviceServiceClient.createDevice(device);
            long duration = System.currentTimeMillis() - startTime;
            
            logger.with("operation", "createDevice")
                  .with("mutation", "createDevice")
                  .with("deviceId", created.getId())
                  .with("deviceName", created.getName())
                  .with("durationMs", duration)
                  .info("GraphQL mutation completed successfully");
            
            return created;
        } catch (Exception e) {
            logger.with("operation", "createDevice")
                  .with("mutation", "createDevice")
                  .with("deviceName", input.get("name"))
                  .with("errorType", e.getClass().getSimpleName())
                  .with("errorMessage", e.getMessage())
                  .error("GraphQL mutation failed", e);
            throw e;
        }
    }
    
    /**
     * Helper method to convert empty strings to null.
     * This allows the frontend to clear fields by sending empty strings.
     */
    private String normalizeEmptyString(String value) {
        return (value != null && value.trim().isEmpty()) ? null : value;
    }
    
    @MutationMapping
    public DeviceDTO updateDevice(@Argument String id, @Argument Map<String, Object> input) {
        logger.with("operation", "updateDevice")
              .with("deviceId", id)
              .info("Updating device");
        
        try {
            UUID deviceId = UUID.fromString(id);
            // Basic format validation for update
            validateUpdateDeviceInput(input);
            
            DeviceDTO existing = deviceServiceClient.getDevice(deviceId);
            
            if (existing == null) {
                throw new ResourceNotFoundException("Device", id);
            }
            
            // Validate optimistic locking version
            if (input.containsKey("expectedVersion")) {
                Integer expectedVersion = (Integer) input.get("expectedVersion");
                if (expectedVersion != null && !expectedVersion.equals(existing.getVersion().intValue())) {
                    logger.with("operation", "updateDevice")
                          .with("deviceId", id)
                          .with("expectedVersion", expectedVersion)
                          .with("currentVersion", existing.getVersion())
                          .warn("Optimistic locking conflict detected");
                    throw new OptimisticLockingException("device", id, 
                            expectedVersion.longValue(), existing.getVersion());
                }
            }
            
            // Update only provided fields - treat empty strings as null to allow clearing fields
            // IMPORTANT: We always update if key is present, even if value becomes null after normalization
            // This allows users to clear fields by sending empty strings
            if (input.containsKey("name")) {
                String value = normalizeEmptyString((String) input.get("name"));
                existing.setName(value);
            }
            if (input.containsKey("ipAddress")) {
                String value = normalizeEmptyString((String) input.get("ipAddress"));
                existing.setIpAddress(value);
            }
            if (input.containsKey("hostname")) {
                String value = normalizeEmptyString((String) input.get("hostname"));
                existing.setHostname(value);
            }
            if (input.containsKey("macAddress")) {
                String value = normalizeEmptyString((String) input.get("macAddress"));
                existing.setMacAddress(value);
            }
            if (input.containsKey("type")) {
                String value = normalizeEmptyString((String) input.get("type"));
                existing.setType(value);
            }
            if (input.containsKey("os")) {
                String value = normalizeEmptyString((String) input.get("os"));
                existing.setOs(value);
            }
            if (input.containsKey("osType")) {
                String value = normalizeEmptyString((String) input.get("osType"));
                existing.setOsType(value);
            }
            if (input.containsKey("make")) {
                String value = normalizeEmptyString((String) input.get("make"));
                existing.setMake(value);
            }
            if (input.containsKey("model")) {
                String value = normalizeEmptyString((String) input.get("model"));
                existing.setModel(value);
            }
            if (input.containsKey("endpointId")) {
                String value = normalizeEmptyString((String) input.get("endpointId"));
                existing.setEndpointId(value);
            }
            if (input.containsKey("assetTag")) {
                String value = normalizeEmptyString((String) input.get("assetTag"));
                existing.setAssetTag(value);
            }
            if (input.containsKey("description")) {
                String value = normalizeEmptyString((String) input.get("description"));
                existing.setDescription(value);
            }
            if (input.containsKey("location")) {
                String value = normalizeEmptyString((String) input.get("location"));
                existing.setLocation(value);
            }
            if (input.containsKey("site")) {
                String siteId = (String) input.get("site");
                existing.setSite(siteId != null ? UUID.fromString(siteId) : null);
            }
            
            // Ensure metadata is not null to avoid backend issues
            if (existing.getMetadata() == null) {
                existing.setMetadata(new HashMap<>());
            }
            
            return deviceServiceClient.updateDevice(deviceId, existing);
        } catch (OptimisticLockingException e) {
            // Re-throw optimistic locking exceptions as-is for proper GraphQL error handling
            throw e;
        } catch (ValidationException e) {
            // Re-throw validation exceptions as-is
            throw e;
        } catch (Exception e) {
            logger.with("operation", "updateDevice")
                  .with("deviceId", id)
                  .with("error", e.getMessage())
                  .error("Failed to update device", e);
            throw new OperationFailedException("updateDevice", e.getMessage(), e);
        }
    }
    
    @MutationMapping
    public Boolean deleteDevice(@Argument String id) {
        logger.with("operation", "deleteDevice")
              .with("deviceId", id)
              .info("Deleting device");
        
        try {
            deviceServiceClient.deleteDevice(UUID.fromString(id));
            return true;
        } catch (Exception e) {
            logger.with("operation", "deleteDevice")
                  .with("deviceId", id)
                  .error("Failed to delete device", e);
            return false;
        }
    }
    
    @MutationMapping
    public Map<String, Object> bulkDeleteDevices(@Argument List<String> deviceIds) {
        logger.with("operation", "bulkDeleteDevices")
              .with("deviceCount", deviceIds.size())
              .info("Bulk deleting devices");
        
        try {
            // Convert string IDs to UUIDs
            List<UUID> uuidList = deviceIds.stream()
                .map(UUID::fromString)
                .toList();
            
            Map<String, Object> result = deviceServiceClient.bulkDeleteDevices(uuidList);
            
            logger.with("operation", "bulkDeleteDevices")
                  .with("successful", result.get("successful"))
                  .with("failed", result.get("failed"))
                  .info("Bulk delete completed");
            
            return result;
        } catch (Exception e) {
            logger.with("operation", "bulkDeleteDevices")
                  .with("deviceCount", deviceIds.size())
                  .error("Failed to bulk delete devices", e);
            throw new OperationFailedException("bulkDeleteDevices", e.getMessage(), e);
        }
    }
    
    @MutationMapping
    public Map<String, Object> bulkUpdateDevices(@Argument Map<String, Object> input) {
        logger.with("operation", "bulkUpdateDevices")
              .with("inputKeys", input.keySet())
              .info("Bulk updating devices");
        
        try {
            @SuppressWarnings("unchecked")
            List<String> deviceIds = (List<String>) input.get("deviceIds");
            @SuppressWarnings("unchecked")
            Map<String, Object> updates = (Map<String, Object>) input.get("updates");
            
            // Convert string IDs to UUIDs
            List<UUID> uuidList = deviceIds.stream()
                .map(UUID::fromString)
                .toList();
            
            // Validate update fields if needed
            if (updates != null) {
                validateUpdateFields(updates);
            }
            
            Map<String, Object> result = deviceServiceClient.bulkUpdateDevices(uuidList, updates);
            
            logger.with("operation", "bulkUpdateDevices")
                  .with("successful", result.get("successful"))
                  .with("failed", result.get("failed"))
                  .info("Bulk update completed");
            
            return result;
        } catch (Exception e) {
            logger.with("operation", "bulkUpdateDevices")
                  .error("Failed to bulk update devices", e);
            throw new OperationFailedException("bulkUpdateDevices", e.getMessage(), e);
        }
    }
    
    @MutationMapping
    public PingTargetDTO startPingMonitoring(@Argument String deviceId) {
        logger.with("operation", "startPingMonitoring")
              .with("deviceId", deviceId)
              .info("Starting ping monitoring for device");
        
        UUID id = UUID.fromString(deviceId);
        DeviceDTO device = deviceServiceClient.getDevice(id);
        
        // Check if target already exists
        List<PingTargetDTO> targets = pingServiceClient.getAllPingTargets();
        PingTargetDTO existingTarget = targets.stream()
            .filter(t -> t.getDeviceId().equals(id))
            .findFirst()
            .orElse(null);
        
        if (existingTarget != null) {
            // Start monitoring on existing target
            return pingServiceClient.startMonitoring(id);
        } else {
            // Create new target
            PingTargetDTO newTarget = PingTargetDTO.builder()
                .deviceId(id)
                .ipAddress(device.getIpAddress())
                .hostname(device.getHostname())
                .monitored(true)
                .pingIntervalSeconds(30)
                .build();
            
            // Create the target first
            PingTargetDTO createdTarget = pingServiceClient.createPingTarget(newTarget);
            
            // Then start monitoring
            return pingServiceClient.startMonitoring(id);
        }
    }
    
    @MutationMapping
    public Boolean stopPingMonitoring(@Argument String deviceId) {
        logger.with("operation", "stopPingMonitoring")
              .with("deviceId", deviceId)
              .info("Stopping ping monitoring for device");
        
        try {
            UUID id = UUID.fromString(deviceId);
            pingServiceClient.stopMonitoring(id);
            return true;
        } catch (Exception e) {
            logger.with("operation", "stopPingMonitoring")
                  .with("deviceId", deviceId)
                  .error("Failed to stop monitoring", e);
            return false;
        }
    }
    
    @MutationMapping
    public Map<String, Object> startMonitoringAll() {
        long startTime = System.currentTimeMillis();
        logger.with("operation", "startMonitoringAll")
              .with("mutation", "startMonitoringAll")
              .debug("Starting GraphQL mutation");
        
        try {
            // TECH DEBT: Fetching all devices at once - same scaling concerns as devicesWithMonitoring
            // See QueryResolver.devicesWithMonitoring() for detailed tech debt notes
            List<DeviceDTO> devices = deviceServiceClient.getDevices(0, 1000).getContent();
            
            int successful = 0;
            int failed = 0;
            
            for (DeviceDTO device : devices) {
                try {
                    startPingMonitoring(device.getId().toString());
                    successful++;
                } catch (Exception e) {
                    failed++;
                    logger.with("operation", "startMonitoringAll")
                          .with("deviceId", device.getId())
                          .warn("Failed to start monitoring for device");
                }
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("totalDevices", devices.size());
            result.put("successful", successful);
            result.put("failed", failed);
            result.put("errors", List.of());
            
            long duration = System.currentTimeMillis() - startTime;
            logger.with("operation", "startMonitoringAll")
                  .with("mutation", "startMonitoringAll")
                  .with("totalDevices", devices.size())
                  .with("successful", successful)
                  .with("failed", failed)
                  .with("durationMs", duration)
                  .info("GraphQL mutation completed");
            
            return result;
        } catch (Exception e) {
            logger.with("operation", "startMonitoringAll")
                  .with("mutation", "startMonitoringAll")
                  .with("errorType", e.getClass().getSimpleName())
                  .with("errorMessage", e.getMessage())
                  .error("GraphQL mutation failed", e);
            throw e;
        }
    }
    
    @MutationMapping
    public Map<String, Object> startMonitoringByType(@Argument String deviceType) {
        long startTime = System.currentTimeMillis();
        logger.with("operation", "startMonitoringByType")
              .with("mutation", "startMonitoringByType")
              .with("deviceType", deviceType)
              .debug("Starting GraphQL mutation");
        
        try {
            List<DeviceDTO> devices = deviceServiceClient.getDevicesByType(deviceType);
            
            int successful = 0;
            int failed = 0;
            
            for (DeviceDTO device : devices) {
                try {
                    startPingMonitoring(device.getId().toString());
                    successful++;
                } catch (Exception e) {
                    failed++;
                    logger.with("operation", "startMonitoringByType")
                          .with("deviceId", device.getId())
                          .with("deviceType", deviceType)
                          .warn("Failed to start monitoring for device");
                }
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("totalDevices", devices.size());
            result.put("successful", successful);
            result.put("failed", failed);
            result.put("errors", List.of());
            
            long duration = System.currentTimeMillis() - startTime;
            logger.with("operation", "startMonitoringByType")
                  .with("mutation", "startMonitoringByType")
                  .with("deviceType", deviceType)
                  .with("totalDevices", devices.size())
                  .with("successful", successful)
                  .with("failed", failed)
                  .with("durationMs", duration)
                  .info("GraphQL mutation completed");
            
            return result;
        } catch (Exception e) {
            logger.with("operation", "startMonitoringByType")
                  .with("mutation", "startMonitoringByType")
                  .with("deviceType", deviceType)
                  .with("errorType", e.getClass().getSimpleName())
                  .with("errorMessage", e.getMessage())
                  .error("GraphQL mutation failed", e);
            throw e;
        }
    }
    
    @MutationMapping
    public Map<String, Object> stopMonitoringAll() {
        long startTime = System.currentTimeMillis();
        logger.with("operation", "stopMonitoringAll")
              .with("mutation", "stopMonitoringAll")
              .debug("Starting GraphQL mutation");
        
        try {
            List<PingTargetDTO> targets = pingServiceClient.getActivePingTargets();
            
            int successful = 0;
            int failed = 0;
            
            for (PingTargetDTO target : targets) {
                try {
                    pingServiceClient.stopMonitoring(target.getDeviceId());
                    successful++;
                } catch (Exception e) {
                    failed++;
                    logger.with("operation", "stopMonitoringAll")
                          .with("deviceId", target.getDeviceId())
                          .warn("Failed to stop monitoring for device");
                }
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("totalDevices", targets.size());
            result.put("successful", successful);
            result.put("failed", failed);
            result.put("errors", List.of());
            
            long duration = System.currentTimeMillis() - startTime;
            logger.with("operation", "stopMonitoringAll")
                  .with("mutation", "stopMonitoringAll")
                  .with("totalDevices", targets.size())
                  .with("successful", successful)
                  .with("failed", failed)
                  .with("durationMs", duration)
                  .info("GraphQL mutation completed");
            
            return result;
        } catch (Exception e) {
            logger.with("operation", "stopMonitoringAll")
                  .with("mutation", "stopMonitoringAll")
                  .with("errorType", e.getClass().getSimpleName())
                  .with("errorMessage", e.getMessage())
                  .error("GraphQL mutation failed", e);
            throw e;
        }
    }
    
    @MutationMapping
    public AlertDTO createAlert(@Argument Map<String, Object> input) {
        logger.with("operation", "createAlert")
              .with("deviceId", input.get("deviceId"))
              .info("Creating new alert");
        
        UUID deviceId = UUID.fromString((String) input.get("deviceId"));
        DeviceDTO device = deviceServiceClient.getDevice(deviceId);
        
        CreateAlertRequest request = CreateAlertRequest.builder()
            .deviceId(deviceId)
            .deviceName(device.getName())
            .alertType((String) input.get("alertType"))
            .message((String) input.get("message"))
            .build();
        
        return alertServiceClient.createAlert(request);
    }
    
    @MutationMapping
    public AlertDTO acknowledgeAlert(@Argument String alertId) {
        logger.with("operation", "acknowledgeAlert")
              .with("alertId", alertId)
              .info("Acknowledging alert");
        
        return alertServiceClient.acknowledgeAlert(UUID.fromString(alertId));
    }
    
    @MutationMapping
    public AlertDTO resolveAlert(@Argument String alertId) {
        logger.with("operation", "resolveAlert")
              .with("alertId", alertId)
              .info("Resolving alert");
        
        return alertServiceClient.resolveAlert(UUID.fromString(alertId));
    }
    
    @MutationMapping
    public Boolean deleteAlert(@Argument String alertId) {
        long startTime = System.currentTimeMillis();
        logger.with("operation", "deleteAlert")
              .with("mutation", "deleteAlert")
              .with("alertId", alertId)
              .debug("Starting GraphQL mutation");
        
        try {
            UUID id = UUID.fromString(alertId);
            alertServiceClient.deleteAlert(id);
            long duration = System.currentTimeMillis() - startTime;
            
            logger.with("operation", "deleteAlert")
                  .with("mutation", "deleteAlert")
                  .with("alertId", alertId)
                  .with("durationMs", duration)
                  .info("GraphQL mutation completed successfully");
            
            return true;
        } catch (IllegalArgumentException e) {
            logger.with("operation", "deleteAlert")
                  .with("mutation", "deleteAlert")
                  .with("alertId", alertId)
                  .with("errorType", "INVALID_UUID")
                  .error("Invalid alert ID format", e);
            return false;
        } catch (Exception e) {
            logger.with("operation", "deleteAlert")
                  .with("mutation", "deleteAlert")
                  .with("alertId", alertId)
                  .with("errorType", e.getClass().getSimpleName())
                  .with("errorMessage", e.getMessage())
                  .error("GraphQL mutation failed", e);
            return false;
        }
    }
    
    private void validateDeviceInput(Map<String, Object> input) {
        Map<String, String> errors = new HashMap<>();
        
        // Validate required fields
        String name = (String) input.get("name");
        if (!InputValidator.isNotBlank(name)) {
            errors.put("name", "Device name is required");
        } else if (!InputValidator.isWithinLength(name, 255)) {
            errors.put("name", "Device name must not exceed 255 characters");
        }
        
        // Validate that either IP address or hostname is provided
        String ipAddress = (String) input.get("ipAddress");
        String hostname = (String) input.get("hostname");
        
        boolean hasIpAddress = InputValidator.isNotBlank(ipAddress);
        boolean hasHostname = InputValidator.isNotBlank(hostname);
        
        if (!hasIpAddress && !hasHostname) {
            errors.put("address", "Either IP address or hostname is required");
        } else {
            // Validate IP address if provided
            if (hasIpAddress && !InputValidator.isValidIpAddress(ipAddress)) {
                errors.put("ipAddress", "Invalid IP address format");
            }
            // Validate hostname if provided
            if (hasHostname && !InputValidator.isValidHostname(hostname)) {
                errors.put("hostname", "Invalid hostname format");
            }
        }
        
        // Validate optional fields if present
        String macAddress = (String) input.get("macAddress");
        if (macAddress != null && !macAddress.isEmpty() && !InputValidator.isValidMacAddress(macAddress)) {
            errors.put("macAddress", "Invalid MAC address format (expected format: XX:XX:XX:XX:XX:XX)");
        }
        
        String siteId = (String) input.get("site");
        if (siteId != null && !siteId.isEmpty() && !InputValidator.isValidUuid(siteId)) {
            errors.put("site", "Invalid site ID format");
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Device validation failed", errors);
        }
    }
    
    private void validateUpdateDeviceInput(Map<String, Object> input) {
        Map<String, String> errors = new HashMap<>();
        
        // Validate fields only if they are present (partial update)
        if (input.containsKey("name")) {
            String name = (String) input.get("name");
            if (!InputValidator.isNotBlank(name)) {
                errors.put("name", "Device name cannot be empty");
            } else if (!InputValidator.isWithinLength(name, 255)) {
                errors.put("name", "Device name must not exceed 255 characters");
            }
        }
        
        if (input.containsKey("ipAddress")) {
            String ipAddress = (String) input.get("ipAddress");
            if (ipAddress != null && !ipAddress.isEmpty() && !InputValidator.isValidIpAddress(ipAddress)) {
                errors.put("ipAddress", "Invalid IP address format");
            }
        }
        
        if (input.containsKey("hostname")) {
            String hostname = (String) input.get("hostname");
            if (hostname != null && !hostname.isEmpty() && !InputValidator.isValidHostname(hostname)) {
                errors.put("hostname", "Invalid hostname format");
            }
        }
        
        if (input.containsKey("macAddress")) {
            String macAddress = (String) input.get("macAddress");
            if (macAddress != null && !macAddress.isEmpty() && !InputValidator.isValidMacAddress(macAddress)) {
                errors.put("macAddress", "Invalid MAC address format (expected format: XX:XX:XX:XX:XX:XX)");
            }
        }
        
        if (input.containsKey("site")) {
            String siteId = (String) input.get("site");
            if (siteId != null && !siteId.isEmpty() && !InputValidator.isValidUuid(siteId)) {
                errors.put("site", "Invalid site ID format");
            }
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Device update validation failed", errors);
        }
    }
    
    @MutationMapping
    public Map<String, Object> generateReport(@Argument Map<String, Object> input) {
        logger.with("operation", "generateReport")
              .with("mutation", "generateReport")
              .info("Generating report");
        
        try {
            String reportType = (String) input.get("reportType");
            String format = (String) input.get("format");
            String startDate = (String) input.get("startDate");
            String endDate = (String) input.get("endDate");
            String title = (String) input.get("title");
            
            // Map GraphQL report types to report service types
            String serviceReportType = mapReportType(reportType);
            
            // Build request for report service
            Map<String, Object> reportRequest = new HashMap<>();
            reportRequest.put("reportType", serviceReportType);
            reportRequest.put("format", format);
            
            // Provide default dates if not specified (last 30 days)
            if (startDate != null) {
                reportRequest.put("startDate", startDate);
            } else {
                reportRequest.put("startDate", java.time.Instant.now().minus(30, java.time.temporal.ChronoUnit.DAYS).toString());
            }
            
            if (endDate != null) {
                reportRequest.put("endDate", endDate);
            } else {
                reportRequest.put("endDate", java.time.Instant.now().toString());
            }
            
            if (title != null) reportRequest.put("title", title);
            
            Map<String, Object> reportResponse = reportServiceClient.generateReport(reportRequest);
            
            // Transform response to match GraphQL schema
            Map<String, Object> result = new HashMap<>();
            result.put("reportId", reportResponse.get("id"));
            result.put("filename", reportResponse.get("filename"));
            result.put("reportType", reportType);  // Use original GraphQL type
            result.put("format", format);
            result.put("generatedAt", reportResponse.get("generatedAt"));
            result.put("fileSizeBytes", reportResponse.get("fileSizeBytes"));
            result.put("downloadUrl", "/api/v1/reports/" + reportResponse.get("id") + "/download?format=" + format);
            
            logger.with("operation", "generateReport")
                  .with("reportId", result.get("reportId"))
                  .with("reportType", reportType)
                  .info("Report generated successfully");
            
            return result;
        } catch (Exception e) {
            logger.with("operation", "generateReport")
                  .with("error", e.getMessage())
                  .error("Failed to generate report", e);
            throw new OperationFailedException("generateReport", e.getMessage(), e);
        }
    }
    
    private String mapReportType(String graphqlType) {
        // Map GraphQL report types to report service enum values
        switch (graphqlType) {
            case "DEVICE_UPTIME": return "UPTIME_SUMMARY";
            case "SYSTEM_HEALTH": return "DEVICE_STATUS";
            case "NETWORK_PERFORMANCE": return "PING_PERFORMANCE";
            case "ALERT_SUMMARY": return "ALERT_HISTORY";
            default: throw new InvalidInputException("reportType", graphqlType, "Supported types are: DEVICE_UPTIME, SYSTEM_HEALTH, NETWORK_PERFORMANCE, ALERT_SUMMARY");
        }
    }
    
    private void validateUpdateFields(Map<String, Object> updates) {
        Map<String, String> errors = new HashMap<>();
        
        // Validate fields only if they are present (partial update)
        if (updates.containsKey("name")) {
            String name = (String) updates.get("name");
            if (!InputValidator.isNotBlank(name)) {
                errors.put("name", "Device name cannot be empty");
            } else if (!InputValidator.isWithinLength(name, 255)) {
                errors.put("name", "Device name must not exceed 255 characters");
            }
        }
        
        if (updates.containsKey("ipAddress")) {
            String ipAddress = (String) updates.get("ipAddress");
            if (ipAddress != null && !ipAddress.isEmpty() && !InputValidator.isValidIpAddress(ipAddress)) {
                errors.put("ipAddress", "Invalid IP address format");
            }
        }
        
        if (updates.containsKey("hostname")) {
            String hostname = (String) updates.get("hostname");
            if (hostname != null && !hostname.isEmpty() && !InputValidator.isValidHostname(hostname)) {
                errors.put("hostname", "Invalid hostname format");
            }
        }
        
        if (updates.containsKey("macAddress")) {
            String macAddress = (String) updates.get("macAddress");
            if (macAddress != null && !macAddress.isEmpty() && !InputValidator.isValidMacAddress(macAddress)) {
                errors.put("macAddress", "Invalid MAC address format (expected format: XX:XX:XX:XX:XX:XX)");
            }
        }
        
        // Throw validation exception if any errors found
        if (!errors.isEmpty()) {
            throw new ValidationException("Validation failed for bulk update", errors);
        }
    }
}