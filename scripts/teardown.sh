#!/bin/bash

# Network Ping Monitor - Teardown Script
# This script stops and cleans up all services

set -e  # Exit on error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
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

# Parse command line arguments
REMOVE_VOLUMES=false
REMOVE_IMAGES=false

while [[ $# -gt 0 ]]; do
    case $1 in
        --volumes)
            REMOVE_VOLUMES=true
            shift
            ;;
        --images)
            REMOVE_IMAGES=true
            shift
            ;;
        --all)
            REMOVE_VOLUMES=true
            REMOVE_IMAGES=true
            shift
            ;;
        *)
            print_warning "Unknown option: $1"
            shift
            ;;
    esac
done

print_status "Stopping Network Ping Monitor..."

# Stop all services
print_status "Stopping all services..."
docker compose down

# Remove volumes if requested
if [ "$REMOVE_VOLUMES" = true ]; then
    print_warning "Removing data volumes..."
    docker compose down -v
    print_status "Data volumes removed"
fi

# Remove images if requested
if [ "$REMOVE_IMAGES" = true ]; then
    print_warning "Removing Docker images..."
    docker compose down --rmi local
    print_status "Docker images removed"
fi

# Show what's left
print_status "Cleanup complete!"

if [ "$REMOVE_VOLUMES" = false ]; then
    echo ""
    print_warning "Data volumes preserved. To remove them, run:"
    echo "  ./scripts/teardown.sh --volumes"
fi

if [ "$REMOVE_IMAGES" = false ]; then
    echo ""
    print_warning "Docker images preserved. To remove them, run:"
    echo "  ./scripts/teardown.sh --images"
fi

echo ""
print_status "To remove everything (volumes and images), run:"
echo "  ./scripts/teardown.sh --all"
echo ""