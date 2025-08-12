# Report Service

Comprehensive report generation service for the NetworkPing Monitor system. Generates PDF and CSV reports by aggregating data from multiple microservices to provide insights into device uptime, performance metrics, and historical trends.

## Architecture Overview

The Report Service acts as a data aggregation and report generation layer, collecting information from various microservices to create comprehensive reports for network administrators and stakeholders.

### Key Features
- **Multi-format Export** - PDF reports with professional formatting and CSV for data analysis
- **Time-based Reporting** - Custom date ranges for all report types
- **Device-specific Reports** - Generate reports for individual devices or groups
- **Scheduled Reports** - Automated report generation at specified intervals
- **Template Engine** - Customizable report templates with branding support
- **Asynchronous Processing** - Non-blocking report generation for large datasets

### Design Patterns
- **Adapter Pattern** - Multiple report format adapters (PDF, CSV, Excel planned)
- **Strategy Pattern** - Different report generation strategies per type
- **Template Method** - Common report generation workflow
- **Repository Pattern** - Clean data access layer
- **Builder Pattern** - Flexible report construction

## Prerequisites

- Java 21
- Docker & Docker Compose
- PostgreSQL 13+
- Service dependencies: Device Service, Ping Service, Alert Service

## Quick Start

### Local Development

```bash
# Clone the repository
git clone https://github.com/kyleamielke/support-report-service.git
cd support-report-service

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
docker build -f docker/Dockerfile -t report-service .

# Run with Docker
docker run -p 8085:8085 report-service

# Or use Docker Compose (from support directory)
cd ../support
./scripts/docker/dev-up.sh report-service
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
| `SERVER_PORT` | Service port | 8085 |
| `SPRING_DATASOURCE_URL` | Database connection URL | jdbc:postgresql://timescaledb:5432/support_report_db |
| `REPORT_STORAGE_PATH` | Local report storage path | /tmp/reports |
| `REPORT_RETENTION_DAYS` | Days to keep generated reports | 7 |
| `REPORT_DOWNLOAD_BASE_URL` | Base URL for download links | http://localhost:8085 |
| `REPORT_PDF_TEMPLATE_PATH` | Path to PDF templates | /templates/pdf |
| `REPORT_CSV_DELIMITER` | CSV delimiter character | , |
| `REPORT_ASYNC_ENABLED` | Enable async report generation | true |
| `REPORT_MAX_CONCURRENT_JOBS` | Max concurrent report jobs | 5 |

See `.env.example` for complete configuration options.

## API Documentation

### REST Endpoints

Base URL: `http://localhost:8085/api/reports`

#### Report Generation
- `POST /generate` - Generate a new report
- `GET /status/{reportId}` - Check report generation status
- `GET /download/{filename}` - Download generated report
- `GET /list` - List available reports (paginated)
- `DELETE /{reportId}` - Delete a report

#### Report Templates
- `GET /templates` - List available report templates
- `GET /templates/{templateId}` - Get template details
- `POST /templates` - Create custom template (admin only)
- `PUT /templates/{templateId}` - Update template

#### Scheduled Reports
- `GET /schedules` - List report schedules
- `POST /schedules` - Create new schedule
- `PUT /schedules/{scheduleId}` - Update schedule
- `DELETE /schedules/{scheduleId}` - Delete schedule

#### Health & Metrics
- `GET /actuator/health` - Service health check
- `GET /actuator/metrics` - Service metrics

### API Examples

#### Generate Report
```bash
curl -X POST http://localhost:8085/api/reports/generate \
  -H "Content-Type: application/json" \
  -d '{
    "reportType": "UPTIME_SUMMARY",
    "format": "PDF",
    "startDate": "2025-01-01T00:00:00Z",
    "endDate": "2025-01-31T23:59:59Z",
    "deviceIds": ["uuid1", "uuid2"],
    "title": "January 2025 Uptime Report",
    "includeCharts": true
  }'
```

Response:
```json
{
  "reportId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "PROCESSING",
  "reportType": "UPTIME_SUMMARY",
  "format": "PDF",
  "requestedAt": "2025-01-15T10:30:00Z",
  "estimatedCompletionTime": "2025-01-15T10:30:30Z"
}
```

#### Check Report Status
```bash
curl http://localhost:8085/api/reports/status/550e8400-e29b-41d4-a716-446655440000
```

Response:
```json
{
  "reportId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "COMPLETED",
  "filename": "uptime-summary-report_20250115_103030.pdf",
  "reportType": "UPTIME_SUMMARY",
  "format": "PDF",
  "generatedAt": "2025-01-15T10:30:30Z",
  "fileSizeBytes": 245678,
  "downloadUrl": "http://localhost:8085/api/reports/download/uptime-summary-report_20250115_103030.pdf",
  "metadata": {
    "deviceCount": 2,
    "dateRange": "2025-01-01 to 2025-01-31",
    "generationTimeMs": 523
  }
}
```

#### Schedule Recurring Report
```bash
curl -X POST http://localhost:8085/api/reports/schedules \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Weekly Uptime Report",
    "reportType": "UPTIME_SUMMARY",
    "format": "PDF",
    "cronExpression": "0 0 9 ? * MON",
    "emailRecipients": ["admin@company.com"],
    "enabled": true,
    "reportConfig": {
      "includeAllDevices": true,
      "rollingWindow": "LAST_7_DAYS"
    }
  }'
```

### Report Generation Request Schema

```typescript
interface ReportRequest {
  reportType: 'UPTIME_SUMMARY' | 'DEVICE_STATUS' | 'PING_PERFORMANCE' | 'ALERT_HISTORY' | 'CUSTOM';
  format: 'PDF' | 'CSV' | 'EXCEL';
  startDate?: string;  // ISO 8601 format
  endDate?: string;    // ISO 8601 format
  deviceIds?: string[]; // Optional device filter
  title?: string;      // Custom report title
  includeCharts?: boolean; // PDF only
  customTemplate?: string; // Template ID for custom reports
  metadata?: Record<string, any>; // Additional parameters
}
```

## Report Types

### UPTIME_SUMMARY
**Purpose**: Comprehensive device availability analysis

**Data Included**:
- Device identification (name, IP, location)
- Uptime percentage with visual indicators
- Total monitoring duration
- Success/failure ping counts
- Downtime incidents with timestamps
- MTBF (Mean Time Between Failures)
- MTTR (Mean Time To Recovery)

**Visualizations** (PDF only):
- Uptime percentage gauge charts
- Availability timeline graphs
- Comparative bar charts for multiple devices

### DEVICE_STATUS
**Purpose**: Current inventory and configuration snapshot

**Data Included**:
- Complete device inventory
- Network configuration (IP, MAC, subnet)
- Hardware details (make, model, type)
- Software information (OS, version)
- Location and site assignment
- Asset management data
- Current monitoring status
- Last seen timestamp

**Grouping Options**:
- By site/location
- By device type
- By operating system
- By monitoring status

### PING_PERFORMANCE
**Purpose**: Network performance analysis and trends

**Data Included**:
- Response time statistics (min/max/avg/median)
- Percentile analysis (95th, 99th)
- Packet loss percentage
- Jitter measurements
- Performance trends over time
- Peak usage periods
- Latency distribution histogram

**Visualizations** (PDF only):
- Response time line graphs
- Latency distribution charts
- Heat maps for time-based patterns

### ALERT_HISTORY
**Purpose**: Historical incident analysis and patterns

**Data Included**:
- Alert timeline with all events
- Device-specific alert frequency
- Alert duration analysis
- Resolution time metrics
- Alert type distribution
- Severity breakdown
- Acknowledgment statistics

**Analysis Features**:
- Incident correlation
- Pattern detection
- Root cause indicators
- Improvement recommendations

### CUSTOM
**Purpose**: User-defined reports with custom data selection

**Features**:
- Custom field selection
- Flexible filtering
- Custom aggregations
- Template-based formatting
- SQL query support (admin only)

## Project Structure

```
report-service/
├── src/main/java/
│   └── io/thatworked/support/report/
│       ├── api/
│       │   ├── controller/      # REST endpoints
│       │   └── dto/             # Request/Response DTOs
│       ├── application/
│       │   ├── service/         # Business logic services
│       │   └── scheduler/       # Report scheduling
│       ├── domain/
│       │   ├── model/           # Domain entities
│       │   ├── generator/       # Report generation strategies
│       │   └── template/        # Template engines
│       ├── infrastructure/
│       │   ├── adapter/         # External service adapters
│       │   ├── repository/      # Data access layer
│       │   ├── storage/         # File storage handling
│       │   └── pdf/             # PDF generation (iText)
│       └── config/              # Spring configuration
├── src/main/resources/
│   ├── templates/               # Report templates
│   │   ├── pdf/                # PDF templates
│   │   └── email/              # Email templates
│   ├── application.yml          # Spring configuration
│   └── static/                  # Static resources (logos, etc)
├── src/test/                    # Test files
├── docker/                      # Docker configuration
├── .env.example                 # Example environment variables
└── build.gradle                 # Build configuration
```

## Testing

This service includes a comprehensive test suite designed for academic requirements with focus on domain model validation and application structure testing.

### Test Suite Overview
- **Total Tests**: 23 tests across 4 test classes
- **Success Rate**: 100% (23/23 passing)
- **Execution Time**: 0.137 seconds
- **Framework**: JUnit 5 with AssertJ assertions

### Test Classes
1. **ApplicationTest** (3 tests) - Validates main application class structure
2. **ReportTypeTest** (8 tests) - Tests report type enum values, requirements, and validation logic
3. **ReportFormatTest** (8 tests) - Tests format enum values, MIME types, and capabilities
4. **ScheduledReportStatusTest** (4 tests) - Tests scheduled report status enum values

### Running Tests
```bash
# Run all tests
./gradlew test

# View test results
open build/reports/tests/test/index.html
```

### Test Documentation
- **[docs/TEST_CASES.md](docs/TEST_CASES.md)** - Detailed test case documentation with academic coverage analysis
- **[docs/TEST_RESULTS.md](docs/TEST_RESULTS.md)** - Complete test execution results and performance metrics

### Manual API Testing
```bash
# Generate sample report
curl -X POST http://localhost:8085/api/reports/generate \
  -H "Content-Type: application/json" \
  -d '{
    "reportType": "DEVICE_STATUS",
    "format": "PDF",
    "title": "Test Report"
  }'
```

## Monitoring

### Health Check
```bash
curl http://localhost:8085/actuator/health
```

The health check includes:
- Database connectivity
- File storage availability
- Service dependencies status
- Report generation queue status

### Metrics
```bash
curl http://localhost:8085/actuator/metrics
```

Key metrics:
- `reports_generated_total` - Total reports generated
- `report_generation_duration` - Time to generate reports
- `report_generation_failures` - Failed report generations
- `report_queue_size` - Pending report jobs

### Prometheus Metrics
```bash
curl http://localhost:8085/actuator/prometheus
```

## Development

### Adding New Report Types

1. Create report generator:
```java
@Component
public class CustomReportGenerator implements ReportGenerator {
    @Override
    public String getReportType() {
        return "CUSTOM_REPORT";
    }
    
    @Override
    public Report generate(ReportRequest request) {
        // Implementation
    }
}
```

2. Add report template:
```html
<!-- templates/pdf/custom-report.html -->
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title th:text="${title}">Custom Report</title>
</head>
<body>
    <!-- Report content -->
</body>
</html>
```

3. Register in configuration:
```yaml
report:
  types:
    custom_report:
      name: "Custom Report"
      description: "Custom report description"
      supported-formats: [PDF, CSV]
```

### PDF Generation

The service uses iText 7 for PDF generation:

```java
public class PdfReportBuilder {
    public byte[] buildReport(ReportData data, String templatePath) {
        // Load HTML template
        String html = templateEngine.process(templatePath, data);
        
        // Convert to PDF
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        HtmlConverter.convertToPdf(html, baos);
        
        return baos.toByteArray();
    }
}
```

### CSV Generation

Using OpenCSV for structured data export:

```java
public class CsvReportBuilder {
    public byte[] buildReport(List<DeviceData> devices) {
        StringWriter writer = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(writer);
        
        // Write headers
        csvWriter.writeNext(new String[]{"Device", "IP", "Uptime %"});
        
        // Write data
        devices.forEach(device -> {
            csvWriter.writeNext(new String[]{
                device.getName(),
                device.getIpAddress(),
                device.getUptimePercentage().toString()
            });
        });
        
        return writer.toString().getBytes();
    }
}
```

## Troubleshooting

### Common Issues

1. **Report Generation Timeout**
   - Increase `REPORT_GENERATION_TIMEOUT` for large datasets
   - Enable async processing: `REPORT_ASYNC_ENABLED=true`
   - Check service dependency response times

2. **PDF Generation Errors**
   - Verify template syntax in `/templates/pdf/`
   - Check iText license compliance
   - Ensure sufficient memory for large reports

3. **Service Communication Failures**
   - Verify Consul service discovery
   - Check network connectivity to dependent services
   - Review circuit breaker status

4. **Storage Issues**
   - Check disk space in `REPORT_STORAGE_PATH`
   - Verify file permissions
   - Review retention policy settings

### Debug Mode

Enable debug logging:
```properties
logging.level.io.thatworked.support.report=DEBUG
logging.level.com.itextpdf=DEBUG
```

## File Storage & Management

### Storage Configuration

Reports are stored locally with automatic cleanup:

```yaml
report:
  storage:
    path: ${REPORT_STORAGE_PATH:/tmp/reports}
    retention-days: ${REPORT_RETENTION_DAYS:7}
    max-size-mb: ${REPORT_MAX_SIZE_MB:100}
    cleanup-cron: ${REPORT_CLEANUP_CRON:0 0 2 * * ?}
```

### Storage Structure
```
/tmp/reports/
├── 2025/
│   ├── 01/
│   │   ├── 15/
│   │   │   ├── uptime-summary-report_20250115_103030.pdf
│   │   │   └── device-status-report_20250115_143022.csv
│   │   └── 16/
│   └── 02/
└── temp/  # In-progress reports
```

### Retention Policy
- Reports older than `REPORT_RETENTION_DAYS` are automatically deleted
- Cleanup runs daily at 2 AM (configurable)
- Manual cleanup: `POST /api/reports/cleanup`

## Service Integration

### External Service Dependencies

#### Device Service Integration
```java
@FeignClient(name = "device-service", 
             fallbackFactory = DeviceServiceFallbackFactory.class)
public interface DeviceServiceClient {
    @GetMapping("/api/v1/devices")
    List<DeviceDTO> getAllDevices();
    
    @GetMapping("/api/v1/devices/{id}")
    DeviceDTO getDevice(@PathVariable String id);
}
```

#### Ping Service Integration
```java
@FeignClient(name = "ping-service",
             fallbackFactory = PingServiceFallbackFactory.class)
public interface PingServiceClient {
    @GetMapping("/api/ping/statistics/{deviceId}")
    PingStatisticsDTO getStatistics(@PathVariable String deviceId,
                                   @RequestParam String startDate,
                                   @RequestParam String endDate);
}
```

#### Alert Service Integration
```java
@FeignClient(name = "alert-service",
             fallbackFactory = AlertServiceFallbackFactory.class)
public interface AlertServiceClient {
    @GetMapping("/api/alerts/device/{deviceId}")
    List<AlertDTO> getDeviceAlerts(@PathVariable String deviceId,
                                  @RequestParam String startDate,
                                  @RequestParam String endDate);
}
```

### Fallback Strategies

1. **Partial Data Reports**: Generate with available data when services are down
2. **Cached Data**: Use last known data with timestamp indicators
3. **Error Sections**: Include error messages in reports for transparency
4. **Retry Logic**: Configurable retry attempts with exponential backoff

## Advanced Features

### Report Scheduling

Supports cron-based scheduling for automated reports:

```java
@Component
public class ReportScheduler {
    @Scheduled(cron = "${report.schedule.cron}")
    public void generateScheduledReports() {
        List<ScheduledReport> reports = scheduleRepository.findEnabled();
        reports.forEach(this::generateReport);
    }
}
```

### Email Distribution

Automatic email delivery for generated reports:

```yaml
report:
  email:
    enabled: ${REPORT_EMAIL_ENABLED:true}
    from: ${REPORT_EMAIL_FROM:reports@networkping.com}
    smtp-host: ${REPORT_SMTP_HOST:smtp.gmail.com}
    smtp-port: ${REPORT_SMTP_PORT:587}
```

### Custom Templates

Support for custom report templates:

1. Upload template: `POST /api/reports/templates`
2. Use in report: `"customTemplate": "template-id"`
3. Template variables: Device data, statistics, custom fields

### Bulk Operations

Generate multiple reports in batch:

```bash
curl -X POST http://localhost:8085/api/reports/bulk \
  -H "Content-Type: application/json" \
  -d '{
    "reports": [
      {"reportType": "UPTIME_SUMMARY", "format": "PDF"},
      {"reportType": "DEVICE_STATUS", "format": "CSV"},
      {"reportType": "PING_PERFORMANCE", "format": "PDF"}
    ],
    "commonParams": {
      "startDate": "2025-01-01T00:00:00Z",
      "endDate": "2025-01-31T23:59:59Z"
    }
  }'
```

## License

Copyright © 2025 Kyle Mielke. All rights reserved.

This software is proprietary and confidential. Unauthorized copying, distribution, or use is strictly prohibited.
