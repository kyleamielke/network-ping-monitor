package io.thatworked.support.ping.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "monitored_devices")
public class MonitoredDevice {
    
    @Id
    @Column(name = "device_id")
    private UUID deviceId;
    
    @Column(name = "device_name", nullable = false)
    private String deviceName;
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "os")
    private String os;
    
    @Column(name = "os_type")
    private String osType;
    
    @Column(name = "site")
    private UUID site;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
    
    @UpdateTimestamp  
    @Column(name = "updated_at")
    private Instant updatedAt;
    
    public MonitoredDevice() {}
    
    public MonitoredDevice(UUID deviceId, String deviceName, String ipAddress) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.ipAddress = ipAddress;
        // Timestamps handled by @CreationTimestamp/@UpdateTimestamp
    }
    
    // Timestamps automatically handled by @CreationTimestamp/@UpdateTimestamp annotations
    
    // Getters and Setters
    public UUID getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(UUID deviceId) {
        this.deviceId = deviceId;
    }
    
    public String getDeviceName() {
        return deviceName;
    }
    
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getOs() {
        return os;
    }
    
    public void setOs(String os) {
        this.os = os;
    }
    
    public String getOsType() {
        return osType;
    }
    
    public void setOsType(String osType) {
        this.osType = osType;
    }
    
    public UUID getSite() {
        return site;
    }
    
    public void setSite(UUID site) {
        this.site = site;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
    
    public Instant getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}