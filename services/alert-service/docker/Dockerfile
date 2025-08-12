FROM eclipse-temurin:21-jre

RUN apt-get update && apt-get install -y curl netcat-openbsd && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy the built JAR file
COPY build/libs/alert-service-*.jar app.jar

# Copy the entrypoint script
COPY docker/docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh

# Expose the port
EXPOSE 8084

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:8084/actuator/health || exit 1

ENTRYPOINT ["/docker-entrypoint.sh"]