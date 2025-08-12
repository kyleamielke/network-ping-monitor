# NetworkPing Monitor - Frontend

A modern React-based web interface for the NetworkPing Monitor system, providing real-time network device monitoring, alerting, and reporting capabilities.

##  Features

- **Real-time Device Monitoring**: Live status updates via WebSocket subscriptions
- **Interactive Dashboard**: At-a-glance view of network health with responsive charts
- **Device Management**: Full CRUD operations for network devices
- **Alert Management**: View, acknowledge, and resolve network alerts
- **Advanced Search**: Multi-criteria device search with pagination
- **Report Generation**: Generate and download PDF/Excel reports
- **Site Management**: Organize devices by location with circuit tracking
- **Responsive Design**: Works seamlessly on desktop and mobile devices

##  Architecture

### Technology Stack
- **Framework**: React 18.3 with TypeScript
- **State Management**: Apollo Client for GraphQL state
- **UI Components**: Material-UI (MUI) v5
- **Routing**: React Router v6
- **GraphQL**: Apollo Client with WebSocket subscriptions
- **Build Tool**: Vite
- **Package Manager**: npm

### Project Structure
```
src/
├── components/         # Reusable UI components
│   ├── common/        # Shared components (Header, Sidebar, etc.)
│   ├── ui/            # UI-specific components (cards, charts, etc.)
│   └── forms/         # Form components
├── contexts/          # React contexts (Toast notifications)
├── graphql/           # GraphQL queries, mutations, subscriptions
│   ├── mutations/
│   ├── queries/
│   └── subscriptions/
├── hooks/             # Custom React hooks
├── pages/             # Page components (routes)
├── services/          # Service layer (Apollo client, auth, etc.)
├── types/             # TypeScript type definitions
└── utils/             # Utility functions
```

##  Prerequisites

- Node.js 18+ and npm
- Access to backend services (API Gateway on port 8080)

##  Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd front-end
```

2. Install dependencies:
```bash
npm install
```

3. Create environment configuration (if needed):
```bash
cp .env.example .env
```

##  Running the Application

### Development Mode
```bash
npm run dev
```
The application will be available at `http://localhost:3000`

### Production Build
```bash
npm run build
npm run preview
```

##  API Configuration

The frontend connects to the API Gateway for all backend operations:

- **GraphQL Endpoint**: `http://localhost:8080/graphql`
- **WebSocket Endpoint**: `ws://localhost:8080/graphql-ws`

To change the backend URL, update the configuration in `src/services/apollo-client.ts`

##  Testing

This frontend includes a comprehensive test suite designed for academic requirements with focus on React components, custom hooks, constants, and TypeScript types.

### Test Suite Overview
- **Total Tests**: 42 tests across 4 test files
- **Success Rate**: 100% (42/42 passing)
- **Execution Time**: ~11 seconds
- **Framework**: Vitest with @testing-library/react

### Test Files
1. **App.test.tsx** (6 tests) - Tests main App component structure and rendering
2. **navigation.test.ts** (10 tests) - Tests navigation constants and structure validation
3. **useDebounce.test.ts** (13 tests) - Tests custom React hook functionality and type safety
4. **device.types.test.ts** (13 tests) - Tests TypeScript type definitions and interfaces

### Running Tests
```bash
# Run all tests
npm test

# Run tests with UI
npm run test:ui

# Run tests with coverage
npm run test:coverage

# View test results
open coverage/index.html
```

### Test Documentation
- **[docs/TEST_CASES.md](docs/TEST_CASES.md)** - Detailed test case documentation with React and TypeScript coverage analysis
- **[docs/TEST_RESULTS.md](docs/TEST_RESULTS.md)** - Complete test execution results and performance metrics

### Manual Testing
```bash
# Start development server for manual testing
npm run dev

# Build for production testing
npm run build && npm run preview
```

##  Key Features

### Dashboard
- Real-time device status monitoring
- Network statistics (online/offline devices, response times)
- Interactive response time charts
- Alert notifications

### Device Management
- Add, edit, and delete network devices
- Support for various device types (router, server, computer, etc.)
- IP and MAC address tracking
- Custom metadata and tags

### Monitoring
- Start/stop monitoring for individual devices
- Configure ping intervals and thresholds
- View ping history and statistics
- Circuit breaker status for failed devices

### Alerts
- Real-time alert notifications
- Alert acknowledgment workflow
- Alert history and filtering
- Email notification integration

### Reports
- Generate network status reports
- Export to PDF or Excel formats
- Scheduled report generation
- Custom date ranges

##  Development

### Code Style
- TypeScript for type safety
- ESLint for code linting
- Prettier for code formatting

### Key Libraries
- `@apollo/client`: GraphQL client with caching
- `@mui/material`: Material Design components
- `react-router-dom`: Client-side routing
- `recharts`: Charting library
- `date-fns`: Date manipulation

### GraphQL Subscriptions
The application uses WebSocket subscriptions for real-time updates:
- `pingUpdates`: Real-time ping results
- `deviceStatusUpdates`: Device online/offline events
- `alertStream`: New alert notifications

##  Deployment

### Docker
```dockerfile
FROM node:18-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
```

### Environment Variables
- `VITE_API_URL`: Backend API URL (default: http://localhost:8080)
- `VITE_WS_URL`: WebSocket URL (default: ws://localhost:8080)

##  Troubleshooting

### WebSocket Connection Issues
- Ensure the API Gateway is running and accessible
- Check browser console for connection errors
- Verify WebSocket endpoint URL

### GraphQL Errors
- Check network tab for failed requests
- Verify GraphQL schema matches backend
- Clear Apollo Client cache if needed

##  License

This project is part of the NetworkPing Monitor system.

##  Contributing

1. Create a feature branch
2. Make your changes
3. Add/update tests
4. Submit a pull request

##  Support

For issues and questions, please check the main project documentation or create an issue in the repository.