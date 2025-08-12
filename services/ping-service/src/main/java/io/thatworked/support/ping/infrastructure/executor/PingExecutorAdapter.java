package io.thatworked.support.ping.infrastructure.executor;

import io.thatworked.support.ping.domain.model.PingTargetDomain;
import io.thatworked.support.ping.domain.port.PingExecutor;
import io.thatworked.support.ping.infrastructure.executor.VirtualThreadPingExecutor;
import io.thatworked.support.ping.infrastructure.queue.PingTask;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

/**
 * Infrastructure adapter for ping executor.
 */
@Component
public class PingExecutorAdapter implements PingExecutor {
    
    private final VirtualThreadPingExecutor virtualThreadPingExecutor;
    
    public PingExecutorAdapter(VirtualThreadPingExecutor virtualThreadPingExecutor) {
        this.virtualThreadPingExecutor = virtualThreadPingExecutor;
    }
    
    @Override
    public void startPing(PingTargetDomain pingTarget) {
        PingTask task = PingTask.builder()
            .deviceId(pingTarget.getDeviceId())
            .ipAddress(pingTarget.getIpAddress())
            .intervalMs(pingTarget.getPingIntervalSeconds() * 1000L)
            .recurring(true)
            .build();
        task.updateNextExecutionTime();
        
        Duration interval = Duration.ofSeconds(pingTarget.getPingIntervalSeconds());
        virtualThreadPingExecutor.schedulePing(task, interval);
    }
    
    @Override
    public void stopPing(UUID deviceId) {
        virtualThreadPingExecutor.cancelPing(deviceId);
    }
}