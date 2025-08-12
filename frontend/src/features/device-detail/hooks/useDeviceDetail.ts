import { useQuery } from '@apollo/client';
import { DEVICE_DETAIL_QUERY } from '@/features/device-detail/api/deviceDetailQueries';
import { DeviceMonitoring } from '@/shared/types/monitoring.types';

interface UseDeviceDetailResult {
  deviceMonitoring: DeviceMonitoring | null;
  loading: boolean;
  error: any;
  refetch: () => void;
}

export const useDeviceDetail = (deviceId: string | undefined): UseDeviceDetailResult => {
  const { data, loading, error, refetch } = useQuery(DEVICE_DETAIL_QUERY, {
    variables: { deviceId },
    fetchPolicy: 'cache-and-network',
    skip: !deviceId,
  });

  return {
    deviceMonitoring: data?.deviceMonitoring || null,
    loading,
    error,
    refetch,
  };
};