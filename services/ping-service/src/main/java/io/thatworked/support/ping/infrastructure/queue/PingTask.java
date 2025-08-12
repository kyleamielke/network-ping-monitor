package io.thatworked.support.ping.infrastructure.queue;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class PingTask implements Comparable<PingTask> {
    private final UUID deviceId;
    private final String ipAddress;
    private final String hostname;
    private final long intervalMs;
    private final boolean recurring;

    private Instant nextExecutionTime;

    public void updateNextExecutionTime() {
        this.nextExecutionTime = Instant.now().plusMillis(intervalMs);
    }

    @Override
    public int compareTo(PingTask other) {
        return this.nextExecutionTime.compareTo(other.nextExecutionTime);
    }
}