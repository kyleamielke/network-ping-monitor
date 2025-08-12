import { useMutation, useApolloClient } from '@apollo/client';
import { useNavigate } from 'react-router-dom';
import { DEVICE_MUTATIONS, CreateDeviceInput, UpdateDeviceInput } from '@/features/devices/api/deviceMutations';
import { DEVICE_QUERIES } from '@/features/devices/api/deviceQueries';
import { useToast } from '@/shared/contexts/ToastContext';

export const useDeviceOperations = () => {
  const client = useApolloClient();
  const navigate = useNavigate();
  const { showToast } = useToast();

  const [createDeviceMutation, { loading: creating }] = useMutation(
    DEVICE_MUTATIONS.CREATE,
    {
      onCompleted: () => {
        showToast('Device created successfully', 'success');
        // Refetch device lists
        client.refetchQueries({
          include: [DEVICE_QUERIES.LIST, DEVICE_QUERIES.LIST_WITH_MONITORING],
        });
      },
      onError: (error) => {
        showToast(`Failed to create device: ${error.message}`, 'error');
      },
    }
  );

  const [updateDeviceMutation, { loading: updating }] = useMutation(
    DEVICE_MUTATIONS.UPDATE,
    {
      onCompleted: () => {
        showToast('Device updated successfully', 'success');
      },
      onError: (error) => {
        // Check for optimistic locking error
        if (error.graphQLErrors?.some(e => 
          e.extensions?.code === 'OPTIMISTIC_LOCKING_ERROR' ||
          e.message.toLowerCase().includes('optimistic locking')
        )) {
          showToast(
            'This device was modified by another user. Please refresh and try again.',
            'warning'
          );
          // Refetch to get the latest version
          client.refetchQueries({
            include: [DEVICE_QUERIES.LIST, DEVICE_QUERIES.LIST_WITH_MONITORING],
          });
        } else {
          showToast(`Failed to update device: ${error.message}`, 'error');
        }
      },
    }
  );

  const [deleteDeviceMutation, { loading: deleting }] = useMutation(
    DEVICE_MUTATIONS.DELETE,
    {
      onCompleted: () => {
        showToast('Device deleted successfully', 'success');
        navigate('/devices');
        // Refetch device lists
        client.refetchQueries({
          include: [DEVICE_QUERIES.LIST, DEVICE_QUERIES.LIST_WITH_MONITORING],
        });
      },
      onError: (error) => {
        showToast(`Failed to delete device: ${error.message}`, 'error');
      },
    }
  );

  const [startMonitoringMutation, { loading: startingMonitoring }] = useMutation(
    DEVICE_MUTATIONS.START_MONITORING,
    {
      onCompleted: () => {
        showToast('Monitoring started', 'success');
      },
      onError: (error) => {
        showToast(`Failed to start monitoring: ${error.message}`, 'error');
      },
    }
  );

  const [stopMonitoringMutation, { loading: stoppingMonitoring }] = useMutation(
    DEVICE_MUTATIONS.STOP_MONITORING,
    {
      onCompleted: () => {
        showToast('Monitoring stopped', 'success');
      },
      onError: (error) => {
        showToast(`Failed to stop monitoring: ${error.message}`, 'error');
      },
    }
  );

  const createDevice = async (input: CreateDeviceInput) => {
    const result = await createDeviceMutation({
      variables: { input },
    });
    return result.data?.createDevice;
  };

  const updateDevice = async (id: string, input: UpdateDeviceInput) => {
    const result = await updateDeviceMutation({
      variables: { id, input },
    });
    return result.data?.updateDevice;
  };

  const deleteDevice = async (id: string) => {
    await deleteDeviceMutation({
      variables: { id },
    });
  };

  const startMonitoring = async (deviceId: string) => {
    await startMonitoringMutation({
      variables: { deviceId },
      refetchQueries: [DEVICE_QUERIES.LIST_WITH_MONITORING],
    });
  };

  const stopMonitoring = async (deviceId: string) => {
    await stopMonitoringMutation({
      variables: { deviceId },
      refetchQueries: [DEVICE_QUERIES.LIST_WITH_MONITORING],
    });
  };

  return {
    createDevice,
    updateDevice,
    deleteDevice,
    startMonitoring,
    stopMonitoring,
    loading: creating || updating || deleting || startingMonitoring || stoppingMonitoring,
  };
};