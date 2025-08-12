package io.thatworked.support.ping.domain.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain model for ping targets.
 * Pure domain object without infrastructure dependencies.
 */
public class PingTargetDomain {
    private final UUID deviceId;
    private final String ipAddress;
    private final String hostname;
    private final boolean monitored;
    private final Integer pingIntervalSeconds;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Long version;

    public PingTargetDomain(UUID deviceId, String ipAddress, String hostname, boolean monitored, 
                           Integer pingIntervalSeconds, Instant createdAt, Instant updatedAt, Long version) {
        this.deviceId = deviceId;
        this.ipAddress = ipAddress;
        this.hostname = hostname;
        this.monitored = monitored;
        this.pingIntervalSeconds = pingIntervalSeconds;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    // Getters
    public UUID getDeviceId() { return deviceId; }
    public String getIpAddress() { return ipAddress; }
    public String getHostname() { return hostname; }
    public boolean isMonitored() { return monitored; }
    public Integer getPingIntervalSeconds() { return pingIntervalSeconds; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Long getVersion() { return version; }

    // Domain methods
    public PingTargetDomain withMonitored(boolean monitored) {
        return new PingTargetDomain(deviceId, ipAddress, hostname, monitored, pingIntervalSeconds, createdAt, Instant.now(), version);
    }

    public PingTargetDomain withIpAddress(String ipAddress) {
        return new PingTargetDomain(deviceId, ipAddress, hostname, monitored, pingIntervalSeconds, createdAt, Instant.now(), version);
    }

    public PingTargetDomain withHostname(String hostname) {
        return new PingTargetDomain(deviceId, ipAddress, hostname, monitored, pingIntervalSeconds, createdAt, Instant.now(), version);
    }

    public PingTargetDomain withPingInterval(Integer pingIntervalSeconds) {
        return new PingTargetDomain(deviceId, ipAddress, hostname, monitored, pingIntervalSeconds, createdAt, Instant.now(), version);
    }

    // Static factory methods
    public static PingTargetDomain create(UUID deviceId, String ipAddress, Integer pingIntervalSeconds) {
        Instant now = Instant.now();
        return new PingTargetDomain(deviceId, ipAddress, null, false, pingIntervalSeconds, now, now, 1L);
    }

    public static PingTargetDomain create(UUID deviceId, String ipAddress, String hostname, Integer pingIntervalSeconds) {
        Instant now = Instant.now();
        return new PingTargetDomain(deviceId, ipAddress, hostname, false, pingIntervalSeconds, now, now, 1L);
    }

    public static PingTargetDomain createMonitored(UUID deviceId, String ipAddress, Integer pingIntervalSeconds) {
        Instant now = Instant.now();
        return new PingTargetDomain(deviceId, ipAddress, null, true, pingIntervalSeconds, now, now, 1L);
    }

    public static PingTargetDomain createMonitored(UUID deviceId, String ipAddress, String hostname, Integer pingIntervalSeconds) {
        Instant now = Instant.now();
        return new PingTargetDomain(deviceId, ipAddress, hostname, true, pingIntervalSeconds, now, now, 1L);
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private UUID deviceId;
        private String ipAddress;
        private String hostname;
        private boolean monitored;
        private Integer pingIntervalSeconds;
        private Instant createdAt;
        private Instant updatedAt;
        private Long version;
        
        public Builder deviceId(UUID deviceId) {
            this.deviceId = deviceId;
            return this;
        }
        
        public Builder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }
        
        public Builder hostname(String hostname) {
            this.hostname = hostname;
            return this;
        }
        
        public Builder monitored(boolean monitored) {
            this.monitored = monitored;
            return this;
        }
        
        public Builder pingIntervalSeconds(Integer pingIntervalSeconds) {
            this.pingIntervalSeconds = pingIntervalSeconds;
            return this;
        }
        
        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        
        public Builder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }
        
        public Builder version(Long version) {
            this.version = version;
            return this;
        }
        
        public PingTargetDomain build() {
            return new PingTargetDomain(deviceId, ipAddress, hostname, monitored, pingIntervalSeconds, createdAt, updatedAt, version);
        }
    }
}