import React, { useCallback, useState } from 'react';
import { Button, Grow } from '@mui/material';
import { Add as AddIcon } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { useMutation } from '@apollo/client';
import { PageLayout } from '@/shared/components/PageLayout';
import { DeviceList } from '@/features/devices/components/DeviceList';
import { DeviceForm } from '@/features/devices/components/DeviceForm';
import { BulkMonitoringControls } from '@/features/devices/components/BulkMonitoringControls';
import { BulkEditDialog, BulkEditUpdates } from '@/features/devices/components/BulkEditDialog';
import { ConfirmDialog } from '@/shared/components/ConfirmDialog';
import { useDevicesWithMonitoring } from '@/features/devices/hooks/useDevices';
import { useDeviceOperations } from '@/features/devices/hooks/useDeviceOperations';
import { useDeviceSubscriptions } from '@/shared/hooks/useDeviceSubscriptions';
import { useDialog } from '@/shared/hooks/useDialog';
import { Device, DeviceWithMonitoring } from '@/shared/types/device.types';
import { useLocale } from '@/shared/contexts/LocaleContext';
import { DEVICE_MUTATIONS } from '@/features/devices/api/deviceMutations';
import { useToast } from '@/shared/contexts/ToastContext';

export const DevicesPage: React.FC = () => {
  const navigate = useNavigate();
  const { devices, loading, error, refetch } = useDevicesWithMonitoring();
  const { startMonitoring, stopMonitoring } = useDeviceOperations();
  const deviceDialog = useDialog<DeviceWithMonitoring | null>(null);
  const { t } = useLocale();
  const { showToast } = useToast();
  const [filteredDevices, setFilteredDevices] = useState<DeviceWithMonitoring[]>([]);
  const [selectedDevices, setSelectedDevices] = useState<string[]>([]);
  const [bulkEditOpen, setBulkEditOpen] = useState(false);
  
  const [bulkUpdateDevices] = useMutation(DEVICE_MUTATIONS.BULK_UPDATE);
  const [deleteDevice] = useMutation(DEVICE_MUTATIONS.DELETE);
  const [deviceToDelete, setDeviceToDelete] = useState<DeviceWithMonitoring | null>(null);

  // Subscribe to real-time updates
  useDeviceSubscriptions();

  // Memoize event handlers to prevent unnecessary re-renders of child components
  const handleDeviceClick = useCallback((device: Device) => {
    navigate(`/devices/${device.id}`);
  }, [navigate]);

  const handleEdit = useCallback((device: Device) => {
    deviceDialog.openDialog(device);
  }, [deviceDialog]);

  const handleCreate = useCallback(() => {
    deviceDialog.openDialog(null);
  }, [deviceDialog]);

  const handleSave = useCallback(() => {
    deviceDialog.closeDialog();
    refetch();
  }, [deviceDialog, refetch]);

  const handleToggleMonitoring = useCallback(async (device: DeviceWithMonitoring) => {
    if (device.pingTarget?.monitored) {
      await stopMonitoring(device.id);
    } else {
      await startMonitoring(device.id);
    }
    refetch();
  }, [startMonitoring, stopMonitoring, refetch]);

  const handleDelete = useCallback((device: DeviceWithMonitoring) => {
    setDeviceToDelete(device);
  }, []);

  const handleConfirmDelete = useCallback(async () => {
    if (!deviceToDelete) return;

    try {
      await deleteDevice({
        variables: {
          deviceId: deviceToDelete.id
        }
      });
      
      showToast(`Device "${deviceToDelete.name}" deleted successfully`, 'success');
      setDeviceToDelete(null);
      refetch();
    } catch (error) {
      showToast(
        `Failed to delete device: ${error instanceof Error ? error.message : 'Unknown error'}`,
        'error'
      );
    }
  }, [deviceToDelete, deleteDevice, showToast, refetch]);

  const handleCancelDelete = useCallback(() => {
    setDeviceToDelete(null);
  }, []);

  const handleBulkEdit = useCallback(() => {
    setBulkEditOpen(true);
  }, []);

  const handleBulkEditSave = useCallback(async (updates: BulkEditUpdates) => {
    try {
      const result = await bulkUpdateDevices({
        variables: {
          input: {
            deviceIds: selectedDevices,
            updates
          }
        }
      });

      const data = result.data.bulkUpdateDevices;
      
      showToast(
        `Successfully updated ${data.successful} devices${data.failed > 0 ? `, ${data.failed} failed` : ''}`,
        data.failed > 0 ? 'warning' : 'success'
      );

      // Clear selection after successful update
      setSelectedDevices([]);
      setBulkEditOpen(false);
      refetch();
    } catch (error) {
      showToast(
        `Error updating devices: ${error instanceof Error ? error.message : 'Unknown error'}`,
        'error'
      );
    }
  }, [bulkUpdateDevices, selectedDevices, showToast, refetch]);

  return (
    <PageLayout
      title={t('devices.title')}
      subtitle={t('devices.subtitle', { count: devices.length })}
      onRefresh={refetch}
      actions={
        <Grow in={true} timeout={600}>
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={handleCreate}
            sx={{
              transition: 'all 0.3s ease',
              '&:hover': {
                transform: 'translateY(-2px)',
                boxShadow: 4,
              }
            }}
          >
            {t('devices.addDevice')}
          </Button>
        </Grow>
      }
    >
      <BulkMonitoringControls 
        allDevices={devices} 
        filteredDevices={filteredDevices} 
        selectedDevices={selectedDevices}
        onRefresh={refetch}
        onSelectionClear={() => setSelectedDevices([])}
        onBulkEdit={handleBulkEdit}
      />
      
      <DeviceList
        devices={devices}
        loading={loading}
        error={error || null}
        onDeviceClick={handleDeviceClick}
        onEdit={handleEdit}
        onDelete={handleDelete}
        onToggleMonitoring={handleToggleMonitoring}
        onFilteredDataChange={setFilteredDevices}
        selectable={true}
        selectedDevices={selectedDevices}
        onSelectionChange={setSelectedDevices}
      />

      <DeviceForm
        open={deviceDialog.open}
        device={deviceDialog.data}
        isEditing={!!deviceDialog.data}
        onClose={deviceDialog.closeDialog}
        onSave={handleSave}
      />

      <ConfirmDialog
        open={!!deviceToDelete}
        title="Delete Device"
        message={`Are you sure you want to delete "${deviceToDelete?.name}"? This will permanently remove the device and all associated monitoring data.`}
        confirmText="Delete"
        cancelText="Cancel"
        onConfirm={handleConfirmDelete}
        onCancel={handleCancelDelete}
      />

      <BulkEditDialog
        open={bulkEditOpen}
        selectedDevices={devices.filter(d => selectedDevices.includes(d.id))}
        onClose={() => setBulkEditOpen(false)}
        onSave={handleBulkEditSave}
      />
    </PageLayout>
  );
};