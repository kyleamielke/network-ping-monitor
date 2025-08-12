# Notification Service

A microservice responsible for sending notifications in the NetworkPing Monitor system. This service processes alert lifecycle events from Kafka and sends notifications through various channels (currently email, with extensibility for Slack, Teams, SMS). Built with Clean Architecture/Hexagonal Architecture principles following Domain-Driven Design patterns.

## Features

- **Event-Driven Architecture** - Kafka consumer for alert lifecycle events
- **Multi-Channel Support** - Extensible notification channel architecture
- **Email Notifications** - SMTP-based email delivery with HTML templates
- **Template Engine** - Thymeleaf-based HTML email templates
- **Professional Templates** - Device down and recovery alert templates
- **Notification History** - Track all sent notifications with results
- **Error Handling** - Comprehensive error handling with domain exceptions
- **Clean Architecture** - Hexagonal architecture with ports and adapters
- **Domain-Driven Design** - Rich domain models with business logic
- **PostgreSQL Storage** - Persistent notification history and results with automatic initialization

## Architecture

The Notification Service follows Clean Architecture (Hexagonal Architecture) principles with a strong focus on Domain-Driven Design. It operates as a pure event consumer, responding to device state changes published by other services without maintaining device state.

### Project Structure
```
notification-service/
├── api/                    # REST API layer (Presentation)
│   ├── controller/        # REST endpoints for notification management
│   ├── dto/              # Data Transfer Objects
│   ├── mapper/           # DTO-Domain mapping
│   └── exception/        # API layer exceptions and handlers
├── application/          # Application layer (Use Cases)
│   ├── dto/             # Application-level DTOs (primitives only)
│   └── usecase/         # Business use cases
│       ├── SendNotificationUseCase
│       ├── GetNotificationHistoryUseCase
│       └── GetNotificationsBySourceEventUseCase
├── domain/              # Domain layer (Core Business)
│   ├── model/          # Domain entities (pure POJOs)
│   │   ├── NotificationRequest
│   │   ├── NotificationResult
│   │   ├── NotificationChannel
│   │   └── NotificationType
│   ├── port/           # Port interfaces (abstractions)
│   │   ├── NotificationRepository
│   │   ├── NotificationSender
│   │   ├── EventPublisher
│   │   └── DomainLogger
│   ├── service/        # Domain services
│   │   └── NotificationDomainService
│   └── exception/      # Domain exceptions
│       ├── NotificationDomainException (abstract)
│       ├── InvalidNotificationRequestException
│       ├── NotificationSendException
│       ├── UnsupportedChannelException
│       ├── NotificationRepositoryException
│       └── EventPublishingException
├── infrastructure/     # Infrastructure layer (Adapters)
│   ├── adapter/       # Port implementations
│   │   ├── EmailNotificationSender
│   │   ├── NotificationRepositoryAdapter
│   │   ├── KafkaEventPublisher
│   │   └── StructuredDomainLogger
│   ├── entity/        # JPA entities
│   ├── repository/    # Spring Data repositories
│   ├── consumer/      # Kafka consumers
│   ├── mapper/        # Entity-Domain mapping
│   └── config/        # Infrastructure configuration
└── config/           # Application configuration
    ├── LoggingConfig    # Structured logging configuration
    └── properties/      # Configuration properties
```

### Key Architectural Patterns

#### Clean Architecture Principles
1. **Dependency Inversion**: Domain layer defines interfaces (ports), infrastructure implements them
2. **Framework Independence**: Domain layer has zero framework dependencies (pure Java)
3. **Testability**: Business logic isolated from external concerns
4. **UI Independence**: API layer can be replaced without affecting business logic

#### Implementation Patterns
- **Hexagonal Architecture**: Ports and Adapters pattern for external dependencies
- **Domain-Driven Design**: Rich domain models with business logic
- **Event Sourcing**: Complete notification history with results
- **Repository Pattern**: Clean separation of data access through ports
- **Event-Driven**: Consumes events and publishes notification lifecycle events
- **Use Case Pattern**: Each business operation as a separate use case class
- **Template Pattern**: Extensible notification channel architecture

#### Layer Responsibilities
- **Domain Layer**: Business logic, entities, ports (interfaces)
- **Application Layer**: Use cases, orchestration, DTO translation
- **Infrastructure Layer**: Email sending, database, Kafka, external services
- **API Layer**: HTTP handling, validation, DTO transformation

## API Endpoints

All endpoints are prefixed with `/api/notifications`

### Notification Management
- **POST** `/` - Send a notification (primarily for testing)
- **POST** `/test` - Send a test notification
- **GET** `/history` - Get notification history with time range
- **GET** `/by-event/{sourceEventId}` - Get notifications for a specific source event

### Health & Metrics
- **GET** `/actuator/health` - Health check endpoint
- **GET** `/actuator/info` - Application information

## Event Processing

### Kafka Events Consumed

#### Alert Created Events (Topic: `alert-lifecycle-events`)
```json
{
    "eventType": "ALERT_CREATED",
    "alertId": "UUID",
    "deviceId": "UUID",
    "severity": "HIGH",
    "title": "Device Down",
    "description": "Device has failed to respond to 3 consecutive pings",
    "timestamp": "ISO-8601"
}
```

#### Alert Resolved Events (Topic: `alert-lifecycle-events`)
```json
{
    "eventType": "ALERT_RESOLVED",
    "alertId": "UUID",
    "deviceId": "UUID",
    "resolvedBy": "system",
    "resolution": "Device recovered",
    "timestamp": "ISO-8601"
}
```

### Events Published

#### Notification Lifecycle Events (Topic: `notification-events`)
- `NOTIFICATION_REQUESTED` - When a notification is requested
- `NOTIFICATION_SENT` - When successfully sent
- `NOTIFICATION_FAILED` - When sending fails

## Domain Model

### Core Entities

#### NotificationRequest
```java
public class NotificationRequest {
    private UUID id;
    private NotificationType type;
    private NotificationChannel channel;
    private String recipient;
    private String subject;
    private String message;
    private Map<String, Object> metadata;
    private UUID sourceEventId;
    private Instant requestedAt;
}
```

#### NotificationResult
```java
public class NotificationResult {
    private UUID id;
    private UUID notificationRequestId;
    private boolean successful;
    private String message;
    private String errorDetails;
    private Instant sentAt;
    private String channelSpecificId;
}
```

### Domain Services

#### NotificationDomainService
- Validates notification requests
- Orchestrates notification sending
- Handles errors and creates failure results
- Publishes domain events
- Maintains notification history

## Configuration

### Environment Variables
```env
# Service Configuration
SERVER_PORT=8083
SPRING_APPLICATION_NAME=notification-service

# SMTP Configuration
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
MAIL_PROPERTIES_MAIL_SMTP_AUTH=true
MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE=true

# Notification Settings
NOTIFICATION_FROM_EMAIL=alerts@yourcompany.com
NOTIFICATION_TO_EMAIL=operations@yourcompany.com

# Kafka Configuration
SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092
SPRING_KAFKA_CONSUMER_GROUP_ID=notification-service
KAFKA_TOPIC_ALERT_LIFECYCLE_EVENTS=alert-lifecycle-events
KAFKA_TOPIC_NOTIFICATION_EVENTS=notification-events

# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://timescaledb:5432/support_notification_db
SPRING_DATASOURCE_USERNAME=support_notification_user
SPRING_DATASOURCE_PASSWORD=your_secure_password_here
SPRING_JPA_HIBERNATE_DDL_AUTO=update

# Consul Configuration
SPRING_CLOUD_CONSUL_HOST=consul
SPRING_CLOUD_CONSUL_PORT=8500
SPRING_CLOUD_CONSUL_DISCOVERY_ENABLED=true
```

### Application Properties
```yaml
spring:
  application:
    name: notification-service
  
  # Mail Configuration
  mail:
    host: ${MAIL_HOST}
    port: ${MAIL_PORT}
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
    properties:
      mail:
        smtp:
          auth: ${MAIL_PROPERTIES_MAIL_SMTP_AUTH:true}
          starttls:
            enable: ${MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE:true}
  
  # Kafka Configuration
  kafka:
    bootstrap-servers: ${SPRING_KAFKA_BOOTSTRAP_SERVERS:localhost:9092}
    consumer:
      group-id: ${SPRING_KAFKA_CONSUMER_GROUP_ID:notification-service}
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      enable-auto-commit: false
      
notification:
  email:
    from: ${NOTIFICATION_FROM_EMAIL:alerts@networkping.com}
    to: ${NOTIFICATION_TO_EMAIL:admin@networkping.com}

# Topics
kafka:
  topics:
    notification-events: notification-events
```

## Email Templates

### Device Down Alert (`device-down-alert.html`)
Professional HTML template with:
- Alert header with device name
- Device information table
- Failure details
- Last success timestamp
- Action recommendations

### Device Recovery Alert (`device-recovery-alert.html`)
Professional HTML template with:
- Recovery confirmation
- Device information
- Downtime duration
- Recovery timestamp
- Current response time

## Error Handling

### Domain Exceptions
All domain exceptions extend `NotificationDomainException` with error codes:
- `INVALID_NOTIFICATION_REQUEST` - Invalid request data
- `NOTIFICATION_SEND_FAILED` - Failed to send notification
- `UNSUPPORTED_CHANNEL` - Channel not supported or enabled
- `NOTIFICATION_REPOSITORY_ERROR` - Database operation failed
- `EVENT_PUBLISHING_ERROR` - Failed to publish events

### Infrastructure Error Translation
Infrastructure adapters catch technology-specific exceptions and translate to domain exceptions:
```java
try {
    // Database operation
} catch (DataAccessException e) {
    throw new NotificationRepositoryException("Failed to save", e);
}
```

## Development

### Prerequisites
- Java 21
- Docker & Docker Compose
- Kafka
- PostgreSQL/TimescaleDB
- SMTP server access

### Local Development
```bash
# Build
./gradlew build

# Run tests
./gradlew test

# Run locally
./gradlew bootRun

# Build Docker image
docker build -f docker/Dockerfile -t notification-service .
```

### Testing

This service includes a comprehensive test suite designed for academic requirements with focus on domain model validation and application structure testing.

#### Test Suite Overview
- **Total Tests**: 17 tests across 3 test classes
- **Success Rate**: 100% (17/17 passing)
- **Execution Time**: 0.139 seconds
- **Framework**: JUnit 5 with AssertJ assertions

#### Test Classes
1. **ApplicationTest** (3 tests) - Validates main application class structure
2. **NotificationChannelTest** (7 tests) - Tests channel enum values, display names, and enabled status
3. **NotificationTypeTest** (7 tests) - Tests notification type enum values, titles, and descriptions

#### Running Tests
```bash
# Run all tests
./gradlew test

# View test results
open build/reports/tests/test/index.html
```

#### Test Documentation
- **[docs/TEST_CASES.md](docs/TEST_CASES.md)** - Detailed test case documentation with academic coverage analysis
- **[docs/TEST_RESULTS.md](docs/TEST_RESULTS.md)** - Complete test execution results and performance metrics

#### Manual API Testing
```bash
# Health check
curl http://localhost:8083/actuator/health

# Send test notification
curl -X POST "http://localhost:8083/api/notifications/test?channel=EMAIL&recipient=test@example.com"

# Get notification history
curl "http://localhost:8083/api/notifications/history?startTime=2024-01-01T00:00:00Z&endTime=2024-12-31T23:59:59Z"
```

## Monitoring

### Health Indicators
- Application health
- Database connectivity
- Kafka consumer health
- SMTP server connectivity

### Metrics
- Notifications sent/failed count
- Processing time
- Channel-specific metrics
- Error rates by type

### Logging
Structured logging with correlation IDs:
```json
{
  "timestamp": "2024-01-01T10:00:00Z",
  "level": "INFO",
  "service": "notification-service",
  "correlationId": "abc-123",
  "operation": "sendNotification",
  "notificationId": "def-456",
  "channel": "EMAIL",
  "message": "Notification sent successfully"
}
```

## Extending the Service

### Adding New Notification Channels
1. Create new port implementation in infrastructure layer
2. Implement `NotificationSender` interface
3. Add channel to `NotificationChannel` enum
4. Add configuration properties
5. Register bean in Spring context

Example for Slack:
```java
@Component
public class SlackNotificationSender implements NotificationSender {
    @Override
    public NotificationResult send(NotificationRequest request) {
        // Slack implementation
    }
    
    @Override
    public boolean supportsChannel(String channelName) {
        return NotificationChannel.SLACK.name().equals(channelName);
    }
}
```

### Adding New Event Types
1. Add to `NotificationType` enum
2. Create event consumer method
3. Add template if needed
4. Update domain service if special handling required

## Troubleshooting

### Common Issues

#### SMTP Authentication Failed
- Enable 2-factor authentication for Gmail
- Generate App Password in Google Account settings
- Use App Password instead of regular password

#### Kafka Consumer Not Receiving Messages
- Verify Kafka broker connectivity
- Check consumer group ID
- Ensure topics exist
- Review offset management

#### Database Connection Issues
- Verify TimescaleDB is running
- Check database credentials
- Ensure database exists
- Review connection pool settings

## License

Copyright © 2025 Kyle Mielke. All rights reserved.

This software is proprietary and confidential. Unauthorized copying, distribution, or use is strictly prohibited.