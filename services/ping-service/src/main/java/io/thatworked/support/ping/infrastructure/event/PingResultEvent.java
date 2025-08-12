package io.thatworked.support.ping.infrastructure.event;

import io.thatworked.support.ping.domain.PingResult;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PingResultEvent extends ApplicationEvent {
    private final PingResult pingResult;

    public PingResultEvent(PingResult pingResult) {
        super(pingResult);
        this.pingResult = pingResult;
    }
}