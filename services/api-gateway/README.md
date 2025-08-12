# API Gateway Service

GraphQL API aggregation layer for the NetworkPing Monitor system. Provides a unified interface for frontend applications to interact with all microservices through a single GraphQL endpoint with advanced features like real-time subscriptions, circuit breakers, and comprehensive configuration management.

## Architecture Overview

The API Gateway acts as a Backend for Frontend (BFF) pattern, orchestrating calls to multiple microservices:

- **Device Service** - Device inventory and management
- **Ping Service** - Network monitoring and ping operations  
- **Alert Service** - Alert lifecycle and notifications
- **Report Service** - Report generation and export
- **Search Service** - Advanced device search capabilities
- **Dashboard Cache Service** - Performance-optimized dashboard data

###  Design Patterns Implemented

- **GraphQL Federation** - Single unified schema
- **Circuit Breaker** - Resilience4j for fault tolerance
- **Event-Driven Architecture** - Kafka event streaming
- **Service Discovery** - Consul integration
- **AOP (Aspect-Oriented Programming)** - Cross-cutting concerns
- **DataLoader Pattern** - N+1 query optimization
- **Real-time Streaming** - WebSocket subscriptions

##  Key Features

###  GraphQL API
- **Unified Schema** - Single endpoint for all operations
- **Type Safety** - Strongly typed queries and mutations
- **Real-time Subscriptions** - WebSocket support for live updates
- **Query Complexity Analysis** - Configurable complexity scoring
- **Field-level Security** - Granular access control
- **Interactive IDE** - GraphiQL interface for development

###  Resilience & Reliability
- **Circuit Breakers** - Service-specific fault tolerance with configurable thresholds
- **Fallback Mechanisms** - Graceful degradation on service failures
- **Health Checks** - Comprehensive health monitoring
- **Retry Logic** - Configurable retry policies
- **Timeout Management** - Service-specific timeout configuration

###  Performance & Scalability
- **DataLoader Optimization** - Batch loading to prevent N+1 queries
- **Parallel Service Calls** - CompletableFuture for concurrent operations
- **Caching Strategy** - Multi-level caching for frequently accessed data
- **Pagination Support** - Efficient large dataset handling
- **Connection Pooling** - Optimized database connections

###  Monitoring & Observability
- **Structured Logging** - JSON-formatted logs with correlation IDs
- **Metrics Collection** - Micrometer integration for monitoring
- **Circuit Breaker Metrics** - Real-time resilience monitoring
- **GraphQL Metrics** - Query performance and complexity tracking
- **Distributed Tracing** - Request flow tracking across services

##  Quick Start

### Prerequisites
- Java 21
- Docker & Docker Compose
- Consul (for service discovery)
- Kafka (for event streaming)

### Local Development

```bash
# Build the application
./gradlew build

# Run locally (requires dependencies)
./gradlew bootRun

# Or with Docker Compose (recommended)
cd /path/to/support/project
./scripts/dev-up.sh api-gateway
```

### Container Build
```bash
# Build with no cache
cd /path/to/support/project
./scripts/docker-build.sh --no-cache api-gateway

# Restart with new image
./scripts/dev-down.sh api-gateway
./scripts/dev-up.sh -d api-gateway
```

## Configuration Management

###  Configuration Files

The API Gateway uses a modular configuration approach with dedicated files:

#### **`application.yml`** - Core Configuration
```yaml
server:
  port: 8080

spring:
  application:
    name: api-gateway
  config:
    import:
      - classpath:graphql.yml
      - classpath:application-circuitbreaker.yml
  
  cloud:
    consul:
      discovery:
        enabled: true
        health-check-path: /actuator/health
        instance-id: ${spring.application.name}:${server.port}
  
  kafka:
    bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS:kafka:9092}
    consumer:
      group-id: ${KAFKA_CONSUMER_GROUP_ID:api-gateway}
    topics:
      alert-lifecycle-events: ${KAFKA_TOPIC_ALERT_LIFECYCLE:alert-lifecycle-events}
      ping-monitoring-events: ${KAFKA_TOPIC_PING_MONITORING:ping-monitoring-events}
```

#### **`graphql.yml`** - GraphQL Configuration
```yaml
graphql:
  # Query execution limits
  query:
    max-complexity: ${GRAPHQL_MAX_QUERY_COMPLEXITY:100}
    max-depth: ${GRAPHQL_MAX_QUERY_DEPTH:8}
  
  # Pagination settings
  pagination:
    default-size: ${GRAPHQL_DEFAULT_PAGE_SIZE:20}
    max-size: ${GRAPHQL_MAX_PAGE_SIZE:100}
    bulk-operation-size: ${GRAPHQL_BULK_OPERATION_SIZE:10000}
    ping-history-limit: ${GRAPHQL_PING_HISTORY_LIMIT:1000}
    
  # Client interface call defaults
  client-defaults:
    recent-alerts-limit: ${GRAPHQL_CLIENT_RECENT_ALERTS_LIMIT:5}
    device-monitoring-ping-history: ${GRAPHQL_DEVICE_MONITORING_PING_HISTORY:50}
    dashboard-ping-history: ${GRAPHQL_DASHBOARD_PING_HISTORY:10}
    search-limit: ${GRAPHQL_SEARCH_LIMIT:10}
  
  # Field complexity scoring for query analysis
  field-complexity:
    # Expensive operations
    monitoring-dashboard: ${GRAPHQL_COMPLEXITY_MONITORING_DASHBOARD:10}
    search-devices: ${GRAPHQL_COMPLEXITY_SEARCH_DEVICES:8}
    ping-history: ${GRAPHQL_COMPLEXITY_PING_HISTORY:5}
    # Simple operations
    device: ${GRAPHQL_COMPLEXITY_DEVICE:1}
    device-monitoring: ${GRAPHQL_COMPLEXITY_DEVICE_MONITORING:2}
```

#### **`application-circuitbreaker.yml`** - Circuit Breaker Configuration
```yaml
resilience4j:
  circuitbreaker:
    configs:
      default:
        failure-rate-threshold: ${CB_DEFAULT_FAILURE_THRESHOLD:50}
        minimum-number-of-calls: ${CB_DEFAULT_MIN_CALLS:10}
        wait-duration-in-open-state: ${CB_DEFAULT_WAIT_DURATION:30s}
      
      critical-service:
        failure-rate-threshold: ${CB_CRITICAL_FAILURE_THRESHOLD:40}
        minimum-number-of-calls: ${CB_CRITICAL_MIN_CALLS:5}
        wait-duration-in-open-state: ${CB_CRITICAL_WAIT_DURATION:20s}
      
      resilient-service:
        failure-rate-threshold: ${CB_RESILIENT_FAILURE_THRESHOLD:60}
        minimum-number-of-calls: ${CB_RESILIENT_MIN_CALLS:15}
        wait-duration-in-open-state: ${CB_RESILIENT_WAIT_DURATION:45s}
    
    instances:
      device-service:
        base-config: critical-service
      ping-service:
        base-config: default
      alert-service:
        base-config: resilient-service
```

#### **`application-dev.yml`** - Development Profile
```yaml
# Activated with SPRING_PROFILES_ACTIVE=dev
logging:
  level:
    io.thatworked.support.gateway: DEBUG
    org.springframework.cloud.openfeign: DEBUG
    io.github.resilience4j: DEBUG

# More aggressive circuit breaker settings for dev testing
resilience4j:
  circuitbreaker:
    configs:
      default:
        slidingWindowSize: 5          # Smaller window for faster testing
        minimumNumberOfCalls: 3       # Trigger circuit breaker faster
        waitDurationInOpenState: 10s  # Shorter wait time for dev
```

###  Environment Variables

All configuration values can be overridden with environment variables:

```bash
# GraphQL Configuration
export GRAPHQL_MAX_QUERY_COMPLEXITY=200
export GRAPHQL_DEFAULT_PAGE_SIZE=50
export GRAPHQL_CLIENT_RECENT_ALERTS_LIMIT=10

# Kafka Configuration
export KAFKA_BOOTSTRAP_SERVERS=localhost:9092
export KAFKA_CONSUMER_GROUP_ID=api-gateway-prod
export KAFKA_TOPIC_ALERT_LIFECYCLE=prod-alert-events

# Circuit Breaker Configuration
export CB_DEFAULT_FAILURE_THRESHOLD=30
export CB_CRITICAL_WAIT_DURATION=15s

# Service Discovery
export CONSUL_HOST=consul.example.com
export CONSUL_PORT=8500
```

##  API Endpoints

### GraphQL Endpoints
- **POST** `/graphql` - Main GraphQL endpoint
- **GET** `/graphiql` - Interactive GraphQL IDE (dev only)
- **WebSocket** `/graphql-ws` - Real-time subscriptions

### Health & Monitoring
- **GET** `/actuator/health` - Comprehensive health check
- **GET** `/actuator/info` - Application information
- **GET** `/actuator/metrics` - Application metrics
- **GET** `/actuator/circuitbreakers` - Circuit breaker status
- **GET** `/actuator/circuitbreakerevents` - Circuit breaker events

### Proxy Endpoints
- **GET** `/api/search` - Global search proxy
- **GET** `/api/search/{type}` - Type-specific search proxy

##  GraphQL Schema & Operations

###  Query Examples

#### Device Management
```graphql
# Get all devices with pagination
query GetDevices($page: Int, $size: Int) {
  devices(page: $page, size: $size) {
    uuid
    name
    ipAddress
    deviceType
    make
    model
    site
    os
    osType
    lastSeen
  }
}

# Advanced device search
query SearchDevices($criteria: DeviceSearchCriteriaInput!) {
  searchDevices(criteria: $criteria) {
    devices {
      uuid
      name
      ipAddress
      deviceType
      site
      os
    }
    totalElements
    totalPages
    hasNext
    hasPrevious
  }
}

# Device monitoring data
query DeviceMonitoring($deviceId: String!) {
  deviceMonitoring(deviceId: $deviceId) {
    device {
      uuid
      name
      ipAddress
      deviceType
    }
    currentStatus {
      isOnline
      responseTimeMs
      lastPing
    }
    statistics {
      uptime
      avgResponseTime
      packetLoss
      totalPings
      successfulPings
    }
    recentAlerts(limit: 5) {
      id
      message
      severity
      timestamp
      status
    }
    pingHistory(limit: 50) {
      timestamp
      responseTimeMs
      success
    }
  }
}
```

#### Dashboard & Monitoring
```graphql
# Monitoring dashboard overview
query MonitoringDashboard {
  monitoringDashboard {
    summary {
      totalDevices
      onlineDevices
      offlineDevices
      monitoredDevices
      avgResponseTime
      systemUptime
    }
    devices {
      uuid
      name
      ipAddress
      currentStatus {
        isOnline
        responseTimeMs
      }
      statistics {
        uptime
        avgResponseTime
      }
      recentAlerts {
        severity
        message
        timestamp
      }
    }
  }
}

# System health overview
query SystemHealth {
  systemHealth {
    services {
      name
      status
      responseTime
      lastCheck
    }
    kafka {
      status
      topics
    }
    database {
      status
      connections
    }
  }
}
```

#### Alerts & Notifications
```graphql
# Get alerts with filtering
query GetAlerts($status: AlertStatus, $severity: AlertSeverity, $page: Int) {
  alerts(status: $status, severity: $severity, page: $page) {
    id
    deviceId
    message
    severity
    status
    timestamp
    acknowledgedAt
    resolvedAt
  }
}

# Recent alerts for device
query RecentAlerts($deviceId: String!, $limit: Int) {
  deviceMonitoring(deviceId: $deviceId) {
    recentAlerts(limit: $limit) {
      id
      message
      severity
      timestamp
      status
    }
  }
}
```

###  Mutation Examples

#### Device Operations
```graphql
# Create a new device
mutation CreateDevice($input: DeviceInputDTO!) {
  createDevice(input: $input) {
    uuid
    name
    ipAddress
    deviceType
    make
    model
    site
  }
}

# Update device information
mutation UpdateDevice($uuid: String!, $input: DeviceInputDTO!) {
  updateDevice(uuid: $uuid, input: $input) {
    uuid
    name
    ipAddress
    deviceType
  }
}

# Delete device
mutation DeleteDevice($uuid: String!) {
  deleteDevice(uuid: $uuid)
}
```

#### Monitoring Operations
```graphql
# Start ping monitoring for a device
mutation StartMonitoring($deviceId: String!) {
  startPingMonitoring(deviceId: $deviceId) {
    deviceId
    isActive
    intervalSeconds
    targetId
  }
}

# Stop ping monitoring
mutation StopMonitoring($deviceId: String!) {
  stopPingMonitoring(deviceId: $deviceId) {
    deviceId
    isActive
  }
}

# Bulk monitoring operations
mutation StartMonitoringAll($criteria: DeviceSearchCriteriaInput) {
  startMonitoringAll(criteria: $criteria) {
    successful
    failed
    totalProcessed
    results {
      deviceId
      success
      message
    }
  }
}
```

#### Alert Management
```graphql
# Acknowledge alert
mutation AcknowledgeAlert($alertId: String!) {
  acknowledgeAlert(alertId: $alertId) {
    id
    status
    acknowledgedAt
  }
}

# Resolve alert
mutation ResolveAlert($alertId: String!) {
  resolveAlert(alertId: $alertId) {
    id
    status
    resolvedAt
  }
}
```

###  Subscription Examples

#### Real-time Monitoring
```graphql
# Real-time ping updates for a device
subscription PingUpdates($deviceId: String!) {
  pingUpdates(deviceId: $deviceId) {
    deviceId
    timestamp
    responseTimeMs
    success
    packetLoss
  }
}

# Device status changes
subscription DeviceStatusUpdates($deviceId: String) {
  deviceStatusUpdates(deviceId: $deviceId) {
    deviceId
    status
    timestamp
    metadata
  }
}
```

#### Alert Notifications
```graphql
# Real-time alert stream
subscription AlertStream {
  alertStream {
    id
    deviceId
    message
    severity
    timestamp
    status
  }
}

# Device-specific alerts
subscription DeviceAlerts($deviceId: String!) {
  deviceAlerts(deviceId: $deviceId) {
    id
    message
    severity
    timestamp
  }
}
```

##  Service Architecture

###  Feign Clients with Circuit Breakers

All external service communication uses Feign clients with circuit breaker protection:

```java
@FeignClient(name = "device-service", 
             path = "/api/devices", 
             fallbackFactory = DeviceServiceClientFallbackFactory.class)
public interface DeviceServiceClient {
    @GetMapping
    PageResponseDTO<DeviceDTO> getAllDevices();
    
    @GetMapping("/{uuid}")
    DeviceDTO getDeviceByUuid(@PathVariable UUID uuid);
    
    @PostMapping
    DeviceDTO createDevice(@RequestBody DeviceInputDTO input);
}
```

#### Circuit Breaker Configuration
- **Critical Services** (device-service, search-service): 40% failure threshold, 20s wait
- **Default Services** (ping-service, report-service): 50% failure threshold, 30s wait  
- **Resilient Services** (alert-service): 60% failure threshold, 45s wait

###  DataLoader Pattern

Implements Facebook's DataLoader pattern to solve N+1 query problems:

```java
@Component
public class AlertsBatchLoader {
    public CompletableFuture<Map<UUID, List<AlertDTO>>> loadAlerts(Set<UUID> deviceIds) {
        // Batch load alerts for multiple devices in single call
        return CompletableFuture.supplyAsync(() -> {
            return deviceIds.stream()
                .collect(Collectors.toMap(
                    deviceId -> deviceId,
                    deviceId -> alertServiceClient.getRecentAlertsByDevice(
                        deviceId, graphQLProperties.getClientDefaults().getRecentAlertsLimit())
                ));
        });
    }
}
```

###  AOP (Aspect-Oriented Programming)

Cross-cutting concerns implemented with Spring AOP:

#### Service Logging Aspect
```java
@Aspect
@Component
public class ServiceLoggingAspect {
    
    @Around("@annotation(LogExecution)")
    public Object logExecution(ProceedingJoinPoint joinPoint, LogExecution annotation) {
        String operation = annotation.value();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        logger.with("operation", operation)
              .with("class", className)
              .info("Starting operation");
              
        // Execute method with timing and error handling
    }
}
```

#### Error Handling Aspect
```java
@Aspect
@Component  
public class ErrorHandlingAspect {
    
    @Around("@annotation(DefaultOnError)")
    public Object handleError(ProceedingJoinPoint joinPoint, DefaultOnError annotation) {
        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            logger.error("Operation failed, returning default value", e);
            return getDefaultValue(annotation.value());
        }
    }
}
```

###  Event Streaming

Kafka-based event streaming for real-time updates:

#### Alert Event Consumer
```java
@KafkaListener(topics = "${spring.kafka.topics.alert-lifecycle-events}", 
               groupId = "${spring.kafka.consumer.group-id}")
public void handleAlertLifecycleEvent(@Payload String message) {
    Map<String, Object> eventMap = objectMapper.readValue(message, Map.class);
    AlertDTO alert = eventTransformationService.transformAlertEvent(eventMap);
    alertStreamingService.broadcastAlert(alert);
}
```

#### Monitoring Event Consumer
```java
@KafkaListener(topics = "${spring.kafka.topics.ping-monitoring-events}", 
               groupId = "${spring.kafka.consumer.group-id}-monitoring")
public void handleMonitoringEvent(@Payload String message) {
    Map<String, Object> eventMap = objectMapper.readValue(message, Map.class);
    String eventType = (String) eventMap.get("eventType");
    UUID deviceId = UUID.fromString((String) eventMap.get("deviceId"));
    
    switch (eventType) {
        case "MONITORING_STARTED":
            deviceStatusStreamService.publishStatusUpdate(deviceId, "NOT_MONITORED", "MONITORING");
            break;
        case "DEVICE_DOWN":
            // Handle device down events
            break;
    }
}
```

##  Testing & Development

###  GraphiQL Interface

Access the interactive GraphQL IDE at `http://localhost:8080/graphiql` for:
- **Schema Exploration** - Browse all available types, queries, mutations
- **Query Development** - Write and test queries with autocomplete
- **Real-time Testing** - Test subscriptions and real-time features
- **Documentation** - Inline documentation for all schema elements

###  Health Checks

```bash
# Basic health check
curl http://localhost:8080/actuator/health

# Detailed health information
curl http://localhost:8080/actuator/health | jq .

# Circuit breaker status
curl http://localhost:8080/actuator/circuitbreakers | jq .

# Application metrics
curl http://localhost:8080/actuator/metrics
```

###  Testing Scenarios

#### Basic Functionality
```bash
# Test device query
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query": "{ devices { uuid name ipAddress } }"}'

# Test device search
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{
    "query": "query SearchDevices($criteria: DeviceSearchCriteriaInput!) { searchDevices(criteria: $criteria) { devices { uuid name } totalElements } }",
    "variables": {
      "criteria": {
        "name": "test",
        "page": 0,
        "size": 5
      }
    }
  }'
```

#### Circuit Breaker Testing
```bash
# Check circuit breaker states
curl http://localhost:8080/actuator/circuitbreakers

# Monitor circuit breaker events
curl http://localhost:8080/actuator/circuitbreakerevents
```

#### Configuration Testing
```bash
# Test with development profile
SPRING_PROFILES_ACTIVE=dev ./gradlew bootRun

# Test with custom configuration
GRAPHQL_MAX_QUERY_COMPLEXITY=50 ./gradlew bootRun
```

###  Development Profiles

#### Default Profile
- Production-ready configuration
- INFO level logging
- Standard circuit breaker thresholds

#### Development Profile (`dev`)
```bash
SPRING_PROFILES_ACTIVE=dev ./gradlew bootRun
```
- DEBUG level logging for troubleshooting
- More aggressive circuit breaker settings for faster testing
- Enhanced error details in responses

#### Testing Profile (`test`)
- Optimized for integration testing
- Faster timeouts and smaller batch sizes
- Mock service endpoints

##  Performance & Monitoring

###  Key Metrics

#### GraphQL Metrics
- Query execution time
- Query complexity scores
- Field fetch performance
- Subscription connection count

#### Circuit Breaker Metrics
- Failure rates per service
- Circuit state transitions
- Recovery times
- Fallback execution counts

#### Service Communication Metrics
- Request/response times per service
- Success/failure rates
- Timeout occurrences
- Retry attempt counts

###  Performance Optimization

#### Query Optimization
1. **DataLoader Batching** - Prevents N+1 queries
2. **Parallel Service Calls** - CompletableFuture for concurrent operations
3. **Field-level Lazy Loading** - @SchemaMapping for expensive fields
4. **Query Complexity Analysis** - Prevents expensive queries

#### Caching Strategy
1. **Service-level Caching** - Cache frequently accessed data
2. **DataLoader Caching** - Per-request cache for batch operations
3. **GraphQL Field Caching** - Cache expensive field resolutions

#### Connection Management
1. **Connection Pooling** - Optimized HTTP client pools
2. **Keep-alive Connections** - Persistent connections to services
3. **Timeout Management** - Service-specific timeout configurations

##  Error Handling & Resilience

###  Circuit Breaker Patterns

#### Service-Specific Configuration
- **Critical Services**: Lower thresholds, faster recovery
- **Resilient Services**: Higher thresholds, longer recovery
- **Default Services**: Balanced configuration

#### Fallback Strategies
1. **Cached Data**: Return last known good data
2. **Default Values**: Provide sensible defaults
3. **Partial Results**: Return available data with warnings
4. **Error Messages**: User-friendly error descriptions

###  Error Categories

#### Client Errors (4xx)
- Validation errors with field-specific messages
- Authentication/authorization failures
- Malformed query syntax errors

#### Service Errors (5xx)
- Circuit breaker open states
- Service timeout errors
- Database connection failures

#### GraphQL Errors
- Query complexity exceeded
- Field resolution failures
- Type validation errors

##  Development Guidelines

###  Adding New Features

#### 1. New GraphQL Operations
```java
@Controller
public class NewFeatureResolver extends BaseResolver {
    
    @QueryMapping
    public FeatureDTO getFeature(@Argument String id) {
        return executeWithLogging("getFeature",
            Map.of("id", id),
            () -> featureService.getById(id)
        );
    }
}
```

#### 2. New Service Clients
```java
@FeignClient(name = "new-service", 
             fallbackFactory = NewServiceClientFallbackFactory.class)
public interface NewServiceClient {
    @GetMapping("/{id}")
    FeatureDTO getFeature(@PathVariable String id);
}
```

#### 3. New Configuration Properties
```yaml
# In graphql.yml
feature:
  default-limit: ${FEATURE_DEFAULT_LIMIT:10}
  cache-ttl: ${FEATURE_CACHE_TTL:300}
```

###  Code Style Guidelines

#### Service Classes
- Use `@LogExecution` for important operations
- Use `@DefaultOnError` for graceful degradation
- Implement proper error handling with try-catch
- Use structured logging with correlation IDs

#### GraphQL Resolvers
- Extend `BaseResolver` for common functionality
- Use `executeWithLogging` for operation tracking
- Implement proper argument validation
- Use DataLoaders for batch operations

#### Configuration
- All values must be externalized with environment variables
- Use meaningful default values
- Group related configuration in dedicated files
- Document all configuration options

##  Testing

This service includes a comprehensive test suite designed for academic requirements with focus on DTO validation and application structure testing.

### Test Suite Overview
- **Total Tests**: 51 tests across 7 test classes
- **Success Rate**: 100% (51/51 passing)
- **Execution Time**: <1 second
- **Framework**: JUnit 5 with AssertJ assertions

### Test Classes
1. **ApplicationTest** (3 tests) - Validates main application class structure
2. **ConnectionTest** (10 tests) - Tests GraphQL Connection DTO (Relay-style pagination)
3. **DeviceDTOTest** (10 tests) - Tests device data transfer objects
4. **EdgeTest** (8 tests) - Tests GraphQL Edge DTO for cursor-based pagination
5. **PageInfoTest** (8 tests) - Tests GraphQL PageInfo DTO for pagination metadata
6. **PageResponseTest** (9 tests) - Tests service response DTOs with fallback support
7. **RoleDTOTest** (10 tests) - Tests role assignment DTOs

### Running Tests
```bash
# Run all tests
./gradlew test

# View test results
open build/reports/tests/test/index.html
```

### Test Documentation
- **[docs/TEST_CASES.md](docs/TEST_CASES.md)** - Detailed test case documentation with GraphQL and DTO coverage analysis
- **[docs/TEST_RESULTS.md](docs/TEST_RESULTS.md)** - Complete test execution results and performance metrics

### Manual API Testing
```bash
# Test GraphQL query
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query": "{ devices { uuid name ipAddress } }"}'
```

### Testing Guidelines

#### Unit Tests
- Test service logic with mocked dependencies
- Test GraphQL resolvers with mocked clients
- Test configuration loading and validation

#### Integration Tests
- Test GraphQL schema and operations
- Test service client communication
- Test circuit breaker behavior
- Test event streaming functionality

#### Performance Tests
- Load test GraphQL queries
- Test subscription scalability
- Validate circuit breaker thresholds
- Monitor memory and CPU usage

##  Deployment

###  Production Deployment

#### Environment Variables
```bash
# Application Configuration
export SPRING_PROFILES_ACTIVE=prod
export SERVER_PORT=8080

# Service Discovery
export CONSUL_HOST=consul.prod.example.com
export CONSUL_PORT=8500

# Database Configuration
export DB_HOST=postgres.prod.example.com
export DB_PORT=5432
export DB_NAME=networkping
export DB_USERNAME=api_gateway
export DB_PASSWORD=${DB_PASSWORD}

# Kafka Configuration
export KAFKA_BOOTSTRAP_SERVERS=kafka1.prod.example.com:9092,kafka2.prod.example.com:9092
export KAFKA_CONSUMER_GROUP_ID=api-gateway-prod
export KAFKA_TOPIC_ALERT_LIFECYCLE=prod-alert-lifecycle-events
export KAFKA_TOPIC_PING_MONITORING=prod-ping-monitoring-events

# Circuit Breaker Tuning
export CB_DEFAULT_FAILURE_THRESHOLD=30
export CB_CRITICAL_FAILURE_THRESHOLD=20
export CB_DEFAULT_WAIT_DURATION=60s

# GraphQL Configuration
export GRAPHQL_MAX_QUERY_COMPLEXITY=200
export GRAPHQL_DEFAULT_PAGE_SIZE=50
export GRAPHQL_MAX_PAGE_SIZE=500

# Performance Tuning
export JVM_OPTS="-Xmx2g -Xms1g -XX:+UseG1GC"
```

#### Docker Production
```dockerfile
FROM eclipse-temurin:21-jre-jammy

# Add application user
RUN addgroup --system spring && adduser --system spring --ingroup spring

# Install dependencies
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copy application
WORKDIR /app
COPY build/libs/api-gateway.jar app.jar
COPY docker/docker-entrypoint.sh /docker-entrypoint.sh

# Set permissions
RUN chmod +x /docker-entrypoint.sh
RUN chown spring:spring /app/app.jar

USER spring:spring

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["/docker-entrypoint.sh"]
```

###  Continuous Integration

#### Build Pipeline
```yaml
# .github/workflows/build.yml
name: Build and Test
on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
      
      - name: Cache Gradle packages
        uses: actions/cache@v3
        with:
          path: ~/.gradle/caches
          key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle') }}
      
      - name: Run tests
        run: ./gradlew test
      
      - name: Build application
        run: ./gradlew build
      
      - name: Upload test results
        uses: actions/upload-artifact@v3
        if: always()
        with:
          name: test-results
          path: build/reports/tests/
```

##  Additional Resources

###  Documentation
- [GraphQL Specification](https://spec.graphql.org/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Resilience4j User Guide](https://resilience4j.readme.io/)
- [Spring Cloud OpenFeign](https://spring.io/projects/spring-cloud-openfeign)

###  Tools & Libraries
- **GraphQL Java** - GraphQL implementation
- **Spring Boot** - Application framework
- **Spring Cloud OpenFeign** - Service communication
- **Resilience4j** - Circuit breaker implementation
- **Apache Kafka** - Event streaming
- **Consul** - Service discovery
- **Micrometer** - Metrics collection
- **SLF4J + Logback** - Structured logging

###  Related Services
- [Device Service](../device-service/README.md) - Device inventory management
- [Ping Service](../ping-service/README.md) - Network monitoring operations
- [Alert Service](../alert-service/README.md) - Alert lifecycle management
- [Report Service](../report-service/README.md) - Report generation
- [Frontend Application](../front-end/README.md) - React web interface

##  License

Copyright © 2025 Kyle Mielke. All rights reserved.

This software is proprietary and confidential. Unauthorized copying, distribution, or use is strictly prohibited.