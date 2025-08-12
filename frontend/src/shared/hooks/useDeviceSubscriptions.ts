import { useSubscription } from '@apollo/client';
import { useEffect, useRef, useMemo, useState } from 'react';
import { DEVICE_STATUS_UPDATES_SUBSCRIPTION, PING_UPDATES_SUBSCRIPTION } from '@/shared/api/subscriptions';
import { PingUpdate, DeviceStatusUpdate } from '@/shared/types/device.types';

// Singleton to track subscription instances
const subscriptionInstances = new Map<string, number>();

// Retry configuration
const MAX_RETRY_ATTEMPTS = 3;
const INITIAL_RETRY_DELAY = 1000; // 1 second
const MAX_RETRY_DELAY = 10000; // 10 seconds

interface UseDeviceSubscriptionsProps {
  deviceId?: string;
  deviceIds?: string[];
  skip?: boolean;
  onPingUpdate?: (data: { pingUpdates: PingUpdate }) => void;
  onStatusUpdate?: (data: { deviceStatusUpdates: DeviceStatusUpdate }) => void;
}

export const useDeviceSubscriptions = ({ 
  deviceId, 
  deviceIds: _deviceIds,
  skip = false,
  onPingUpdate, 
  onStatusUpdate 
}: UseDeviceSubscriptionsProps = {}) => {
  const instanceIdRef = useRef<string>();
  const [statusRetryCount, setStatusRetryCount] = useState(0);
  const [pingRetryCount, setPingRetryCount] = useState(0);
  const statusRetryTimeoutRef = useRef<NodeJS.Timeout>();
  const pingRetryTimeoutRef = useRef<NodeJS.Timeout>();
  
  // Create unique instance ID on mount
  useEffect(() => {
    if (!instanceIdRef.current) {
      instanceIdRef.current = `sub-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
      const count = subscriptionInstances.get('total') || 0;
      subscriptionInstances.set('total', count + 1);
      subscriptionInstances.set(instanceIdRef.current, Date.now());
      
    }
    
    return () => {
      if (instanceIdRef.current) {
        subscriptionInstances.delete(instanceIdRef.current);
        const count = subscriptionInstances.get('total') || 1;
        subscriptionInstances.set('total', Math.max(0, count - 1));
      }
      // Clear any pending retry timeouts
      if (statusRetryTimeoutRef.current) clearTimeout(statusRetryTimeoutRef.current);
      if (pingRetryTimeoutRef.current) clearTimeout(pingRetryTimeoutRef.current);
    };
  }, []);
  
  // Determine subscription strategy
  const subscriptionDeviceId = deviceId;
  const shouldSubscribe = !skip && instanceIdRef.current !== undefined; // Only subscribe if we have an instance ID
  
  // Memoize subscription options to prevent unnecessary resubscriptions
  const statusSubscriptionOptions = useMemo(() => ({
    variables: { deviceId: subscriptionDeviceId },
    skip: !shouldSubscribe || statusRetryCount >= MAX_RETRY_ATTEMPTS,
    onSubscriptionData: ({ subscriptionData }: any) => {
      // Reset retry count on successful data
      if (subscriptionData.data) {
        setStatusRetryCount(0);
        if (onStatusUpdate) {
          onStatusUpdate(subscriptionData.data);
        }
      }
    },
    onError: (error: Error) => {
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
    shouldResubscribe: true,
  }), [subscriptionDeviceId, shouldSubscribe, onStatusUpdate, statusRetryCount]);

  // Subscribe to device status updates
  const { 
    data: statusUpdates, 
    loading: statusLoading, 
    error: statusError 
  } = useSubscription(DEVICE_STATUS_UPDATES_SUBSCRIPTION, statusSubscriptionOptions);


  // Memoize ping subscription options to prevent unnecessary resubscriptions
  const pingSubscriptionOptions = useMemo(() => ({
    skip: !shouldSubscribe || pingRetryCount >= MAX_RETRY_ATTEMPTS,
    onSubscriptionData: ({ subscriptionData }: any) => {
      // Reset retry count on successful data
      if (subscriptionData.data) {
        setPingRetryCount(0);
        if (onPingUpdate) {
          onPingUpdate(subscriptionData.data);
        }
      }
    },
    onError: (error: Error) => {
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
    shouldResubscribe: true,
  }), [shouldSubscribe, onPingUpdate, pingRetryCount]);

  // Subscribe to ping updates
  const pingSubscriptionResult = useSubscription(PING_UPDATES_SUBSCRIPTION, pingSubscriptionOptions);
  
  const { data: pingUpdates, loading: pingLoading, error: pingError } = pingSubscriptionResult;
  

  return {
    statusUpdates: statusUpdates?.deviceStatusUpdates,
    pingUpdates: pingUpdates?.pingUpdates,
    loading: statusLoading || pingLoading,
    error: statusError || pingError,
  };
};