package io.thatworked.support.gateway.resolver;

import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.gateway.dto.dashboard.DeviceStatusDTO;
import io.thatworked.support.gateway.dto.device.DeviceDTO;
import io.thatworked.support.gateway.dto.monitoring.DeviceMonitoringDTO;
import io.thatworked.support.gateway.dto.ping.PingTargetDTO;
import io.thatworked.support.gateway.dto.ping.PingResultDTO;
import graphql.schema.DataFetchingEnvironment;
import org.dataloader.DataLoader;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Field resolver for Device type to handle monitoring-related fields
 */
@Controller
public class DeviceFieldResolver {
    
    private final StructuredLogger logger;
    
    public DeviceFieldResolver(StructuredLoggerFactory loggerFactory) {
        this.logger = loggerFactory.getLogger(DeviceFieldResolver.class);
    }
    
    @SchemaMapping(typeName = "Device", field = "monitoring")
    public CompletableFuture<DeviceMonitoringDTO> monitoring(DeviceDTO device, DataFetchingEnvironment env) {
        logger.with("operation", "device.monitoring")
              .with("deviceId", device.getId())
              .debug("Resolving monitoring field for device");
        
        // Use the existing DataLoader for device monitoring
        DataLoader<UUID, DeviceMonitoringDTO> loader = env.getDataLoader("deviceMonitoringLoader");
        return loader.load(device.getId());
    }
    
    @SchemaMapping(typeName = "Device", field = "pingTarget")
    public CompletableFuture<PingTargetDTO> pingTarget(DeviceDTO device, DataFetchingEnvironment env) {
        logger.with("operation", "device.pingTarget")
              .with("deviceId", device.getId())
              .debug("Resolving pingTarget field for device");
        
        // Use the DataLoader for batch loading
        DataLoader<UUID, PingTargetDTO> loader = env.getDataLoader("pingTargetLoader");
        return loader.load(device.getId());
    }
    
    @SchemaMapping(typeName = "Device", field = "currentStatus")
    public CompletableFuture<DeviceStatusDTO> currentStatus(DeviceDTO device, DataFetchingEnvironment env) {
        logger.with("operation", "device.currentStatus")
              .with("deviceId", device.getId())
              .debug("Resolving currentStatus field for device");
        
        // Use the DataLoader for batch loading
        DataLoader<UUID, DeviceStatusDTO> loader = env.getDataLoader("deviceStatusLoader");
        return loader.load(device.getId());
    }
    
    @SchemaMapping(typeName = "Device", field = "recentPings")
    public CompletableFuture<List<PingResultDTO>> recentPings(DeviceDTO device, DataFetchingEnvironment env) {
        logger.with("operation", "device.recentPings")
              .with("deviceId", device.getId())
              .debug("Resolving recentPings field for device");
        
        // Use the DataLoader for batch loading recent pings
        DataLoader<UUID, List<PingResultDTO>> loader = env.getDataLoader("recentPingsLoader");
        return loader.load(device.getId());
    }
}