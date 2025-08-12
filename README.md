# NetworkPing Monitor System

A comprehensive network monitoring system that tracks the availability and response times of networked devices using ICMP ping operations. The system continuously monitors network hosts, records their status, and provides real-time insights through a GraphQL API with WebSocket subscriptions.

## Features

- **Multi-Device Monitoring**: Concurrent ICMP ping monitoring for multiple devices
- **Advanced Search**: Multi-criteria device search with pagination and sorting
- **Real-time Updates**: WebSocket subscriptions for live dashboard updates
- **Cascade Delete**: Automatic cleanup of related data on device deletion
- **Real-time Alerts**: Email notifications for device availability changes
- **Historical Analytics**: Time-series data storage with uptime statistics
- **GraphQL API**: Unified API gateway for all services with subscriptions
- **Event-Driven Architecture**: Kafka-based microservices communication
- **Application-Level Database Management**: Each service manages its own database lifecycle
- **Production Ready**: Health checks, dependency management, and robust startup handling

## Architecture

The system follows a microservices architecture with independent, scalable services:

```
                     ┌─────────────────┐
                     │    Frontend     │
                     │ (React + Vite)  │
                     │   Port 3000     │
                     └────────┬────────┘
                              │ GraphQL + WebSocket
                    ┌─────────▼───────────┐
                    │     API Gateway     │
                    │     (Port 8080)     │
                    │ GraphQL + WebSocket │
                    │   Event Streaming   │
                    └────────┬────────────┘
                             │ REST (internal)
       ┌─────────────────────┼────────────────────┐
       │                     │                    │
┌──────▼───────┐    ┌────────▼────────┐    ┌──────▼───────┐
│Device Service│    │   Ping Service  │    │ Notification │
│ (Port 8081)  │    │   (Port 8082)   │    │   Service    │
│              │    │                 │    │  (Port 8083) │
│ PostgreSQL   │    │  TimescaleDB    │    │  SMTP Ready  │
└──────┬───────┘    └────────┬────────┘    └───────┬──────┘
       │                     │                     │
       │              ┌──────▼───────┐             │
       │              │Alert Service │             │
       │              │ (Port 8084)  │             │
       │              │ PostgreSQL   │             │
       │              └──────┬───────┘             │
       │                     │                     │
       │              ┌──────▼───────┐      ┌──────▼───────┐
       │              │Report Service│      │Search Service│
       │              │ (Port 8085)  │      │ (Port 8086)  │
       │              │ PostgreSQL   │      │ PostgreSQL   │
       │              └──────┬───────┘      └──────┬───────┘
       │                     │                     │
       └─────────────────────┼─────────────────────┘
                             │
                    ┌────────▼────────┐
                    │     Kafka       │
                    │ Event Streaming │
                    └─────────────────┘
```

## Services

### Frontend (Port 3000)
- **React + TypeScript** with Material-UI components
- **Apollo Client** for GraphQL queries and subscriptions
- **Real-time updates** via WebSocket subscriptions
- **Responsive design** for desktop and mobile

### API Gateway (Port 8080)
- **GraphQL API** for frontend communication
- **WebSocket subscriptions** for real-time updates
- **Service orchestration** and data aggregation
- **FeignClient** integration with all microservices

### Device Service (Port 8081)
- **Device management** with full CRUD operations
- **Advanced search** across all device fields
- **PostgreSQL storage** with automated setup
- **Event publishing** for device state changes
- **Cascade delete** support for related data cleanup

### Ping Service (Port 8082)
- **ICMP monitoring** with configurable intervals
- **TimescaleDB** for time-series ping data
- **Virtual threads** for concurrent ping execution
- **Circuit breaker** pattern for resilience
- **Event-driven** device synchronization

### Notification Service (Port 8083)
- **Email alerts** for device availability changes
- **Configurable SMTP** settings
- **HTML email templates** with device details
- **Kafka consumer** for alert lifecycle events

### Alert Service (Port 8084)
- **Alert management** with thresholds
- **Alert lifecycle** (created, resolved, acknowledged)
- **PostgreSQL storage** for alert history
- **Event publishing** for notification service

### Report Service (Port 8085)
- **Report generation** for monitoring data
- **Historical analytics** and trends
- **Export capabilities** (PDF, CSV planned)
- **PostgreSQL storage** for report metadata

### Search Service (Port 8086)
- **Full-text search** across devices
- **Caching layer** for performance
- **Relevance scoring** for search results
- **PostgreSQL** with search optimization

## Quick Start

### Prerequisites
- Docker & Docker Compose v2
- Java 21 (for local development)
- Node.js 18+ (for frontend development)
- Git with submodule support

### Automated Setup (Recommended)

1. Clone the repository with submodules:
```bash
git clone --recursive git@github.com:kyleamielke/support.git
cd support
```

2. Run the automated setup script:
```bash
chmod +x scripts/setup.sh
./scripts/setup.sh
```

The setup script will:
- Create .env files from templates with secure passwords
- Build all Java microservices and frontend
- Start infrastructure services (TimescaleDB, Kafka, Consul)
- Start all application services
- Prompt to seed demo devices

### Setup Script Options

```bash
# Full setup with automatic seeding
./scripts/setup.sh --seed

# Skip service builds (use existing JARs)
./scripts/setup.sh --skip-service-build

# Skip Docker image builds
./scripts/setup.sh --skip-docker-build

# Skip seed data prompt
./scripts/setup.sh --no-seed

# Show all options
./scripts/setup.sh --help
```

### Manual Setup

If you prefer manual control:

1. Start all services:
```bash
docker compose up -d
```

2. Verify services are running:
```bash
docker compose ps
```

3. Access the services:
- Frontend: http://localhost:3000
- GraphQL Playground: http://localhost:8080/graphiql
- Consul UI: http://localhost:8500

### Production Deployment

For production environments:
- Frontend: https://monitor.thatworked.io
- API: https://api.thatworked.io/graphql

## API Examples

### GraphQL Device Search
```graphql
query SearchDevices {
  searchDevices(criteria: {
    name: "server"
    deviceType: "server"
    page: 0
    size: 10
  }) {
    devices {
      uuid
      name
      ipAddress
      deviceType
      site
    }
    totalElements
    totalPages
  }
}
```

### Create Device (GraphQL)
```graphql
mutation CreateDevice {
  createDevice(input: {
    name: "Web Server"
    ipAddress: "192.168.1.100"
    os: "Ubuntu"
    osType: "server"
  }) {
    uuid
    name
    ipAddress
  }
}
```

### Start Monitoring (GraphQL)
```graphql
mutation StartMonitoring {
  startPingMonitoring(deviceId: "<device-uuid>") {
    deviceId
    ipAddress
    isActive
  }
}
```

### Real-time Subscription
```graphql
subscription MonitoringUpdates {
  monitoringUpdates {
    deviceId
    status
    responseTime
    timestamp
  }
}
```

## Configuration

### Email Notifications
Configure in `notification-service/.env`:
```env
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
NOTIFICATION_TO_EMAIL=alerts@yourcompany.com
```

### Service Ports
Default service ports (configurable in .env files):
- API Gateway: 8080
- Device Service: 8081
- Ping Service: 8082
- Notification Service: 8083
- Alert Service: 8084
- Report Service: 8085
- Search Service: 8086
- Frontend: 3000
- Consul: 8500
- Kafka: 29092
- PostgreSQL/TimescaleDB: 5432

### Database Configuration
The system uses **application-level database initialization** following microservices best practices:

- **Device Service**: `support_device_db` with dedicated `support_device_user`
- **Ping Service**: `support_ping_db` with dedicated `support_ping_user` + TimescaleDB extension
- **Alert Service**: `support_alert_db` with dedicated `support_alert_user`
- **Report Service**: `support_report_db` with dedicated `support_report_user`
- **Search Service**: `support_search_db` with dedicated `support_search_user`
- **Automatic Setup**: Databases and users created on first startup
- **Service Isolation**: Each service manages its own database lifecycle

### Health Checks & Dependencies
The system implements comprehensive health checks for production reliability:

- **Infrastructure Services**: Consul, Kafka, PostgreSQL/TimescaleDB health validation
- **Application Services**: Spring Boot Actuator health endpoints
- **Dependency Chains**: Services wait for dependencies to be healthy before starting
- **Startup Robustness**: Automatic retries and graceful failure handling

All services include health check endpoints accessible at `/actuator/health`.

## Development

### Building Services
Each service can be built independently:
```bash
cd services/device-service
./gradlew clean build -x test
```

### Building Frontend
```bash
cd frontend
npm install
npm run build
```

### Running Tests
```bash
# All services
./gradlew test

# Specific service
cd services/ping-service
./gradlew test
```

### Updating Submodules
```bash
git submodule update --remote --merge
```

### Cleanup
Use the teardown script to stop and clean up:
```bash
./scripts/teardown.sh
```

## Technology Stack

- **Backend**: Spring Boot 3.x, Java 21
- **Frontend**: React 18, TypeScript, Material-UI, Vite
- **API**: GraphQL (GraphQL Java) with WebSocket subscriptions
- **Databases**: PostgreSQL 15, TimescaleDB
- **Message Queue**: Apache Kafka (KRaft mode)
- **Service Discovery**: Consul
- **Containerization**: Docker, Docker Compose v2
- **Build Tools**: Gradle 8.x, npm/Vite

## Project Status

### Completed Features
- Multi-device ICMP monitoring
- Real-time WebSocket subscriptions
- Email notifications
- Device CRUD operations
- Advanced search functionality
- Cascade delete operations
- Historical data storage
- Docker Compose deployment
- Automated setup scripts
- Health checks and monitoring

### In Progress
- PDF/CSV report export
- Advanced alerting rules
- User authentication
- Multi-tenancy support

## Contributing

This is a private project. For questions or issues, please contact the maintainer.

## License

Copyright © 2025 Kyle Mielke. All rights reserved.

This software is proprietary and confidential. Unauthorized copying, distribution, or use is strictly prohibited.