rootProject.name = "network-ping-monitor"

// Include all service modules
include(
    "common",
    "services:api-gateway",
    "services:device-service",
    "services:ping-service",
    "services:alert-service",
    "services:notification-service",
    "services:report-service",
    "services:search-service"
)

// Configure module project names
project(":common").name = "support-common"
project(":services:api-gateway").name = "support-api-gateway"
project(":services:device-service").name = "support-device-service"
project(":services:ping-service").name = "support-ping-service"
project(":services:alert-service").name = "support-alert-service"
project(":services:notification-service").name = "support-notification-service"
project(":services:report-service").name = "support-report-service"
project(":services:search-service").name = "support-search-service"