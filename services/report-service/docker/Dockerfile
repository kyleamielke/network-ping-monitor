FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the jar file
COPY build/libs/report-service-*.jar app.jar

# Copy entrypoint
COPY docker/docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

# Create reports directory
RUN mkdir -p /tmp/reports

EXPOSE 8085

ENTRYPOINT ["docker-entrypoint.sh"]