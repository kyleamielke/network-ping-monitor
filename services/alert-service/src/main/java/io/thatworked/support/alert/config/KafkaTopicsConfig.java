package io.thatworked.support.alert.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration properties for Kafka topics.
 */
@Configuration
@ConfigurationProperties(prefix = "alert-service.kafka.topics")
public class KafkaTopicsConfig {
    
    // Consumed topics
    private String deviceEvents = "device-events";
    private String pingMonitoringEvents = "ping-monitoring-events";
    private String snmpMonitoringEvents = "snmp-monitoring-events";
    private String wmiMonitoringEvents = "wmi-monitoring-events";
    
    // Published topics
    private String alertLifecycleEvents = "alert-lifecycle-events";
    private String alertNotifications = "alert-notifications";
    
    // Getters and setters
    public String getDeviceEvents() {
        return deviceEvents;
    }
    
    public void setDeviceEvents(String deviceEvents) {
        this.deviceEvents = deviceEvents;
    }
    
    public String getPingMonitoringEvents() {
        return pingMonitoringEvents;
    }
    
    public void setPingMonitoringEvents(String pingMonitoringEvents) {
        this.pingMonitoringEvents = pingMonitoringEvents;
    }
    
    public String getSnmpMonitoringEvents() {
        return snmpMonitoringEvents;
    }
    
    public void setSnmpMonitoringEvents(String snmpMonitoringEvents) {
        this.snmpMonitoringEvents = snmpMonitoringEvents;
    }
    
    public String getWmiMonitoringEvents() {
        return wmiMonitoringEvents;
    }
    
    public void setWmiMonitoringEvents(String wmiMonitoringEvents) {
        this.wmiMonitoringEvents = wmiMonitoringEvents;
    }
    
    public String getAlertLifecycleEvents() {
        return alertLifecycleEvents;
    }
    
    public void setAlertLifecycleEvents(String alertLifecycleEvents) {
        this.alertLifecycleEvents = alertLifecycleEvents;
    }
    
    public String getAlertNotifications() {
        return alertNotifications;
    }
    
    public void setAlertNotifications(String alertNotifications) {
        this.alertNotifications = alertNotifications;
    }
}