package io.thatworked.support.ping.infrastructure.event;

import io.thatworked.support.ping.domain.PingTarget;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PingTargetStartedEvent extends ApplicationEvent {
    private final PingTarget pingTarget;

    public PingTargetStartedEvent(PingTarget pingTarget) {
        super(pingTarget);
        this.pingTarget = pingTarget;
    }
}