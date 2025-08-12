FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the jar file
COPY build/libs/search-service-*.jar app.jar

# Copy entrypoint
COPY docker/docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

EXPOSE 8086

ENTRYPOINT ["docker-entrypoint.sh"]