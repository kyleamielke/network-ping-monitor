package io.thatworked.support.ping.infrastructure.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class PingTargetStoppedEvent extends ApplicationEvent {
    private final UUID deviceId;

    public PingTargetStoppedEvent(UUID deviceId) {
        super(deviceId);
        this.deviceId = deviceId;
    }
}