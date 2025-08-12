#!/bin/bash
set -e

# Wait for dependencies if needed
if [ -n "${WAIT_FOR_IT}" ]; then
    echo "Waiting for dependencies..."
    for service in ${WAIT_FOR_IT//,/ }; do
        echo "Waiting for $service..."
        while ! nc -z ${service%:*} ${service#*:}; do
            sleep 1
        done
        echo "$service is ready"
    done
fi

# Execute java with the jar
exec java -jar /app/app.jar