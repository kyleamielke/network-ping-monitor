package io.thatworked.support.gateway.client;

import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class NotificationServiceFallbackFactory implements FallbackFactory<NotificationServiceClient> {
    
    private final StructuredLogger logger;
    
    public NotificationServiceFallbackFactory(StructuredLoggerFactory loggerFactory) {
        this.logger = loggerFactory.getLogger(NotificationServiceFallbackFactory.class);
    }
    
    @Override
    public NotificationServiceClient create(Throwable cause) {
        return new NotificationServiceClient() {
            
            @Override
            public Map<String, Object> getNotificationConfig() {
                logger.with("operation", "getNotificationConfig")
                      .error("Notification service unavailable", cause);
                
                Map<String, Object> fallback = new HashMap<>();
                fallback.put("error", "Notification service is currently unavailable");
                fallback.put("message", cause.getMessage());
                return fallback;
            }
            
            @Override
            public Map<String, Object> updateNotificationConfig(Map<String, Object> config) {
                logger.with("operation", "updateNotificationConfig")
                      .error("Notification service unavailable", cause);
                
                Map<String, Object> fallback = new HashMap<>();
                fallback.put("error", "Notification service is currently unavailable");
                fallback.put("message", cause.getMessage());
                return fallback;
            }
            
            @Override
            public Map<String, Object> sendTestNotification(Map<String, Object> request) {
                logger.with("operation", "sendTestNotification")
                      .error("Notification service unavailable", cause);
                
                Map<String, Object> fallback = new HashMap<>();
                fallback.put("error", "Notification service is currently unavailable");
                fallback.put("message", cause.getMessage());
                return fallback;
            }
        };
    }
}