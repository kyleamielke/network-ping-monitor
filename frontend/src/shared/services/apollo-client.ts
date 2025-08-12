import { ApolloClient, InMemoryCache, createHttpLink, split } from '@apollo/client';
import { getMainDefinition } from '@apollo/client/utilities';
import { GraphQLWsLink } from '@apollo/client/link/subscriptions';
import { createClient } from 'graphql-ws';

// Automatically detect the host if not explicitly set
const getApiUrl = () => {
  // If VITE_API_URL is set and not localhost, use it
  if (import.meta.env.VITE_API_URL && !import.meta.env.VITE_API_URL.includes('localhost')) {
    return import.meta.env.VITE_API_URL;
  }
  
  // Special handling for thatworked.io domains
  if (window.location.hostname.endsWith('.thatworked.io')) {
    const protocol = window.location.protocol;
    return `${protocol}//api.thatworked.io:8080`;
  }
  
  // If running from a non-localhost domain, use the same domain with port 8080
  if (window.location.hostname !== 'localhost' && window.location.hostname !== '127.0.0.1') {
    const protocol = window.location.protocol;
    const hostname = window.location.hostname;
    return `${protocol}//${hostname}:8080`;
  }
  
  // For localhost development
  if (import.meta.env.DEV) {
    return `${window.location.protocol}//localhost:8080`;
  }
  
  // In production (containerized), use relative URLs which nginx will proxy
  return '';
};

const getWsUrl = () => {
  // If VITE_WS_URL is set and not localhost, use it
  if (import.meta.env.VITE_WS_URL && !import.meta.env.VITE_WS_URL.includes('localhost')) {
    return import.meta.env.VITE_WS_URL;
  }
  
  // Special handling for thatworked.io domains
  if (window.location.hostname.endsWith('.thatworked.io')) {
    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    return `${protocol}//api.thatworked.io:8080`;
  }
  
  // If running from a non-localhost domain, use the same domain with port 8080
  if (window.location.hostname !== 'localhost' && window.location.hostname !== '127.0.0.1') {
    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    const hostname = window.location.hostname;
    return `${protocol}//${hostname}:8080`;
  }
  
  // For localhost development
  if (import.meta.env.DEV) {
    return `ws://localhost:8080`;
  }
  
  // In production (containerized), use relative WebSocket URL
  const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
  const host = window.location.host;
  return `${protocol}//${host}`;
};

const API_URL = getApiUrl();
const WS_URL = getWsUrl();

// HTTP connection for queries and mutations
const httpLink = createHttpLink({
  uri: `${API_URL}/graphql`,
});

// WebSocket connection for subscriptions
const wsClient = createClient({
  url: `${WS_URL}/graphql-ws`,
  connectionParams: {
    // Add authentication headers here if needed
  },
  keepAlive: 10000, // Send keep-alive ping every 10 seconds to match server config
  connectionAckWaitTimeout: 30000, // Wait 30 seconds for connection ack
  retryAttempts: 5,
  shouldRetry: () => true,
    on: {
      error: (error) => {
        console.error('WebSocket connection error:', error);
      },
    },
});

// WebSocket link for subscriptions

const wsLink = new GraphQLWsLink(wsClient);

// Split link to route queries/mutations over HTTP and subscriptions over WebSocket
const splitLink = split(
  ({ query }) => {
    const definition = getMainDefinition(query);
    return (
      definition.kind === 'OperationDefinition' &&
      definition.operation === 'subscription'
    );
  },
  wsLink,
  httpLink,
);

// Apollo Client instance
export const apolloClient = new ApolloClient({
  link: splitLink,
  cache: new InMemoryCache({
    typePolicies: {
      Query: {
        fields: {
          devices: {
            merge(_existing = [], incoming: unknown[]) {
              return incoming;
            },
          },
          devicesWithMonitoring: {
            merge(_existing = [], incoming: unknown[]) {
              return incoming;
            },
          },
          monitoringDashboard: {
            merge(existing, incoming) {
              return {
                ...existing,
                ...incoming,
              };
            },
          },
        },
      },
      Device: {
        keyFields: ['id'],
        fields: {
          currentStatus: {
            merge(existing, incoming) {
              return {
                ...existing,
                ...incoming,
              };
            },
          },
          pingTarget: {
            merge(_existing, incoming) {
              return incoming; // Always use incoming data for pingTarget
            },
          },
          recentPings: {
            merge(_existing, incoming) {
              return incoming; // Always use incoming data for recentPings array
            },
          },
        },
      },
      PingTarget: {
        keyFields: ['deviceId'], // Use deviceId as the key for PingTarget
      },
      PingResult: {
        keyFields: false, // Don't normalize PingResult - treat as embedded data
      },
    },
  }),
  defaultOptions: {
    watchQuery: {
      fetchPolicy: 'cache-and-network',
    },
    query: {
      fetchPolicy: 'cache-first',
    },
  },
  // Clear cache on initialization to prevent stale subscription state
  ssrMode: false,
  connectToDevTools: true,
});

// Don't clear store on initialization - can cause subscription issues
// apolloClient.clearStore().catch(err => {
//   console.warn('Failed to clear Apollo Client cache on initialization:', err);
// });