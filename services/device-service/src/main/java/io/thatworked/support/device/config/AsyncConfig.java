package io.thatworked.support.device.config;

import io.thatworked.support.device.config.properties.DeviceServiceProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Async configuration for event publishing.
 */
@Configuration
@EnableAsync
@RequiredArgsConstructor
@ConditionalOnProperty(name = "device-service.features.async-events", havingValue = "true", matchIfMissing = true)
public class AsyncConfig {
    
    private final DeviceServiceProperties properties;
    
    @Bean(name = "eventPublisherExecutor")
    public Executor eventPublisherExecutor() {
        var poolConfig = properties.getThreadPool().getEventPublisher();
        
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(poolConfig.getCoreSize());
        executor.setMaxPoolSize(poolConfig.getMaxSize());
        executor.setQueueCapacity(poolConfig.getQueueCapacity());
        executor.setThreadNamePrefix(poolConfig.getThreadNamePrefix());
        executor.setKeepAliveSeconds(poolConfig.getKeepAliveSeconds());
        executor.setAllowCoreThreadTimeOut(poolConfig.isAllowCoreThreadTimeout());
        executor.setWaitForTasksToCompleteOnShutdown(poolConfig.isWaitForTasksToCompleteOnShutdown());
        executor.setAwaitTerminationSeconds(poolConfig.getAwaitTerminationSeconds());
        executor.initialize();
        return executor;
    }
}