import { useQuery } from '@apollo/client';
import { DEVICE_QUERIES } from '@/features/devices/api/deviceQueries';
import { Device, DeviceWithMonitoring } from '@/shared/types/device.types';

interface UseDevicesOptions {
  withMonitoring?: boolean;
  pollInterval?: number;
}

export const useDevices = (options: UseDevicesOptions = {}) => {
  const { withMonitoring = true, pollInterval } = options;

  const query = withMonitoring
    ? DEVICE_QUERIES.LIST_WITH_MONITORING
    : DEVICE_QUERIES.LIST;

  const {
    data,
    loading,
    error,
    refetch,
  } = useQuery(query, {
    fetchPolicy: 'cache-and-network',
    pollInterval,
  });

  const devices = withMonitoring
    ? data?.devicesWithMonitoring || []
    : data?.devices || [];

  return {
    devices: devices as (Device[] | DeviceWithMonitoring[]),
    loading,
    error,
    refetch,
  };
};

// Typed hook for devices with monitoring
export const useDevicesWithMonitoring = (pollInterval?: number) => {
  const {
    data,
    loading,
    error,
    refetch,
  } = useQuery(DEVICE_QUERIES.LIST_WITH_MONITORING, {
    fetchPolicy: 'cache-and-network',
    pollInterval,
  });

  return {
    devices: (data?.devicesWithMonitoring || []) as DeviceWithMonitoring[],
    loading,
    error,
    refetch,
  };
};