package io.thatworked.support.gateway.config;

import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.gateway.dataloader.AlertsBatchLoader;
import io.thatworked.support.gateway.dataloader.DeviceMonitoringBatchLoader;
import io.thatworked.support.gateway.dataloader.DeviceStatusBatchLoader;
import io.thatworked.support.gateway.dataloader.PingTargetBatchLoader;
import io.thatworked.support.gateway.dataloader.RecentPingsBatchLoader;
import io.thatworked.support.gateway.dto.alert.AlertListDTO;
import io.thatworked.support.gateway.dto.dashboard.DeviceStatusDTO;
import io.thatworked.support.gateway.dto.monitoring.DeviceMonitoringDTO;
import io.thatworked.support.gateway.dto.ping.PingTargetDTO;
import io.thatworked.support.gateway.dto.ping.PingResultDTO;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.BatchLoaderRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Configuration
public class DataLoaderConfig {
    
    private final StructuredLogger logger;
    private final BatchLoaderRegistry registry;
    private final DeviceMonitoringBatchLoader deviceMonitoringBatchLoader;
    private final DeviceStatusBatchLoader deviceStatusBatchLoader;
    private final PingTargetBatchLoader pingTargetBatchLoader;
    private final AlertsBatchLoader alertsBatchLoader;
    private final RecentPingsBatchLoader recentPingsBatchLoader;
    
    public DataLoaderConfig(StructuredLoggerFactory loggerFactory,
                          BatchLoaderRegistry registry,
                          DeviceMonitoringBatchLoader deviceMonitoringBatchLoader,
                          DeviceStatusBatchLoader deviceStatusBatchLoader,
                          PingTargetBatchLoader pingTargetBatchLoader,
                          AlertsBatchLoader alertsBatchLoader,
                          RecentPingsBatchLoader recentPingsBatchLoader) {
        this.logger = loggerFactory.getLogger(DataLoaderConfig.class);
        this.registry = registry;
        this.deviceMonitoringBatchLoader = deviceMonitoringBatchLoader;
        this.deviceStatusBatchLoader = deviceStatusBatchLoader;
        this.pingTargetBatchLoader = pingTargetBatchLoader;
        this.alertsBatchLoader = alertsBatchLoader;
        this.recentPingsBatchLoader = recentPingsBatchLoader;
    }
    
    @jakarta.annotation.PostConstruct
    public void configureBatchLoaders() {
        logger.with("operation", "configureBatchLoaders")
              .info("Configuring GraphQL DataLoaders");
        
        // Register device monitoring batch loader
        registry.forTypePair(UUID.class, DeviceMonitoringDTO.class)
                .withName("deviceMonitoringLoader")
                .registerMappedBatchLoader((deviceIds, env) -> {
                    logger.with("operation", "deviceMonitoringLoader")
                          .with("deviceCount", deviceIds.size())
                          .debug("Loading device monitoring data");
                    return deviceMonitoringBatchLoader.load(new ArrayList<>(deviceIds));
                });
        
        // Register ping target batch loader
        registry.forTypePair(UUID.class, PingTargetDTO.class)
                .withName("pingTargetLoader")
                .registerMappedBatchLoader((deviceIds, env) -> {
                    logger.with("operation", "pingTargetLoader")
                          .with("deviceCount", deviceIds.size())
                          .debug("Loading ping target data");
                    return pingTargetBatchLoader.load(new ArrayList<>(deviceIds));
                });
        
        // Register device status batch loader
        registry.forTypePair(UUID.class, DeviceStatusDTO.class)
                .withName("deviceStatusLoader")
                .registerMappedBatchLoader((deviceIds, env) -> {
                    logger.with("operation", "deviceStatusLoader")
                          .with("deviceCount", deviceIds.size())
                          .debug("Loading device status data");
                    return deviceStatusBatchLoader.load(deviceIds);
                });
        
        // Register alerts batch loader
        registry.forTypePair(UUID.class, AlertListDTO.class)
                .withName("alertsLoader")
                .registerMappedBatchLoader((deviceIds, env) -> {
                    logger.with("operation", "alertsLoader")
                          .with("deviceCount", deviceIds.size())
                          .debug("Loading alerts data");
                    return alertsBatchLoader.load(new ArrayList<>(deviceIds));
                });
        
        // Register recent pings batch loader  
        registry.forTypePair(UUID.class, List.class)
                .withName("recentPingsLoader")
                .registerMappedBatchLoader((deviceIds, env) -> {
                    logger.with("operation", "recentPingsLoader")
                          .with("deviceCount", deviceIds.size())
                          .debug("Loading recent pings data for mini indicators");
                    return recentPingsBatchLoader.load(new ArrayList<>(deviceIds))
                            .map(result -> {
                                // Convert to the expected Map type
                                return (Map<UUID, List>) (Map<?, ?>) result;
                            });
                });
        
        logger.with("operation", "configureBatchLoaders")
              .with("loadersConfigured", 5)
              .info("DataLoaders configuration complete");
    }
}