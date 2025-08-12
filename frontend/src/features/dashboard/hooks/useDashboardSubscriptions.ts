import { useSubscription, useApolloClient } from '@apollo/client';
import { useState, useRef, useEffect } from 'react';
import { ALERT_STREAM_SUBSCRIPTION, PING_UPDATES_SUBSCRIPTION, DEVICE_STATUS_UPDATES_SUBSCRIPTION } from '@/shared/api/subscriptions';

// Retry configuration
const MAX_RETRY_ATTEMPTS = 3;
const INITIAL_RETRY_DELAY = 1000; // 1 second
const MAX_RETRY_DELAY = 10000; // 10 seconds

export const useDashboardSubscriptions = () => {
  const client = useApolloClient();
  const [alertRetryCount, setAlertRetryCount] = useState(0);
  const [pingRetryCount, setPingRetryCount] = useState(0);
  const [statusRetryCount, setStatusRetryCount] = useState(0);
  const alertRetryTimeoutRef = useRef<NodeJS.Timeout>();
  const pingRetryTimeoutRef = useRef<NodeJS.Timeout>();
  const statusRetryTimeoutRef = useRef<NodeJS.Timeout>();
  
  // Clean up timeouts on unmount
  useEffect(() => {
    return () => {
      if (alertRetryTimeoutRef.current) clearTimeout(alertRetryTimeoutRef.current);
      if (pingRetryTimeoutRef.current) clearTimeout(pingRetryTimeoutRef.current);
      if (statusRetryTimeoutRef.current) clearTimeout(statusRetryTimeoutRef.current);
    };
  }, []);

  // Subscribe to alert stream
  useSubscription(ALERT_STREAM_SUBSCRIPTION, {
    skip: alertRetryCount >= MAX_RETRY_ATTEMPTS,
    onData: ({ data }) => {
      if (data?.data?.alertStream) {
        setAlertRetryCount(0); // Reset on success
        // Update the monitoring dashboard cache to increment unresolved alerts
        const cache = client.cache;
        cache.modify({
          fields: {
            monitoringDashboard(existingData) {
              return {
                ...existingData,
                unresolvedAlerts: (existingData.unresolvedAlerts || 0) + 1
              };
            }
          }
        });
      }
    },
    onError: (error) => {
      if (alertRetryCount < MAX_RETRY_ATTEMPTS) {
        const delay = Math.min(INITIAL_RETRY_DELAY * Math.pow(2, alertRetryCount), MAX_RETRY_DELAY);
        console.warn(`Alert subscription error (attempt ${alertRetryCount + 1}/${MAX_RETRY_ATTEMPTS}), retrying in ${delay}ms...`);
        
        alertRetryTimeoutRef.current = setTimeout(() => {
          setAlertRetryCount(prev => prev + 1);
        }, delay);
      } else {
        console.error('Alert subscription failed after max retries:', error);
      }
    },
  });

  // Subscribe to ping updates
  useSubscription(PING_UPDATES_SUBSCRIPTION, {
    skip: pingRetryCount >= MAX_RETRY_ATTEMPTS,
    onData: ({ data }) => {
      if (data?.data?.pingUpdates) {
        setPingRetryCount(0); // Reset on success
        const update = data.data.pingUpdates;
        
        try {
          // Update the device's current status in cache
          const cacheId = client.cache.identify({ __typename: 'Device', id: update.deviceId });
          
          if (cacheId) {
            // Update the device's status and recentPings in cache
            client.cache.modify({
              id: cacheId,
              fields: {
                currentStatus(existingStatus) {
                  return {
                    ...existingStatus,
                    __typename: 'DeviceStatus',
                    online: update.success,
                    responseTime: update.responseTimeMs,
                    lastStatusChange: new Date().toISOString()
                  };
                },
                recentPings(existingPings = []) {
                  // Make sure we have an array
                  const currentPings = Array.isArray(existingPings) ? existingPings : [];
                  
                  const newPing = {
                    __typename: 'PingResult',
                    timestamp: update.timestamp,
                    success: update.success,
                    responseTimeMs: update.responseTimeMs
                  };
                  
                  // Prepend new ping and keep only 50 most recent
                  return [newPing, ...currentPings].slice(0, 50);
                }
              }
            });
          } else {
          }
        } catch (error) {
          console.error('Error updating cache:', error);
        }
      }
    },
    onError: (error) => {
      if (pingRetryCount < MAX_RETRY_ATTEMPTS) {
        const delay = Math.min(INITIAL_RETRY_DELAY * Math.pow(2, pingRetryCount), MAX_RETRY_DELAY);
        console.warn(`Ping subscription error (attempt ${pingRetryCount + 1}/${MAX_RETRY_ATTEMPTS}), retrying in ${delay}ms...`);
        
        pingRetryTimeoutRef.current = setTimeout(() => {
          setPingRetryCount(prev => prev + 1);
        }, delay);
      } else {
        console.error('Ping subscription failed after max retries:', error);
      }
    },
  });

  // Subscribe to device status updates
  useSubscription(DEVICE_STATUS_UPDATES_SUBSCRIPTION, {
    skip: statusRetryCount >= MAX_RETRY_ATTEMPTS,
    onData: ({ data }) => {
      if (data?.data?.deviceStatusUpdates) {
        setStatusRetryCount(0); // Reset on success
        // Just log for now - don't update counts
      }
    },
    onError: (error) => {
      if (statusRetryCount < MAX_RETRY_ATTEMPTS) {
        const delay = Math.min(INITIAL_RETRY_DELAY * Math.pow(2, statusRetryCount), MAX_RETRY_DELAY);
        console.warn(`Status subscription error (attempt ${statusRetryCount + 1}/${MAX_RETRY_ATTEMPTS}), retrying in ${delay}ms...`);
        
        statusRetryTimeoutRef.current = setTimeout(() => {
          setStatusRetryCount(prev => prev + 1);
        }, delay);
      } else {
        console.error('Status subscription failed after max retries:', error);
      }
    },
  });
};