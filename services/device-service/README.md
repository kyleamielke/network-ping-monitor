# Device Service

A microservice responsible for device management in the NetworkPing Monitor system. This service handles all device-related operations including CRUD operations, validation, event publishing, and cascade delete operations. Built with Clean Architecture/Hexagonal Architecture principles following Domain-Driven Design patterns.

## Features

- **Device Management** - Complete CRUD operations for network devices
- **Advanced Search** - Multi-criteria device search with pagination and sorting
- **Cascade Delete** - Automatic cleanup of related data when devices are deleted
- **Data Validation** - IP address, MAC address, and device type validation
- **Site Assignment** - Organize devices by geographical locations
- **Role Management** - Device role assignment and management
- **PostgreSQL Storage** - Production-ready relational database with automatic initialization
- **Event Publishing** - Kafka integration for device state changes and deletion events
- **Clean Architecture** - Hexagonal architecture with ports and adapters
- **Domain-Driven Design** - Rich domain models with encapsulated business logic
- **API Versioning** - RESTful API with v1 versioning support
- **Health Checks** - Spring Boot Actuator endpoints for monitoring

## Architecture

The Device Service follows Clean Architecture (Hexagonal Architecture) principles with a strong focus on Domain-Driven Design. It serves as the source of truth for all device information in the system. Other services subscribe to device events rather than directly accessing device data, maintaining loose coupling and service independence.

### Project Structure
```
device-service/
├── api/                    # REST API layer (Presentation)
│   ├── controller/        # REST endpoints handling HTTP requests
│   ├── dto/              # Data Transfer Objects
│   │   ├── request/      # Incoming request DTOs
│   │   └── response/     # Outgoing response DTOs
│   ├── mapper/           # DTO-Domain mapping (MapStruct)
│   ├── validation/       # Custom validators
│   └── exception/        # API layer exceptions
├── application/          # Application layer (Use Cases)
│   ├── service/         # Application services (orchestration)
│   └── usecase/         # Business use cases
│       ├── CreateDeviceUseCase
│       ├── UpdateDeviceUseCase
│       ├── DeleteDeviceUseCase
│       └── AssignDeviceToSiteUseCase
├── domain/              # Domain layer (Core Business)
│   ├── model/          # Domain entities (pure POJOs)
│   │   ├── DeviceDomain
│   │   ├── DeviceRoleDomain
│   │   └── DeviceStatus
│   ├── port/           # Port interfaces (abstractions)
│   │   ├── DeviceRepository
│   │   ├── EventPublisher
│   │   └── DeviceSearchPort
│   ├── service/        # Domain services
│   └── exception/      # Domain exceptions
└── infrastructure/     # Infrastructure layer (Implementations)
    ├── persistence/    # Database implementations
    │   ├── jpa/       # JPA entities and repositories
    │   └── mapper/    # Domain-Entity mapping
    ├── event/         # Event publishing (Kafka)
    ├── config/        # Configuration classes
    └── exception/     # Infrastructure exceptions
```

## API Endpoints

### Device Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/devices` | Get all devices with pagination |
| GET | `/api/v1/devices/{uuid}` | Get device by UUID |
| POST | `/api/v1/devices` | Create new device |
| PUT | `/api/v1/devices/{uuid}` | Update existing device |
| DELETE | `/api/v1/devices/{uuid}` | Delete device (triggers cascade delete) |
| GET | `/api/v1/devices/search` | Search devices with criteria |
| POST | `/api/v1/devices/{uuid}/role` | Assign role to device |
| GET | `/api/v1/devices/by-ip/{ipAddress}` | Find device by IP address |

### Health and Monitoring

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/actuator/health` | Health check endpoint |
| GET | `/actuator/info` | Service information |
| GET | `/actuator/metrics` | Service metrics |

## Configuration

### Environment Variables

Configure the service using environment variables or `.env` file:

```env
# Service Configuration
SERVER_PORT=8081
SPRING_APPLICATION_NAME=device-service

# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://timescaledb:5432/support_device_db
SPRING_DATASOURCE_USERNAME=support_device_user
SPRING_DATASOURCE_PASSWORD=your_secure_password_here
SPRING_JPA_HIBERNATE_DDL_AUTO=update

# Kafka Configuration
SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092
DEVICE_SERVICE_KAFKA_ENABLED=true
DEVICE_SERVICE_KAFKA_TOPIC_DEVICE_EVENTS=device-events

# Consul Configuration
SPRING_CLOUD_CONSUL_HOST=consul
SPRING_CLOUD_CONSUL_PORT=8500
SPRING_CLOUD_CONSUL_DISCOVERY_ENABLED=true

# API Configuration
DEVICE_API_PAGINATION_DEFAULT_SIZE=50
DEVICE_API_PAGINATION_MAX_SIZE=100
```

## Events

The Device Service publishes the following events to Kafka:

### Device Created Event
```json
{
  "eventType": "DEVICE_CREATED",
  "deviceId": "uuid",
  "timestamp": "2025-01-01T00:00:00Z",
  "device": { ... }
}
```

### Device Updated Event
```json
{
  "eventType": "DEVICE_UPDATED",
  "deviceId": "uuid",
  "timestamp": "2025-01-01T00:00:00Z",
  "device": { ... }
}
```

### Device Deleted Event
```json
{
  "eventType": "DEVICE_DELETED",
  "deviceId": "uuid",
  "timestamp": "2025-01-01T00:00:00Z"
}
```

## Database Schema

The service automatically initializes its database schema on startup:

```sql
CREATE TABLE devices (
    uuid UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    ip_address VARCHAR(45) NOT NULL UNIQUE,
    mac_address VARCHAR(17),
    device_type VARCHAR(50),
    os VARCHAR(100),
    os_type VARCHAR(50),
    location VARCHAR(255),
    site VARCHAR(100),
    description TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_devices_name ON devices(name);
CREATE INDEX idx_devices_type ON devices(device_type);
CREATE INDEX idx_devices_site ON devices(site);
```

## Building and Running

### Local Development

1. Build the service:
```bash
./gradlew clean build
```

2. Run tests:
```bash
./gradlew test
```

3. Run locally:
```bash
./gradlew bootRun
```

### Docker Deployment

1. Build Docker image:
```bash
docker build -t device-service:latest -f docker/Dockerfile .
```

2. Run with Docker Compose:
```bash
docker compose up device-service
```

## Testing

The service includes comprehensive test coverage:

- **Unit Tests** - Domain logic and service tests
- **Integration Tests** - Repository and API tests
- **Contract Tests** - API contract validation

Run all tests:
```bash
./gradlew test
```

Run with coverage:
```bash
./gradlew test jacocoTestReport
```

## Monitoring

The service exposes health and metrics endpoints:

- Health: `http://localhost:8081/actuator/health`
- Metrics: `http://localhost:8081/actuator/metrics`
- Info: `http://localhost:8081/actuator/info`

## Dependencies

- Spring Boot 3.x
- Spring Data JPA
- PostgreSQL Driver
- Spring Kafka
- Spring Cloud Consul
- MapStruct
- Lombok
- Spring Boot Actuator

## License

Copyright © 2025 Kyle Mielke. All rights reserved.