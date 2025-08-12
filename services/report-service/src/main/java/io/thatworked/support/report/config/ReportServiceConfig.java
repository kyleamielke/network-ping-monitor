package io.thatworked.support.report.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

/**
 * Configuration properties for report service.
 */
@ConfigurationProperties(prefix = "report-service")
public class ReportServiceConfig {
    
    private final Service service;
    private final Report report;
    
    @ConstructorBinding
    public ReportServiceConfig(Service service, Report report) {
        this.service = service != null ? service : new Service();
        this.report = report != null ? report : new Report();
    }
    
    public Service getService() {
        return service;
    }
    
    public Report getReport() {
        return report;
    }
    
    public static class Service {
        private String name = "report-service";
        private String version = "1.0.0";
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getVersion() {
            return version;
        }
        
        public void setVersion(String version) {
            this.version = version;
        }
    }
    
    public static class Report {
        private String storagePath = "/tmp/reports";
        private int retentionDays = 90;
        private int maxConcurrentReports = 5;
        
        public String getStoragePath() {
            return storagePath;
        }
        
        public void setStoragePath(String storagePath) {
            this.storagePath = storagePath;
        }
        
        public int getRetentionDays() {
            return retentionDays;
        }
        
        public void setRetentionDays(int retentionDays) {
            this.retentionDays = retentionDays;
        }
        
        public int getMaxConcurrentReports() {
            return maxConcurrentReports;
        }
        
        public void setMaxConcurrentReports(int maxConcurrentReports) {
            this.maxConcurrentReports = maxConcurrentReports;
        }
    }
}