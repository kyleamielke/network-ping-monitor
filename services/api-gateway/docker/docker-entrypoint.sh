#!/bin/bash
set -e

# Wait for Consul to be ready
echo "Waiting for Consul to be ready..."
for i in {1..30}; do
  if curl -s http://consul:8500/v1/status/leader > /dev/null 2>&1; then
    echo "Consul is ready!"
    break
  fi
  echo "Waiting for Consul... ($i/30)"
  sleep 2
done

# Start the application
exec java -jar app.jar