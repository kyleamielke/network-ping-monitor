FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the jar file
COPY build/libs/device-service.jar app.jar

# Copy entrypoint
COPY docker/docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

EXPOSE 8081

ENTRYPOINT ["docker-entrypoint.sh"]