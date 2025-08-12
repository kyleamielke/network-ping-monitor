package io.thatworked.support.gateway.controller;

import io.thatworked.support.gateway.event.PingEvent;
import io.thatworked.support.gateway.resolver.SubscriptionResolver;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/test")
public class TestController {
    
    private final SubscriptionResolver subscriptionResolver;
    
    public TestController(SubscriptionResolver subscriptionResolver) {
        this.subscriptionResolver = subscriptionResolver;
    }
    
    @PostMapping("/ping-event")
    public String publishTestPingEvent(@RequestParam(required = false) String deviceId) {
        UUID uuid = deviceId != null ? UUID.fromString(deviceId) : UUID.randomUUID();
        
        PingEvent event = PingEvent.builder()
            .deviceId(uuid)
            .timestamp(Instant.now())
            .success(true)
            .responseTimeMs(42L)
            .build();
            
        subscriptionResolver.publishPingEvent(event);
        
        return "Test ping event published for device: " + uuid;
    }
    
    @GetMapping("/subscription-status")
    public String getSubscriptionStatus() {
        return "Subscription Status: " + subscriptionResolver.getSubscriptionStatus();
    }
}