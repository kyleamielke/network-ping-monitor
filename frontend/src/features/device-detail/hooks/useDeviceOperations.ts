import { useMutation } from '@apollo/client';
import { useNavigate } from 'react-router-dom';
import { DELETE_DEVICE, START_MONITORING, STOP_MONITORING } from '@/features/device-detail/api/deviceDetailMutations';
import { GET_ALL_DEVICES } from '@/shared/api/deviceQueries';
import { DEVICE_DETAIL_QUERY } from '@/features/device-detail/api/deviceDetailQueries';

export const useDeviceOperations = (deviceId: string | undefined) => {
  const navigate = useNavigate();

  const [deleteDeviceMutation] = useMutation(DELETE_DEVICE, {
    update(cache) {
      if (deviceId) {
        cache.evict({ id: `Device:${deviceId}` });
        cache.gc();
      }
    },
    refetchQueries: [{ query: GET_ALL_DEVICES }],
    onCompleted: () => {
      navigate('/devices');
    },
  });

  const [startMonitoringMutation] = useMutation(START_MONITORING, {
    refetchQueries: deviceId ? [{ query: DEVICE_DETAIL_QUERY, variables: { deviceId } }] : [],
  });

  const [stopMonitoringMutation] = useMutation(STOP_MONITORING, {
    refetchQueries: deviceId ? [{ query: DEVICE_DETAIL_QUERY, variables: { deviceId } }] : [],
  });

  const deleteDevice = async () => {
    if (!deviceId) return;
    try {
      await deleteDeviceMutation({ variables: { deviceId } });
    } catch (err) {
      console.error('Failed to delete device:', err);
      throw err;
    }
  };

  const startMonitoring = async () => {
    if (!deviceId) return;
    try {
      await startMonitoringMutation({ variables: { deviceId } });
    } catch (err) {
      console.error('Failed to start monitoring:', err);
      throw err;
    }
  };

  const stopMonitoring = async () => {
    if (!deviceId) return;
    try {
      await stopMonitoringMutation({ variables: { deviceId } });
    } catch (err) {
      console.error('Failed to stop monitoring:', err);
      throw err;
    }
  };

  return {
    deleteDevice,
    startMonitoring,
    stopMonitoring,
  };
};