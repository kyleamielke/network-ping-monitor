#!/bin/sh
set -e

echo "Starting Frontend Service..."

# Default values
: ${API_GATEWAY_HOST:=api-gateway}
: ${API_GATEWAY_PORT:=8080}

echo "Configuration:"
echo "  API_GATEWAY_HOST: ${API_GATEWAY_HOST}"
echo "  API_GATEWAY_PORT: ${API_GATEWAY_PORT}"

# Create nginx config from template
cat > /etc/nginx/conf.d/default.conf <<EOF
server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;
    index index.html;

    # Gzip compression
    gzip on;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml application/xml+rss text/javascript;

    # API Gateway proxy
    location /graphql {
        proxy_pass http://${API_GATEWAY_HOST}:${API_GATEWAY_PORT}/graphql;
        proxy_http_version 1.1;
        proxy_set_header Upgrade \$http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
        proxy_cache_bypass \$http_upgrade;
        
        # WebSocket support for subscriptions
        proxy_read_timeout 86400;
    }

    # WebSocket endpoint for GraphQL subscriptions
    location /graphql-ws {
        proxy_pass http://${API_GATEWAY_HOST}:${API_GATEWAY_PORT}/graphql-ws;
        proxy_http_version 1.1;
        proxy_set_header Upgrade \$http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
        
        # WebSocket specific settings
        proxy_read_timeout 86400;
        proxy_buffering off;
    }

    # GraphiQL proxy
    location /graphiql {
        proxy_pass http://${API_GATEWAY_HOST}:${API_GATEWAY_PORT}/graphiql;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }

    # API proxy
    location /api/ {
        proxy_pass http://${API_GATEWAY_HOST}:${API_GATEWAY_PORT}/api/;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }

    # React app - serve index.html for all routes
    location / {
        try_files \$uri \$uri/ /index.html;
    }

    # Cache static assets
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
EOF

echo "Nginx configuration generated successfully"

# Execute the CMD from Dockerfile
exec "$@"