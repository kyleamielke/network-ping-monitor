package io.thatworked.support.gateway.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(
    name = "notification-service",
    url = "${services.notification.url:http://notification-service:8083}",
    fallbackFactory = NotificationServiceFallbackFactory.class
)
public interface NotificationServiceClient {
    
    @GetMapping("/api/v1/notifications/config")
    Map<String, Object> getNotificationConfig();
    
    @PutMapping("/api/v1/notifications/config")
    Map<String, Object> updateNotificationConfig(@RequestBody Map<String, Object> config);
    
    @PostMapping("/api/v1/notifications/test")
    Map<String, Object> sendTestNotification(@RequestBody Map<String, Object> request);
}