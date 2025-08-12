#!/bin/bash
set -e

# Add any pre-start configuration here
echo "Starting Notification Service..."

# Start the Spring Boot application
exec java ${JAVA_OPTS} -jar /app/app.jar "$@"