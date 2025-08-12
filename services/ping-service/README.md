# Ping Service

A high-performance microservice responsible for ICMP ping monitoring in the NetworkPing Monitor system. This service executes concurrent ping operations using Java virtual threads, stores time-series data in TimescaleDB, and publishes monitoring events for real-time alerting.

## Features

- **Concurrent Ping Execution** - Virtual threads for efficient concurrent monitoring
- **Queue-Based Architecture** - Scalable ping task queue management
- **TimescaleDB Storage** - Optimized time-series database for ping results
- **Alert Threshold Management** - Configurable failure/recovery thresholds
- **Circuit Breaker Pattern** - Resilient failure handling with automatic recovery
- **Event Publishing** - Real-time monitoring events via Kafka
- **Cascade Delete Support** - Automatic cleanup when devices are deleted
- **Dynamic Target Management** - Add/remove monitoring targets at runtime
- **Health Monitoring** - Spring Boot Actuator endpoints
- **Retry Logic** - Exponential backoff for transient failures

## Architecture

The Ping Service uses a queue-based architecture with virtual threads for efficient concurrent execution. It subscribes to device events to maintain its monitoring targets and publishes monitoring events for downstream services.

### Core Components

```
ping-service/
├── domain/                 # Business logic
│   ├── model/             # Domain entities
│   │   ├── PingTarget
│   │   ├── PingResult
│   │   └── AlertState
│   ├── service/           # Domain services
│   │   ├── PingExecutor
│   │   ├── AlertStateService
│   │   └── ThresholdManager
│   └── port/              # Port interfaces
├── application/           # Application services
│   ├── VirtualThreadPingService
│   ├── MonitoringOrchestrator
│   └── usecase/
│       ├── StartMonitoringUseCase
│       └── StopMonitoringUseCase
├── infrastructure/        # External integrations
│   ├── persistence/       # TimescaleDB repositories
│   ├── event/            # Kafka publishers/consumers
│   ├── ping/             # ICMP implementation
│   └── config/           # Spring configuration
└── api/                   # REST endpoints
    ├── controller/
    └── dto/
```

## API Endpoints

### Monitoring Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/ping/start/{deviceId}` | Start monitoring a device |
| POST | `/api/v1/ping/stop/{deviceId}` | Stop monitoring a device |
| GET | `/api/v1/ping/status/{deviceId}` | Get current monitoring status |
| GET | `/api/v1/ping/history/{deviceId}` | Get ping history |
| GET | `/api/v1/ping/statistics/{deviceId}` | Get ping statistics |
| GET | `/api/v1/ping/targets` | List all active targets |

### Health and Monitoring

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/actuator/health` | Health check endpoint |
| GET | `/actuator/metrics` | Service metrics |
| GET | `/actuator/info` | Service information |

## Configuration

### Environment Variables

```env
# Service Configuration
SERVER_PORT=8082
SPRING_APPLICATION_NAME=ping-service

# Database Configuration (TimescaleDB)
SPRING_DATASOURCE_URL=jdbc:postgresql://timescaledb:5432/support_ping_db
SPRING_DATASOURCE_USERNAME=support_ping_user
SPRING_DATASOURCE_PASSWORD=your_secure_password_here
SPRING_JPA_HIBERNATE_DDL_AUTO=update

# Ping Configuration
PING_EXECUTOR_PING_INTERVAL=5
PING_EXECUTOR_PING_TIMEOUT=3000
PING_EXECUTOR_THREAD_POOL_SIZE=100
PING_EXECUTOR_USE_VIRTUAL_THREADS=true

# Alert Thresholds
PING_ALERTING_ENABLED=true
PING_ALERTING_FAILURE_THRESHOLD=3
PING_ALERTING_RECOVERY_THRESHOLD=2

# Kafka Configuration
SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092
KAFKA_TOPIC_DEVICE_EVENTS=device-events
KAFKA_TOPIC_PING_MONITORING_EVENTS=ping-monitoring-events

# Consul Configuration
SPRING_CLOUD_CONSUL_HOST=consul
SPRING_CLOUD_CONSUL_PORT=8500
SPRING_CLOUD_CONSUL_DISCOVERY_ENABLED=true
```

## Events

### Consumed Events

The service subscribes to device events:

```json
{
  "eventType": "DEVICE_CREATED",
  "deviceId": "uuid",
  "device": {
    "ipAddress": "192.168.1.1",
    "name": "Router"
  }
}
```

```json
{
  "eventType": "DEVICE_DELETED",
  "deviceId": "uuid"
}
```

### Published Events

#### Device Down Event
```json
{
  "eventType": "DEVICE_DOWN",
  "deviceId": "uuid",
  "timestamp": "2025-01-01T00:00:00Z",
  "consecutiveFailures": 3,
  "lastError": "Request timeout"
}
```

#### Device Recovery Event
```json
{
  "eventType": "DEVICE_RECOVERED",
  "deviceId": "uuid",
  "timestamp": "2025-01-01T00:00:00Z",
  "downtime": 300000,
  "responseTime": 25
}
```

#### Ping Result Event
```json
{
  "eventType": "PING_RESULT",
  "deviceId": "uuid",
  "timestamp": "2025-01-01T00:00:00Z",
  "success": true,
  "responseTime": 25,
  "ipAddress": "192.168.1.1"
}
```

## Database Schema

### TimescaleDB Tables

```sql
-- Ping targets table
CREATE TABLE ping_targets (
    device_id UUID PRIMARY KEY,
    ip_address VARCHAR(45) NOT NULL,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Ping results hypertable
CREATE TABLE ping_results (
    time TIMESTAMP NOT NULL,
    device_id UUID NOT NULL,
    success BOOLEAN NOT NULL,
    response_time_ms INTEGER,
    error_message TEXT,
    PRIMARY KEY (time, device_id)
);

-- Convert to TimescaleDB hypertable
SELECT create_hypertable('ping_results', 'time');

-- Create indexes for performance
CREATE INDEX idx_ping_results_device_time ON ping_results (device_id, time DESC);
```

## Alert Thresholds

The service implements intelligent alerting based on configurable thresholds:

- **Down Alert**: Triggered after N consecutive failures (default: 3)
- **Recovery Alert**: Triggered after N consecutive successes (default: 2)
- **Flapping Detection**: Prevents alert storms from unstable devices

### Threshold Configuration

```yaml
ping:
  alerting:
    enabled: true
    failure-threshold: 3    # Failures before DOWN alert
    recovery-threshold: 2   # Successes before RECOVERY alert
    flapping-window: 300    # Seconds to detect flapping
```

## Performance Optimization

### Virtual Threads

The service uses Java 21 virtual threads for efficient concurrent execution:

```java
// Handles thousands of concurrent pings efficiently
ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
```

### Circuit Breaker

Implements circuit breaker pattern for resilience:

- **Closed**: Normal operation
- **Open**: Skip pings for consistently failing devices
- **Half-Open**: Test recovery with limited pings

## Building and Running

### Local Development

```bash
# Build the service
./gradlew clean build

# Run tests
./gradlew test

# Run locally
./gradlew bootRun
```

### Docker Deployment

```bash
# Build Docker image
docker build -t ping-service:latest -f docker/Dockerfile .

# Run with Docker Compose
docker compose up ping-service
```

## Testing

Comprehensive test coverage includes:

- **Unit Tests** - Domain logic and service tests
- **Integration Tests** - TimescaleDB and Kafka integration
- **Performance Tests** - Virtual thread performance validation

```bash
# Run all tests
./gradlew test

# Run with coverage
./gradlew test jacocoTestReport
```

## Monitoring

### Metrics

Key metrics exposed via Actuator:

- `ping.execution.time` - Ping execution duration
- `ping.success.rate` - Success rate percentage
- `ping.active.targets` - Number of active targets
- `ping.queue.size` - Pending ping tasks

### Health Checks

- Database connectivity
- Kafka connectivity
- Thread pool status
- Queue capacity

## Dependencies

- Spring Boot 3.x
- Spring Data JPA
- TimescaleDB/PostgreSQL Driver
- Spring Kafka
- Spring Cloud Consul
- Spring Boot Actuator
- Apache Commons Net (ICMP)

## License

Copyright © 2025 Kyle Mielke. All rights reserved.