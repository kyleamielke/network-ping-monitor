package io.thatworked.support.ping.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for messages.
 */
@Configuration
@ConfigurationProperties(prefix = "ping-service.messages")
public class MessagesConfig {
    
    private final Errors errors = new Errors();
    private final Api api = new Api();
    private final Success success = new Success();
    private final Defaults defaults = new Defaults();
    
    public Errors getErrors() {
        return errors;
    }
    
    public Api getApi() {
        return api;
    }
    
    public Success getSuccess() {
        return success;
    }
    
    public Defaults getDefaults() {
        return defaults;
    }
    
    public static class Errors {
        private String pingTargetNotFound = "Ping target not found with id: %s";
        private String entityNotFound = "Entity not found: %s";
        private String deviceMaxTargets = "Device has reached maximum number of ping targets: %d";
        private String targetAlreadyExists = "Ping target already exists for IP: %s";
        private String validationFailed = "Request validation failed";
        private String internalServerError = "An unexpected error occurred";
        
        // Getters and setters
        public String getPingTargetNotFound() {
            return pingTargetNotFound;
        }
        
        public void setPingTargetNotFound(String pingTargetNotFound) {
            this.pingTargetNotFound = pingTargetNotFound;
        }
        
        public String getEntityNotFound() {
            return entityNotFound;
        }
        
        public void setEntityNotFound(String entityNotFound) {
            this.entityNotFound = entityNotFound;
        }
        
        public String getDeviceMaxTargets() {
            return deviceMaxTargets;
        }
        
        public void setDeviceMaxTargets(String deviceMaxTargets) {
            this.deviceMaxTargets = deviceMaxTargets;
        }
        
        public String getTargetAlreadyExists() {
            return targetAlreadyExists;
        }
        
        public void setTargetAlreadyExists(String targetAlreadyExists) {
            this.targetAlreadyExists = targetAlreadyExists;
        }
        
        public String getValidationFailed() {
            return validationFailed;
        }
        
        public void setValidationFailed(String validationFailed) {
            this.validationFailed = validationFailed;
        }
        
        public String getInternalServerError() {
            return internalServerError;
        }
        
        public void setInternalServerError(String internalServerError) {
            this.internalServerError = internalServerError;
        }
    }
    
    public static class Api {
        private String pingTargetNotFoundTitle = "Ping Target Not Found";
        private String entityNotFoundTitle = "Entity Not Found";
        private String validationFailedTitle = "Validation Failed";
        private String internalErrorTitle = "Internal Server Error";
        
        // Getters and setters
        public String getPingTargetNotFoundTitle() {
            return pingTargetNotFoundTitle;
        }
        
        public void setPingTargetNotFoundTitle(String pingTargetNotFoundTitle) {
            this.pingTargetNotFoundTitle = pingTargetNotFoundTitle;
        }
        
        public String getEntityNotFoundTitle() {
            return entityNotFoundTitle;
        }
        
        public void setEntityNotFoundTitle(String entityNotFoundTitle) {
            this.entityNotFoundTitle = entityNotFoundTitle;
        }
        
        public String getValidationFailedTitle() {
            return validationFailedTitle;
        }
        
        public void setValidationFailedTitle(String validationFailedTitle) {
            this.validationFailedTitle = validationFailedTitle;
        }
        
        public String getInternalErrorTitle() {
            return internalErrorTitle;
        }
        
        public void setInternalErrorTitle(String internalErrorTitle) {
            this.internalErrorTitle = internalErrorTitle;
        }
    }
    
    public static class Success {
        private String pingTargetCreated = "Ping target created successfully";
        private String pingTargetStarted = "Ping monitoring started for target: %s";
        private String pingTargetStopped = "Ping monitoring stopped for target: %s";
        private String pingTargetDeleted = "Ping target deleted successfully";
        private String pingResultsRetrieved = "Retrieved %d ping results";
        
        // Getters and setters
        public String getPingTargetCreated() {
            return pingTargetCreated;
        }
        
        public void setPingTargetCreated(String pingTargetCreated) {
            this.pingTargetCreated = pingTargetCreated;
        }
        
        public String getPingTargetStarted() {
            return pingTargetStarted;
        }
        
        public void setPingTargetStarted(String pingTargetStarted) {
            this.pingTargetStarted = pingTargetStarted;
        }
        
        public String getPingTargetStopped() {
            return pingTargetStopped;
        }
        
        public void setPingTargetStopped(String pingTargetStopped) {
            this.pingTargetStopped = pingTargetStopped;
        }
        
        public String getPingTargetDeleted() {
            return pingTargetDeleted;
        }
        
        public void setPingTargetDeleted(String pingTargetDeleted) {
            this.pingTargetDeleted = pingTargetDeleted;
        }
        
        public String getPingResultsRetrieved() {
            return pingResultsRetrieved;
        }
        
        public void setPingResultsRetrieved(String pingResultsRetrieved) {
            this.pingResultsRetrieved = pingResultsRetrieved;
        }
    }
    
    public static class Defaults {
        private String unknownDevice = "Unknown Device";
        private String unknownIpAddress = "Unknown IP";
        private String systemUser = "System";
        private String pingTimeout = "Ping timeout";
        
        // Getters and setters
        public String getUnknownDevice() {
            return unknownDevice;
        }
        
        public void setUnknownDevice(String unknownDevice) {
            this.unknownDevice = unknownDevice;
        }
        
        public String getUnknownIpAddress() {
            return unknownIpAddress;
        }
        
        public void setUnknownIpAddress(String unknownIpAddress) {
            this.unknownIpAddress = unknownIpAddress;
        }
        
        public String getSystemUser() {
            return systemUser;
        }
        
        public void setSystemUser(String systemUser) {
            this.systemUser = systemUser;
        }
        
        public String getPingTimeout() {
            return pingTimeout;
        }
        
        public void setPingTimeout(String pingTimeout) {
            this.pingTimeout = pingTimeout;
        }
    }
}