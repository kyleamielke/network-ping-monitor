#!/bin/bash
set -e

echo "Starting Alert Service..."
echo "Environment variables:"
echo "  SERVER_PORT: ${SERVER_PORT}"
echo "  DB_NAME: ${DB_NAME}"
echo "  KAFKA_BOOTSTRAP_SERVERS: ${KAFKA_BOOTSTRAP_SERVERS}"
echo "  SPRING_CLOUD_CONSUL_HOST: ${SPRING_CLOUD_CONSUL_HOST}"

# Wait for dependencies to be ready
echo "Waiting for dependencies..."

# Extract database host and port from datasource URL
DB_HOST=$(echo ${SPRING_DATASOURCE_URL} | sed -n 's/.*:\/\/\([^:]*\):.*/\1/p')
DB_PORT=$(echo ${SPRING_DATASOURCE_URL} | sed -n 's/.*:\([0-9]*\)\/.*/\1/p')

# Wait for PostgreSQL
echo "Waiting for PostgreSQL at ${DB_HOST}:${DB_PORT}..."
while ! nc -z ${DB_HOST} ${DB_PORT}; do
  sleep 1
done
echo "PostgreSQL is ready!"

# Wait for Kafka
KAFKA_HOST=$(echo ${KAFKA_BOOTSTRAP_SERVERS} | cut -d':' -f1)
KAFKA_PORT=$(echo ${KAFKA_BOOTSTRAP_SERVERS} | cut -d':' -f2)
echo "Waiting for Kafka at ${KAFKA_HOST}:${KAFKA_PORT}..."
while ! nc -z ${KAFKA_HOST} ${KAFKA_PORT}; do
  sleep 1
done
echo "Kafka is ready!"

# Wait for Consul
echo "Waiting for Consul at ${SPRING_CLOUD_CONSUL_HOST}:${SPRING_CLOUD_CONSUL_PORT}..."
while ! nc -z ${SPRING_CLOUD_CONSUL_HOST} ${SPRING_CLOUD_CONSUL_PORT}; do
  sleep 1
done
echo "Consul is ready!"

echo "All dependencies are ready. Starting Alert Service..."

# Start the application
exec java -jar app.jar