#!/bin/bash

# Network Ping Monitor - Setup Script
# This script builds and starts all services

set -e  # Exit on error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[✓]${NC} $1"
}

print_error() {
    echo -e "${RED}[✗]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[!]${NC} $1"
}

print_info() {
    echo -e "${BLUE}[i]${NC} $1"
}

# Function to generate random password
generate_password() {
    openssl rand -base64 32 | tr -d "=+/" | cut -c1-25
}

# Function to setup .env files from .env.example
setup_env_files() {
    local dir=$1
    local env_file="$dir/.env"
    local example_file="$dir/.env.example"
    
    if [ -f "$example_file" ] && [ ! -f "$env_file" ]; then
        print_info "Creating $env_file from example..."
        cp "$example_file" "$env_file"
        
        # Generate random passwords for database users
        local random_pass=$(generate_password)
        
        # Replace placeholder passwords with random ones
        sed -i "s/your_secure_password_here/$random_pass/g" "$env_file"
        sed -i "s/device_secure_pass_2024/$random_pass/g" "$env_file"
        sed -i "s/ping_secure_pass_2024/$random_pass/g" "$env_file"
        sed -i "s/alert_secure_pass_2024/$random_pass/g" "$env_file"
        sed -i "s/notification_secure_pass_2024/$random_pass/g" "$env_file"
        sed -i "s/report_secure_pass_2024/$random_pass/g" "$env_file"
        sed -i "s/search_secure_pass_2024/$random_pass/g" "$env_file"
        
        print_status "Created $env_file with secure passwords"
    elif [ -f "$env_file" ]; then
        print_info "$env_file already exists, skipping..."
    fi
}

# Parse command line arguments
SKIP_SERVICE_BUILD=false
SKIP_DOCKER_BUILD=false
AUTO_SEED=false
SKIP_SEED=false

print_info "Network Ping Monitor Setup"
echo ""

while [[ "$#" -gt 0 ]]; do
    case $1 in
        --skip-service-build) SKIP_SERVICE_BUILD=true; print_info "Skipping service builds" ;;
        --skip-docker-build) SKIP_DOCKER_BUILD=true; print_info "Skipping Docker builds" ;;
        --seed) AUTO_SEED=true; print_info "Will seed demo data automatically" ;;
        --no-seed) SKIP_SEED=true; print_info "Will skip seeding prompt" ;;
        --help) 
            echo "Usage: $0 [options]"
            echo ""
            echo "Options:"
            echo "  --skip-service-build  Skip building Java/Node services"
            echo "  --skip-docker-build   Skip building Docker images"
            echo "  --seed                Automatically seed demo data"
            echo "  --no-seed             Skip seed data prompt"
            echo "  --help                Show this help message"
            echo ""
            echo "Examples:"
            echo "  $0                          # Full setup with prompts"
            echo "  $0 --seed                   # Full setup with auto-seeding"
            echo "  $0 --skip-service-build     # Skip gradle/npm builds"
            echo "  $0 --skip-docker-build      # Skip docker image builds"
            echo "  $0 --no-seed                # Skip seed prompt"
            exit 0
            ;;
        *) print_warning "Unknown parameter: $1"; exit 1 ;;
    esac
    shift
done

echo ""

# Check and create main .env file
if [ ! -f .env ]; then
    if [ -f .env.example ]; then
        print_info "Creating .env from .env.example..."
        cp .env.example .env
        print_status ".env file created"
    else
        print_error ".env file not found and no .env.example available!"
        print_warning "Please create a .env file with your configuration"
        exit 1
    fi
fi

# Setup .env files for each service
SERVICES=("device-service" "ping-service" "alert-service" "notification-service" "report-service" "search-service" "api-gateway")

for service in "${SERVICES[@]}"; do
    if [ -d "services/$service" ]; then
        setup_env_files "services/$service"
    fi
done

# Setup env_files directory - copy from boilerplate if files don't exist
if [ -d "env_files/boilerplate" ]; then
    for boilerplate_file in env_files/boilerplate/*.env; do
        if [ -f "$boilerplate_file" ]; then
            env_filename=$(basename "$boilerplate_file")
            env_file="env_files/$env_filename"
            if [ ! -f "$env_file" ]; then
                print_info "Creating $env_file from boilerplate..."
                cp "$boilerplate_file" "$env_file"
                
                # Generate random passwords for PostgreSQL
                if [[ "$env_file" == *"db.env"* ]]; then
                    db_password=$(generate_password)
                    sed -i "s/devpassword/$db_password/g" "$env_file"
                    sed -i "s/postgres_password_here/$db_password/g" "$env_file"
                fi
                
                print_status "Created $env_file"
            fi
        fi
    done
elif [ -d "env_files" ]; then
    # Fallback to .env.example files if boilerplate doesn't exist
    for example_file in env_files/*.env.example; do
        if [ -f "$example_file" ]; then
            env_file="${example_file%.example}"
            if [ ! -f "$env_file" ]; then
                print_info "Creating $env_file from example..."
                cp "$example_file" "$env_file"
                
                # Generate random passwords for PostgreSQL
                if [[ "$env_file" == *"db.env"* ]]; then
                    db_password=$(generate_password)
                    sed -i "s/devpassword/$db_password/g" "$env_file"
                    sed -i "s/postgres_password_here/$db_password/g" "$env_file"
                fi
                
                print_status "Created $env_file"
            fi
        fi
    done
fi

# Load environment variables
export $(grep -v '^#' .env | xargs)

print_status "Starting Network Ping Monitor setup..."

# Build services unless skipped
if [ "$SKIP_SERVICE_BUILD" = false ]; then
    # Build common module first (if it exists)
    if [ -d "common" ]; then
        print_info "Building common module..."
        cd common
        ./gradlew clean build -x test
        cd ..
        print_status "Common module built successfully"
    fi

    # Build Java services with Gradle
    JAVA_SERVICES=("device-service" "ping-service" "alert-service" "notification-service" "report-service" "search-service" "api-gateway")

    for service in "${JAVA_SERVICES[@]}"; do
        if [ -d "services/$service" ]; then
            print_info "Building $service..."
            cd services/$service
            ./gradlew clean build -x test
            cd ../..
            print_status "$service built successfully"
        else
            print_warning "Service directory services/$service not found, skipping..."
        fi
    done

    # Build frontend with npm
    if [ -d "frontend" ]; then
        print_info "Building frontend..."
        cd frontend
        
        # Create .env if .env.example exists
        if [ -f ".env.example" ] && [ ! -f ".env" ]; then
            cp .env.example .env
            print_status "Created frontend/.env from example"
        fi
        
        npm install
        npm run build
        cd ..
        print_status "Frontend built successfully"
    else
        print_warning "Frontend directory not found, skipping..."
    fi
else
    print_info "Skipping service builds (--skip-service-build flag)"
fi

# Build Docker images unless skipped
if [ "$SKIP_DOCKER_BUILD" = false ]; then
    print_status "Building Docker images..."
    docker compose build --parallel
else
    print_info "Skipping Docker builds (--skip-docker-build flag)"
fi

# Pull infrastructure images
print_status "Pulling infrastructure images..."
docker compose pull consul kafka timescaledb

# Start infrastructure services first
print_status "Starting infrastructure services..."
docker compose up -d consul kafka timescaledb

# Wait for infrastructure to be healthy
print_status "Waiting for infrastructure services to be healthy..."
sleep 15

# Check infrastructure health
docker compose ps consul kafka timescaledb

# Start application services
print_status "Starting application services..."
docker compose up -d device-service search-service ping-service alert-service notification-service report-service api-gateway

# Wait for services to be healthy
print_status "Waiting for application services to be healthy..."
sleep 20

# Start frontend
print_status "Starting frontend..."
docker compose up -d frontend

# Show service status
print_status "All services started. Checking status..."
docker compose ps

# Handle seeding
if [ "$SKIP_SEED" = false ]; then
    if [ "$AUTO_SEED" = true ]; then
        # Auto-seed without prompting
        print_status "Auto-seeding demo devices..."
        sleep 10
        if [ -d "scripts/seed" ]; then
            cd scripts/seed
            npm install
            node seed-devices.js
            cd ../..
            print_status "Demo devices seeded successfully!"
        else
            print_warning "Seed script directory not found"
        fi
    else
        # Prompt for seeding
        echo ""
        read -p "Would you like to seed demo devices? (y/N): " -n 1 -r
        echo ""
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            print_status "Waiting for API Gateway to be ready..."
            sleep 10
            if [ -d "scripts/seed" ]; then
                print_status "Seeding demo devices..."
                cd scripts/seed
                npm install
                node seed-devices.js
                cd ../..
                print_status "Demo devices seeded successfully!"
            else
                print_warning "Seed script directory not found"
            fi
        else
            print_info "Skipping seed data"
        fi
    fi
else
    print_info "Skipping seed data (--no-seed flag)"
fi

# Display access information
echo ""
print_status "Network Ping Monitor is running!"
echo ""
echo "Access points:"
echo "  - Frontend:        http://localhost:${FRONTEND_PORT:-3000}"
echo "  - API Gateway:     http://localhost:${API_GATEWAY_PORT:-8080}/graphiql"
echo "  - Consul UI:       http://localhost:${CONSUL_PORT:-8500}"
echo ""

if [ "$SKIP_SEED" = false ] && [ "$AUTO_SEED" = false ]; then
    echo "To manually seed demo devices later:"
    echo "  - Run: cd scripts/seed && npm install && node seed-devices.js"
    echo ""
fi

echo "To view logs:"
echo "  - All services:    docker compose logs -f"
echo "  - Specific service: docker compose logs -f [service-name]"
echo ""
echo "To stop all services:"
echo "  - Run: ./scripts/teardown.sh"
echo ""

if [ -d "services/notification-service" ]; then
    print_warning "Note: .env files have been created with random passwords."
    print_warning "If you need to configure email notifications, update services/notification-service/.env"
fi