package io.thatworked.support.gateway.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.gateway.client.DeviceServiceClient;
import io.thatworked.support.gateway.dto.device.DeviceDTO;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Resilient service wrapper for device operations with circuit breaker patterns.
 * Demonstrates circuit breaker, retry, and time limiter patterns.
 */
@Service
public class ResilientDeviceService {

    private final DeviceServiceClient deviceServiceClient;
    private final StructuredLogger logger;

    public ResilientDeviceService(DeviceServiceClient deviceServiceClient, 
                                 StructuredLoggerFactory loggerFactory) {
        this.deviceServiceClient = deviceServiceClient;
        this.logger = loggerFactory.getLogger(ResilientDeviceService.class);
    }

    /**
     * Get device with circuit breaker and retry patterns.
     */
    @CircuitBreaker(name = "device-service", fallbackMethod = "getDeviceFallback")
    @Retry(name = "device-service")
    public DeviceDTO getDevice(UUID deviceId) {
        logger.with("operation", "getDevice")
              .with("deviceId", deviceId.toString())
              .info("Calling device service");
              
        return deviceServiceClient.getDevice(deviceId);
    }

    /**
     * Get devices batch with circuit breaker protection.
     */
    @CircuitBreaker(name = "device-service", fallbackMethod = "getDevicesBatchFallback")
    @Retry(name = "device-service")
    public List<DeviceDTO> getDevicesBatch(List<UUID> deviceIds) {
        logger.with("operation", "getDevicesBatch")
              .with("deviceCount", deviceIds.size())
              .info("Calling device service batch endpoint");
              
        return deviceServiceClient.getDevicesBatch(deviceIds);
    }

    /**
     * Async device creation with time limiter.
     */
    @CircuitBreaker(name = "device-service", fallbackMethod = "createDeviceAsyncFallback")
    @TimeLimiter(name = "device-service")
    @Retry(name = "device-service")
    public CompletableFuture<DeviceDTO> createDeviceAsync(DeviceDTO device) {
        logger.with("operation", "createDeviceAsync")
              .with("deviceName", device.getName())
              .info("Creating device asynchronously");
              
        return CompletableFuture.supplyAsync(() -> deviceServiceClient.createDevice(device));
    }

    // Fallback methods

    /**
     * Fallback method for getDevice when circuit breaker is open.
     */
    public DeviceDTO getDeviceFallback(UUID deviceId, Exception ex) {
        logger.with("operation", "getDeviceFallback")
              .with("deviceId", deviceId.toString())
              .with("errorType", ex.getClass().getSimpleName())
              .error("Device service unavailable, returning fallback response", ex);
              
        DeviceDTO fallback = new DeviceDTO();
        fallback.setId(deviceId);
        fallback.setName("Device Unavailable");
        fallback.setIpAddress("0.0.0.0");
        return fallback;
    }

    /**
     * Fallback method for getDevicesBatch when circuit breaker is open.
     */
    public List<DeviceDTO> getDevicesBatchFallback(List<UUID> deviceIds, Exception ex) {
        logger.with("operation", "getDevicesBatchFallback")
              .with("deviceCount", deviceIds.size())
              .with("errorType", ex.getClass().getSimpleName())
              .error("Device service batch unavailable, returning empty list", ex);
              
        return Collections.emptyList();
    }

    /**
     * Fallback method for createDeviceAsync when circuit breaker is open.
     */
    public CompletableFuture<DeviceDTO> createDeviceAsyncFallback(DeviceDTO device, Exception ex) {
        logger.with("operation", "createDeviceAsyncFallback")
              .with("deviceName", device.getName())
              .with("errorType", ex.getClass().getSimpleName())
              .error("Device creation service unavailable", ex);
              
        DeviceDTO fallback = new DeviceDTO();
        fallback.setName("Failed to create: " + device.getName());
        return CompletableFuture.completedFuture(fallback);
    }
}