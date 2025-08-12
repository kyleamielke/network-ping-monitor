package io.thatworked.support.ping.infrastructure.executor;

import io.thatworked.support.ping.domain.PingResult;
import io.thatworked.support.ping.infrastructure.queue.PingTask;

/**
 * Interface for delegating ping execution to avoid circular dependencies
 */
public interface PingExecutionDelegate {
    
    /**
     * Execute a ping task
     * @param task The ping task to execute
     * @return The ping result
     */
    PingResult executePing(PingTask task);
}