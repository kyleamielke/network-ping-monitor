package io.thatworked.support.ping.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for service settings.
 */
@Configuration
@ConfigurationProperties(prefix = "ping-service.service")
public class ServiceConfig {
    
    private String name = "ping-service";
    private String version = "1.0.0";
    private String description = "Network device ping monitoring service for NetworkPing Monitor";
    
    private final Api api = new Api();
    
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Api getApi() {
        return api;
    }
    
    public static class Api {
        private String basePath = "/api/ping";
        private String version = "v1";
        
        private final Cors cors = new Cors();
        private final Pagination pagination = new Pagination();
        
        public String getBasePath() {
            return basePath;
        }
        
        public void setBasePath(String basePath) {
            this.basePath = basePath;
        }
        
        public String getVersion() {
            return version;
        }
        
        public void setVersion(String version) {
            this.version = version;
        }
        
        public Cors getCors() {
            return cors;
        }
        
        public Pagination getPagination() {
            return pagination;
        }
    }
    
    public static class Cors {
        private String allowedOrigins = "*";
        private String allowedMethods = "GET,POST,PUT,DELETE,OPTIONS";
        private String allowedHeaders = "*";
        private int maxAge = 3600;
        
        public String getAllowedOrigins() {
            return allowedOrigins;
        }
        
        public void setAllowedOrigins(String allowedOrigins) {
            this.allowedOrigins = allowedOrigins;
        }
        
        public String getAllowedMethods() {
            return allowedMethods;
        }
        
        public void setAllowedMethods(String allowedMethods) {
            this.allowedMethods = allowedMethods;
        }
        
        public String getAllowedHeaders() {
            return allowedHeaders;
        }
        
        public void setAllowedHeaders(String allowedHeaders) {
            this.allowedHeaders = allowedHeaders;
        }
        
        public int getMaxAge() {
            return maxAge;
        }
        
        public void setMaxAge(int maxAge) {
            this.maxAge = maxAge;
        }
    }
    
    public static class Pagination {
        private int defaultPage = 0;
        private int defaultPageSize = 20;
        private int maxPageSize = 100;
        private String defaultSortField = "timestamp";
        private String defaultSortDirection = "DESC";
        
        public int getDefaultPage() {
            return defaultPage;
        }
        
        public void setDefaultPage(int defaultPage) {
            this.defaultPage = defaultPage;
        }
        
        public int getDefaultPageSize() {
            return defaultPageSize;
        }
        
        public void setDefaultPageSize(int defaultPageSize) {
            this.defaultPageSize = defaultPageSize;
        }
        
        public int getMaxPageSize() {
            return maxPageSize;
        }
        
        public void setMaxPageSize(int maxPageSize) {
            this.maxPageSize = maxPageSize;
        }
        
        public String getDefaultSortField() {
            return defaultSortField;
        }
        
        public void setDefaultSortField(String defaultSortField) {
            this.defaultSortField = defaultSortField;
        }
        
        public String getDefaultSortDirection() {
            return defaultSortDirection;
        }
        
        public void setDefaultSortDirection(String defaultSortDirection) {
            this.defaultSortDirection = defaultSortDirection;
        }
    }
}