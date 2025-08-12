FROM eclipse-temurin:21-jre

WORKDIR /app

# Add a volume pointing to /app/data
VOLUME /app/data

# Copy the jar file
COPY build/libs/*.jar app.jar

# Copy Entrypoint
COPY docker/docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

# Expose the application port
EXPOSE 8082

# Start the application
ENTRYPOINT ["docker-entrypoint.sh"]