# Alert Service

A microservice responsible for alert management in the NetworkPing Monitor system. This service manages alert lifecycle, thresholds, and publishes alert events for notification delivery. It implements intelligent alerting logic to prevent alert fatigue while ensuring critical issues are promptly communicated.

## Features

- **Alert Lifecycle Management** - Created, acknowledged, resolved states
- **Threshold-Based Alerting** - Configurable failure and recovery thresholds
- **Alert Deduplication** - Prevents duplicate alerts for same issues
- **Cascade Delete Support** - Automatic cleanup when devices are deleted
- **Event-Driven Architecture** - Consumes monitoring events, publishes alert events
- **Alert History** - Complete audit trail of all alerts
- **Alert Suppression** - Time-based and condition-based suppression
- **Alert Correlation** - Groups related alerts together
- **PostgreSQL Storage** - Reliable persistent storage with automatic initialization
- **Health Monitoring** - Spring Boot Actuator endpoints

## Architecture

The Alert Service acts as the central alerting hub, consuming monitoring events from various sources and determining when alerts should be created, updated, or resolved. It publishes alert lifecycle events for the notification service to handle delivery.

### Core Components

```
alert-service/
├── domain/                 # Business logic
│   ├── model/             # Domain entities
│   │   ├── Alert
│   │   ├── AlertState
│   │   └── AlertSeverity
│   ├── service/           # Domain services
│   │   ├── AlertLifecycleService
│   │   ├── ThresholdEvaluator
│   │   └── AlertCorrelator
│   └── port/              # Port interfaces
├── application/           # Application services
│   ├── AlertManagementService
│   ├── AlertProcessingService
│   └── usecase/
│       ├── CreateAlertUseCase
│       ├── AcknowledgeAlertUseCase
│       └── ResolveAlertUseCase
├── infrastructure/        # External integrations
│   ├── persistence/       # PostgreSQL repositories
│   ├── event/            # Kafka publishers/consumers
│   └── config/           # Spring configuration
└── api/                   # REST endpoints
    ├── controller/
    └── dto/
```

## API Endpoints

### Alert Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/alerts` | Get all alerts with pagination |
| GET | `/api/v1/alerts/{id}` | Get alert by ID |
| GET | `/api/v1/alerts/device/{deviceId}` | Get alerts for device |
| POST | `/api/v1/alerts/{id}/acknowledge` | Acknowledge an alert |
| POST | `/api/v1/alerts/{id}/resolve` | Manually resolve an alert |
| GET | `/api/v1/alerts/active` | Get all active alerts |
| GET | `/api/v1/alerts/history` | Get alert history |

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
SERVER_PORT=8084
SPRING_APPLICATION_NAME=alert-service

# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://timescaledb:5432/support_alert_db
SPRING_DATASOURCE_USERNAME=support_alert_user
SPRING_DATASOURCE_PASSWORD=your_secure_password_here
SPRING_JPA_HIBERNATE_DDL_AUTO=update

# Alert Configuration
ALERT_DEFAULT_SEVERITY=MEDIUM
ALERT_AUTO_RESOLVE_ENABLED=true
ALERT_HISTORY_RETENTION_DAYS=90

# Threshold Configuration
ALERT_FAILURE_THRESHOLD=3
ALERT_RECOVERY_THRESHOLD=2
ALERT_FLAPPING_WINDOW_SECONDS=300

# Kafka Configuration
SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092
KAFKA_TOPIC_PING_MONITORING_EVENTS=ping-monitoring-events
KAFKA_TOPIC_DEVICE_EVENTS=device-events
KAFKA_TOPIC_ALERT_LIFECYCLE_EVENTS=alert-lifecycle-events

# Consul Configuration
SPRING_CLOUD_CONSUL_HOST=consul
SPRING_CLOUD_CONSUL_PORT=8500
SPRING_CLOUD_CONSUL_DISCOVERY_ENABLED=true
```

## Events

### Consumed Events

#### Monitoring Events
```json
{
  "eventType": "DEVICE_DOWN",
  "deviceId": "uuid",
  "timestamp": "2025-01-01T00:00:00Z",
  "consecutiveFailures": 3
}
```

#### Device Events
```json
{
  "eventType": "DEVICE_DELETED",
  "deviceId": "uuid",
  "timestamp": "2025-01-01T00:00:00Z"
}
```

### Published Events

#### Alert Created Event
```json
{
  "eventType": "ALERT_CREATED",
  "alertId": "uuid",
  "deviceId": "uuid",
  "severity": "HIGH",
  "title": "Device Down",
  "description": "Device has failed to respond to 3 consecutive pings",
  "timestamp": "2025-01-01T00:00:00Z"
}
```

#### Alert Acknowledged Event
```json
{
  "eventType": "ALERT_ACKNOWLEDGED",
  "alertId": "uuid",
  "acknowledgedBy": "user",
  "timestamp": "2025-01-01T00:00:00Z"
}
```

#### Alert Resolved Event
```json
{
  "eventType": "ALERT_RESOLVED",
  "alertId": "uuid",
  "resolvedBy": "system",
  "resolution": "Device recovered",
  "timestamp": "2025-01-01T00:00:00Z"
}
```

## Database Schema

```sql
CREATE TABLE alerts (
    id UUID PRIMARY KEY,
    device_id UUID NOT NULL,
    alert_type VARCHAR(50) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    state VARCHAR(20) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL,
    acknowledged_at TIMESTAMP,
    acknowledged_by VARCHAR(100),
    resolved_at TIMESTAMP,
    resolved_by VARCHAR(100),
    resolution TEXT,
    metadata JSONB
);

CREATE INDEX idx_alerts_device_id ON alerts(device_id);
CREATE INDEX idx_alerts_state ON alerts(state);
CREATE INDEX idx_alerts_created_at ON alerts(created_at DESC);
CREATE INDEX idx_alerts_severity ON alerts(severity);
```

## Alert States

The service manages alerts through the following states:

1. **CREATED** - Initial state when alert is generated
2. **ACKNOWLEDGED** - Alert has been seen by an operator
3. **RESOLVED** - Issue has been resolved
4. **SUPPRESSED** - Alert is temporarily suppressed

### State Transitions

```
CREATED → ACKNOWLEDGED → RESOLVED
   ↓           ↓
SUPPRESSED  SUPPRESSED
```

## Alert Severities

Alerts are classified into severity levels:

- **CRITICAL** - Immediate action required
- **HIGH** - Significant issue requiring attention
- **MEDIUM** - Notable issue that should be investigated
- **LOW** - Informational alert
- **INFO** - System notification

## Alert Correlation

The service implements intelligent alert correlation:

- Groups related alerts together
- Prevents alert storms
- Identifies root cause alerts
- Suppresses downstream alerts

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
docker build -t alert-service:latest -f docker/Dockerfile .

# Run with Docker Compose
docker compose up alert-service
```

## Testing

Comprehensive test coverage includes:

- **Unit Tests** - Domain logic and service tests
- **Integration Tests** - Database and Kafka integration
- **Contract Tests** - API contract validation

```bash
# Run all tests
./gradlew test

# Run with coverage
./gradlew test jacocoTestReport
```

## Monitoring

### Metrics

Key metrics exposed via Actuator:

- `alerts.created.total` - Total alerts created
- `alerts.active.count` - Current active alerts
- `alerts.resolution.time` - Average resolution time
- `alerts.severity.distribution` - Distribution by severity

### Health Checks

- Database connectivity
- Kafka connectivity
- Alert processing status

## Dependencies

- Spring Boot 3.x
- Spring Data JPA
- PostgreSQL Driver
- Spring Kafka
- Spring Cloud Consul
- Spring Boot Actuator
- Jackson (JSON processing)

## License

Copyright © 2025 Kyle Mielke. All rights reserved.