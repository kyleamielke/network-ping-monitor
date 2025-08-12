package io.thatworked.support.alert.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration properties for messages.
 */
@Configuration
@ConfigurationProperties(prefix = "alert-service.messages")
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
        private String alertNotFound = "Alert not found with id: %s";
        private String deviceMaxAlerts = "Device has reached maximum number of active alerts: %d";
        private String alertAlreadyAcknowledged = "Alert is already acknowledged";
        private String alertAlreadyResolved = "Alert is already resolved";
        private String validationFailed = "Request validation failed";
        private String internalServerError = "An unexpected error occurred";
        
        // Getters and setters
        public String getAlertNotFound() {
            return alertNotFound;
        }
        
        public void setAlertNotFound(String alertNotFound) {
            this.alertNotFound = alertNotFound;
        }
        
        public String getDeviceMaxAlerts() {
            return deviceMaxAlerts;
        }
        
        public void setDeviceMaxAlerts(String deviceMaxAlerts) {
            this.deviceMaxAlerts = deviceMaxAlerts;
        }
        
        public String getAlertAlreadyAcknowledged() {
            return alertAlreadyAcknowledged;
        }
        
        public void setAlertAlreadyAcknowledged(String alertAlreadyAcknowledged) {
            this.alertAlreadyAcknowledged = alertAlreadyAcknowledged;
        }
        
        public String getAlertAlreadyResolved() {
            return alertAlreadyResolved;
        }
        
        public void setAlertAlreadyResolved(String alertAlreadyResolved) {
            this.alertAlreadyResolved = alertAlreadyResolved;
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
        private String alertNotFoundTitle = "Alert Not Found";
        private String validationFailedTitle = "Validation Failed";
        private String internalErrorTitle = "Internal Server Error";
        
        // Getters and setters
        public String getAlertNotFoundTitle() {
            return alertNotFoundTitle;
        }
        
        public void setAlertNotFoundTitle(String alertNotFoundTitle) {
            this.alertNotFoundTitle = alertNotFoundTitle;
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
        private String alertCreated = "Alert created successfully";
        private String alertAcknowledged = "Alert acknowledged by %s";
        private String alertResolved = "Alert resolved by %s";
        private String alertDeleted = "Alert deleted successfully";
        private String alertsCleaned = "Cleaned up %d resolved alerts";
        
        // Getters and setters
        public String getAlertCreated() {
            return alertCreated;
        }
        
        public void setAlertCreated(String alertCreated) {
            this.alertCreated = alertCreated;
        }
        
        public String getAlertAcknowledged() {
            return alertAcknowledged;
        }
        
        public void setAlertAcknowledged(String alertAcknowledged) {
            this.alertAcknowledged = alertAcknowledged;
        }
        
        public String getAlertResolved() {
            return alertResolved;
        }
        
        public void setAlertResolved(String alertResolved) {
            this.alertResolved = alertResolved;
        }
        
        public String getAlertDeleted() {
            return alertDeleted;
        }
        
        public void setAlertDeleted(String alertDeleted) {
            this.alertDeleted = alertDeleted;
        }
        
        public String getAlertsCleaned() {
            return alertsCleaned;
        }
        
        public void setAlertsCleaned(String alertsCleaned) {
            this.alertsCleaned = alertsCleaned;
        }
    }
    
    public static class Defaults {
        private String acknowledgedBy = "System";
        private String resolvedBy = "System";
        private String unknownDevice = "Unknown Device";
        private String unknownUser = "Unknown User";
        
        // Getters and setters
        public String getAcknowledgedBy() {
            return acknowledgedBy;
        }
        
        public void setAcknowledgedBy(String acknowledgedBy) {
            this.acknowledgedBy = acknowledgedBy;
        }
        
        public String getResolvedBy() {
            return resolvedBy;
        }
        
        public void setResolvedBy(String resolvedBy) {
            this.resolvedBy = resolvedBy;
        }
        
        public String getUnknownDevice() {
            return unknownDevice;
        }
        
        public void setUnknownDevice(String unknownDevice) {
            this.unknownDevice = unknownDevice;
        }
        
        public String getUnknownUser() {
            return unknownUser;
        }
        
        public void setUnknownUser(String unknownUser) {
            this.unknownUser = unknownUser;
        }
    }
}