package io.thatworked.support.gateway.config;

import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket configuration for GraphQL subscriptions.
 * Note: Spring GraphQL handles most of the WebSocket setup automatically
 * when graphql.websocket.path is configured in application.yml
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    
    private final StructuredLogger logger;
    
    public WebSocketConfig(StructuredLoggerFactory loggerFactory) {
        this.logger = loggerFactory.getLogger(WebSocketConfig.class);
    }
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Spring GraphQL automatically registers its WebSocket handler
        // at the path specified in application.yml (graphql.websocket.path)
        logger.with("operation", "registerWebSocketHandlers")
              .info("WebSocket configuration initialized for GraphQL subscriptions");
    }
}