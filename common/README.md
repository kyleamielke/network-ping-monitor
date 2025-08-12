# Common Support Library

## Overview
Shared library containing common utilities and standardized components for all microservices in the support system.

## Features

### Standardized Logging
- **Structured JSON logging** with consistent format across all services
- **Correlation ID tracking** for distributed request tracing
- **Error code standardization** for better error categorization
- **Performance metrics logging** for operation timing
- **Security event logging** for audit trails

## Usage

### Adding to Your Service

Add the dependency to your `build.gradle.kts`:
```kotlin
dependencies {
    implementation(project(":common"))
}
```

### Using Structured Logging

```java
@Service
public class MyService {
    private final StructuredLogger log;
    
    public MyService(StructuredLoggerFactory loggerFactory) {
        this.log = loggerFactory.getLogger(MyService.class);
    }
    
    public void doSomething() {
        // Simple logging
        log.info("Processing request");
        
        // Logging with context
        log.with("userId", userId)
           .with("action", "update")
           .info("User action performed");
        
        // Error logging with error code
        try {
            // operation
        } catch (Exception e) {
            log.with("errorCode", ErrorCode.DB_001.getCode())
               .error("Database operation failed", e);
        }
        
        // Performance logging
        log.measure("database.query", () -> {
            return repository.findAll();
        });
    }
}
```

### Correlation ID Filter

The correlation ID filter is automatically registered when you include this library. It will:
- Extract correlation ID from incoming HTTP headers
- Generate new correlation ID if not present
- Add correlation ID to all log entries
- Propagate correlation ID to outgoing HTTP calls

### Kafka Correlation ID

For Kafka messages, use the interceptor:
```java
ProducerRecord<String, Object> record = new ProducerRecord<>(topic, key, value);
KafkaCorrelationIdInterceptor.addCorrelationId(record);
kafkaTemplate.send(record);
```

### Standard Logback Configuration

Include the common logback configuration in your service's `logback-spring.xml`:
```xml
<configuration>
    <include resource="logback-common.xml"/>
    <!-- Service-specific configuration -->
</configuration>
```

## Error Codes

Standard error codes are defined in `ErrorCode` enum:
- **NET_xxx**: Network-related errors
- **DB_xxx**: Database errors
- **VAL_xxx**: Validation errors
- **SVC_xxx**: Service errors
- **KFK_xxx**: Kafka errors
- **SEC_xxx**: Security errors
- **BIZ_xxx**: Business logic errors
- **SYS_xxx**: System errors

## Components

- **StructuredLogger**: Main logging interface with fluent API
- **StructuredLoggerFactory**: Factory for creating loggers
- **CorrelationIdFilter**: HTTP filter for correlation ID management
- **KafkaCorrelationIdInterceptor**: Kafka interceptor for correlation ID
- **ErrorCode**: Standardized error codes
- **LogContext**: Standard log entry structure
- **LoggingConfiguration**: Spring auto-configuration

## Configuration

The library is auto-configured via Spring Boot. You can customize behavior with these properties:
```yaml
logging:
  level:
    io.thatworked.support: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%X{correlationId}] %-5level [%thread] %logger{36} - %msg%n"
```

## Best Practices

1. **Always use structured logging** instead of string concatenation
2. **Include relevant context** in log entries (IDs, operations, etc.)
3. **Use appropriate log levels** (DEBUG, INFO, WARN, ERROR)
4. **Use error codes** for categorizing errors
5. **Measure performance** of critical operations
6. **Log security events** for audit trails