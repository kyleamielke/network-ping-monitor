# Search Service

Global search and discovery service for the NetworkPing Monitor system. Provides unified search capabilities across all microservices with intelligent result ranking, caching, and real-time indexing.

## Architecture Overview

The Search Service acts as a centralized search hub, aggregating and indexing data from multiple services to provide fast, relevant search results across the entire system.

### Key Features
- **Global Search** - Search across devices, alerts, reports, and all entities from one endpoint
- **Parallel Execution** - Concurrent searches across multiple services for optimal performance
- **Intelligent Ranking** - Relevance-based result ordering with configurable algorithms
- **Real-time Indexing** - Event-driven updates via Kafka for immediate searchability
- **Result Caching** - Multi-level caching strategy for frequently searched terms
- **Type-specific Search** - Targeted searches for specific entity types

### Design Patterns
- **Aggregator Pattern** - Combines results from multiple service endpoints
- **Strategy Pattern** - Different search strategies per entity type
- **Cache-Aside Pattern** - Intelligent caching with TTL management
- **Circuit Breaker** - Fault tolerance for service dependencies
- **Event Sourcing** - Maintains search index through domain events

## Prerequisites

- Java 21
- Docker & Docker Compose
- PostgreSQL 13+
- Elasticsearch 8.x (optional, for advanced search)
- Kafka

## Quick Start

### Local Development

```bash
# Clone the repository
git clone https://github.com/kyleamielke/support-search-service.git
cd support-search-service

# Build the service
./gradlew build

# Run tests
./gradlew test

# Run the service
./gradlew bootRun
```

### Docker

```bash
# Build Docker image
docker build -f docker/Dockerfile -t search-service .

# Run with Docker
docker run -p 8086:8086 search-service

# Or use Docker Compose (from support directory)
cd ../support
./scripts/docker/dev-up.sh search-service
```

## Configuration

### Environment Variables

Create a `.env` file based on `.env.example`:

```bash
cp .env.example .env
```

Key configuration variables:

| Variable | Description | Default |
|----------|-------------|---------|
| `SERVER_PORT` | Service port | 8086 |
| `SPRING_DATASOURCE_URL` | Database connection URL | jdbc:postgresql://timescaledb:5432/support_search_db |
| `SEARCH_RESULT_LIMIT_DEFAULT` | Default result limit | 10 |
| `SEARCH_RESULT_LIMIT_MAX` | Maximum results per search | 100 |
| `SEARCH_CACHE_ENABLED` | Enable search result caching | true |
| `SEARCH_CACHE_TTL_SECONDS` | Cache time-to-live | 300 |
| `SEARCH_PARALLEL_ENABLED` | Enable parallel search execution | true |
| `SEARCH_TIMEOUT_MS` | Search timeout per service | 2000 |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | Kafka broker addresses | kafka:9092 |

See `.env.example` for complete configuration options.

## API Documentation

### REST Endpoints

Base URL: `http://localhost:8086/api/search`

#### Search Operations
- `GET /` - Global search across all entities
- `GET /{type}` - Type-specific search (devices, alerts, reports, etc.)
- `GET /suggest` - Search suggestions and autocomplete
- `GET /recent` - Recent search history
- `POST /advanced` - Advanced search with filters

#### Index Management
- `POST /index/refresh` - Manually refresh search index
- `GET /index/stats` - Index statistics and health
- `DELETE /index/cache` - Clear search cache

#### Health & Metrics
- `GET /actuator/health` - Service health check
- `GET /actuator/metrics` - Service metrics

### API Examples

#### Global Search
```bash
curl "http://localhost:8086/api/search?q=server&limit=20"
```

Response:
```json
{
  "query": "server",
  "totalResults": 45,
  "searchTimeMs": 123,
  "results": [
    {
      "type": "DEVICE",
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "name": "Web Server 01",
      "description": "Main production web server",
      "relevanceScore": 0.95,
      "highlight": "Web <em>Server</em> 01",
      "metadata": {
        "ipAddress": "192.168.1.100",
        "deviceType": "Server",
        "status": "online"
      }
    },
    {
      "type": "ALERT",
      "id": "660e8400-e29b-41d4-a716-446655440001",
      "name": "Server Down Alert",
      "description": "Web server not responding",
      "relevanceScore": 0.87,
      "highlight": "<em>Server</em> Down Alert",
      "metadata": {
        "severity": "HIGH",
        "deviceId": "550e8400-e29b-41d4-a716-446655440000",
        "timestamp": "2025-01-15T10:30:00Z"
      }
    }
  ],
  "facets": {
    "type": {
      "DEVICE": 25,
      "ALERT": 15,
      "REPORT": 5
    }
  }
}
```

#### Type-specific Search
```bash
curl "http://localhost:8086/api/search/devices?q=192.168&limit=10"
```

#### Advanced Search
```bash
curl -X POST http://localhost:8086/api/search/advanced \
  -H "Content-Type: application/json" \
  -d '{
    "query": "server",
    "filters": {
      "type": ["DEVICE"],
      "deviceType": ["Server"],
      "status": ["online"],
      "site": ["datacenter-1"]
    },
    "dateRange": {
      "start": "2025-01-01T00:00:00Z",
      "end": "2025-01-31T23:59:59Z"
    },
    "sortBy": "relevance",
    "page": 0,
    "size": 20
  }'
```

#### Search Suggestions
```bash
curl "http://localhost:8086/api/search/suggest?q=serv&limit=5"
```

Response:
```json
{
  "suggestions": [
    "server",
    "service",
    "server room",
    "server-01",
    "service desk"
  ]
}
```

### Event Consumption

The service consumes events for real-time index updates:

| Event | Topic | Action |
|-------|-------|--------|
| `DeviceCreatedEvent` | `device-events` | Index new device |
| `DeviceUpdatedEvent` | `device-events` | Update device index |
| `DeviceDeletedEvent` | `device-events` | Remove from index |
| `AlertCreatedEvent` | `alert-lifecycle-events` | Index new alert |
| `ReportGeneratedEvent` | `report-events` | Index new report |

## Project Structure

```
search-service/
├── src/main/java/
│   └── io/thatworked/support/search/
│       ├── api/
│       │   ├── controller/      # REST endpoints
│       │   └── dto/             # Request/Response DTOs
│       ├── application/
│       │   ├── service/         # Search orchestration
│       │   └── aggregator/      # Result aggregation
│       ├── domain/
│       │   ├── model/           # Search result models
│       │   ├── ranking/         # Relevance algorithms
│       │   └── strategy/        # Search strategies
│       ├── infrastructure/
│       │   ├── client/          # Service clients
│       │   ├── cache/           # Caching implementation
│       │   ├── index/           # Index management
│       │   └── event/           # Kafka consumers
│       └── config/              # Spring configuration
├── src/main/resources/
│   ├── application.yml          # Spring configuration
│   └── search/                  # Search configurations
├── src/test/                    # Test files
├── docker/                      # Docker configuration
├── .env.example                 # Example environment variables
└── build.gradle                 # Build configuration
```

## Testing

This service includes a comprehensive test suite designed for academic requirements with focus on domain model validation and application structure testing.

### Test Suite Overview
- **Total Tests**: 13 tests across 2 test classes
- **Success Rate**: 100% (13/13 passing)
- **Execution Time**: <1 second
- **Framework**: JUnit 5 with AssertJ assertions

### Test Classes
1. **ApplicationTest** (3 tests) - Validates main application class structure
2. **SearchTypeTest** (10 tests) - Tests SearchType enum for search operations, value conversion, and business logic

### Running Tests
```bash
# Run all tests
./gradlew test

# View test results
open build/reports/tests/test/index.html
```

### Test Documentation
- **[docs/TEST_CASES.md](docs/TEST_CASES.md)** - Detailed test case documentation with search domain coverage analysis
- **[docs/TEST_RESULTS.md](docs/TEST_RESULTS.md)** - Complete test execution results and performance metrics

### Manual API Testing
```bash
# Test search functionality
curl -X POST http://localhost:8086/api/search \
  -H "Content-Type: application/json" \
  -d '{"query": "test", "type": "device", "page": 0, "size": 10}'
```

### Test Coverage
```bash
./gradlew jacocoTestReport
# Report available at build/reports/jacoco/test/html/index.html
```

## Monitoring

### Health Check
```bash
curl http://localhost:8086/actuator/health
```

Response includes:
- Service status
- Database connectivity
- Cache status
- Index health
- Service dependencies

### Metrics
```bash
curl http://localhost:8086/actuator/metrics
```

Key metrics:
- `search_requests_total` - Total search requests
- `search_response_time` - Search latency histogram
- `search_cache_hit_rate` - Cache effectiveness
- `search_parallel_executions` - Concurrent search operations
- `index_size` - Number of indexed items

### Prometheus Metrics
```bash
curl http://localhost:8086/actuator/prometheus
```

## Development

### Search Strategies

Implement custom search strategies for new entity types:

```java
@Component
public class CustomEntitySearchStrategy implements SearchStrategy {
    @Override
    public String getEntityType() {
        return "CUSTOM";
    }
    
    @Override
    public SearchResults search(String query, SearchOptions options) {
        // Custom search implementation
    }
}
```

### Ranking Algorithms

Customize result ranking:

```java
@Component
public class CustomRankingAlgorithm implements RankingAlgorithm {
    @Override
    public double calculateRelevance(SearchResult result, String query) {
        // Custom relevance calculation
        double nameMatch = calculateNameSimilarity(result.getName(), query);
        double descriptionMatch = calculateDescriptionMatch(result.getDescription(), query);
        double recency = calculateRecencyScore(result.getLastModified());
        
        return (nameMatch * 0.5) + (descriptionMatch * 0.3) + (recency * 0.2);
    }
}
```

### Adding New Search Sources

1. Create service client:
```java
@FeignClient(name = "new-service")
public interface NewServiceClient {
    @GetMapping("/api/search")
    List<SearchableEntity> search(@RequestParam String query);
}
```

2. Implement search adapter:
```java
@Component
public class NewServiceSearchAdapter implements SearchAdapter {
    @Override
    public CompletableFuture<List<SearchResult>> search(String query) {
        // Adapter implementation
    }
}
```

3. Register in aggregator:
```java
@Configuration
public class SearchConfig {
    @Bean
    public SearchAggregator searchAggregator(List<SearchAdapter> adapters) {
        return new ParallelSearchAggregator(adapters);
    }
}
```

## Troubleshooting

### Common Issues

1. **Slow Search Performance**
   - Enable parallel search: `SEARCH_PARALLEL_ENABLED=true`
   - Increase cache TTL: `SEARCH_CACHE_TTL_SECONDS=600`
   - Check service response times in metrics
   - Consider implementing Elasticsearch for large datasets

2. **Stale Search Results**
   - Verify Kafka event consumption
   - Check index refresh frequency
   - Manually refresh index: `POST /api/search/index/refresh`
   - Review cache invalidation logic

3. **Missing Search Results**
   - Ensure all services are registered
   - Check circuit breaker status
   - Verify service client timeouts
   - Review search strategy implementations

4. **High Memory Usage**
   - Reduce cache size limits
   - Implement cache eviction policies
   - Monitor result set sizes
   - Use pagination for large results

### Debug Mode

Enable debug logging:
```properties
logging.level.io.thatworked.support.search=DEBUG
logging.level.org.springframework.cloud.openfeign=DEBUG
```

## Performance Optimization

### Caching Strategy

Multi-level caching for optimal performance:

1. **Request Cache** - Short-lived cache for identical queries
2. **Result Cache** - Longer-lived cache for search results
3. **Index Cache** - In-memory index for frequent searches

### Parallel Execution

Configure parallel search settings:

```yaml
search:
  parallel:
    enabled: true
    thread-pool-size: 10
    timeout-ms: 2000
    fail-fast: false
```

### Index Optimization

1. **Incremental Updates** - Process events in batches
2. **Async Indexing** - Non-blocking index updates
3. **Partial Indexes** - Index only searchable fields
4. **Compression** - Reduce index memory footprint

## License

Copyright © 2025 Kyle Mielke. All rights reserved.

This software is proprietary and confidential. Unauthorized copying, distribution, or use is strictly prohibited.