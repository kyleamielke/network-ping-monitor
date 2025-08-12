import React, { useState } from 'react';
import { useParams } from 'react-router-dom';
import { Box, Grid, Alert, CircularProgress, Fade, Grow } from '@mui/material';
import { useDeviceDetail } from '@/features/device-detail/hooks/useDeviceDetail';
import { useDeviceOperations } from '@/features/device-detail/hooks/useDeviceOperations';
import { useDeviceSubscriptions } from '@/shared/hooks/useDeviceSubscriptions';
import { ConfirmDialog } from '@/shared/components/ConfirmDialog';
import { Breadcrumbs } from '@/shared/components/Breadcrumbs';
// import { NavigationState } from '@/shared/types/navigation.types';
import { useBreadcrumbNavigation } from '@/shared/hooks/useBreadcrumbNavigation';
import { DeviceDetailHeader } from '@/features/device-detail/components/DeviceDetailHeader';
import { DeviceInfoCard } from '@/features/device-detail/components/DeviceInfoCard';
import { MonitoringStatusCard } from '@/features/device-detail/components/MonitoringStatusCard';
import { StatisticsCard } from '@/features/device-detail/components/StatisticsCard';
import { RecentAlertsCard } from '@/features/device-detail/components/RecentAlertsCard';
import { DeviceForm } from '@/features/devices/components/DeviceForm';
import { PingHistoryChart } from '@/shared/components/charts/PingHistoryChart';

export const DeviceDetailPage: React.FC = () => {
  const { deviceId } = useParams<{ deviceId: string }>();
  // const location = useLocation();
  const { getBreadcrumbs } = useBreadcrumbNavigation();
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);

  const { deviceMonitoring, loading, error, refetch } = useDeviceDetail(deviceId);
  const { deleteDevice, startMonitoring, stopMonitoring } = useDeviceOperations(deviceId);

  // Subscribe to device status updates
  useDeviceSubscriptions({
    deviceId,
    onStatusUpdate: (update) => {
      if (update?.deviceStatusUpdates?.deviceId === deviceId) {
        refetch();
      }
    },
  });

  const handleDelete = async () => {
    try {
      await deleteDevice();
    } catch (err) {
      console.error('Failed to delete device:', err);
    }
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress />
      </Box>
    );
  }

  if (error || !deviceMonitoring) {
    return (
      <Box p={2}>
        <Alert severity="error">
          {error ? `Error loading device: ${error.message}` : 'Device not found'}
        </Alert>
      </Box>
    );
  }

  const { device, currentStatus, pingTarget, statistics, recentAlerts } = deviceMonitoring;
  const isMonitoring = pingTarget?.monitored ?? false;

  return (
    <Fade in={true} timeout={500}>
      <Box>
        <Breadcrumbs 
        items={getBreadcrumbs([
          { label: 'Dashboard', path: '/dashboard' },
          { label: 'Devices', path: '/devices' },
          { label: device.name }
        ])}
      />
      <DeviceDetailHeader
        device={device}
        currentStatus={currentStatus}
        isMonitoring={isMonitoring}
        onRefresh={refetch}
        onStartMonitoring={startMonitoring}
        onStopMonitoring={stopMonitoring}
        onEdit={() => setEditDialogOpen(true)}
        onDelete={() => setDeleteDialogOpen(true)}
      />

      <Grid container spacing={3}>
        <Grid item xs={12} md={6}>
          <Grow in={true} timeout={600}>
            <div>
              <DeviceInfoCard device={device} />
            </div>
          </Grow>
        </Grid>

        {currentStatus && (
          <Grid item xs={12} md={6}>
            <Grow in={true} timeout={800}>
              <div>
                <MonitoringStatusCard status={currentStatus} />
              </div>
            </Grow>
          </Grid>
        )}

        {statistics && (
          <Grid item xs={12}>
            <Grow in={true} timeout={1000}>
              <div>
                <StatisticsCard statistics={statistics} />
              </div>
            </Grow>
          </Grid>
        )}

        {/* Ping History Chart - Always show if device exists */}
        <Grid item xs={12}>
          <Grow in={true} timeout={1200}>
            <div>
              <PingHistoryChart 
                deviceId={deviceId!}
                deviceName={device.name}
                height={400}
              />
            </div>
          </Grow>
        </Grid>

        <Grid item xs={12}>
          <Grow in={true} timeout={1400}>
            <div>
              <RecentAlertsCard alerts={recentAlerts || []} deviceName={device.name} />
            </div>
          </Grow>
        </Grid>
      </Grid>

      {/* Edit Dialog */}
      <DeviceForm
        open={editDialogOpen}
        onClose={() => setEditDialogOpen(false)}
        device={device}
        isEditing={true}
        onSave={() => {
          setEditDialogOpen(false);
          refetch();
        }}
      />

      {/* Delete Confirmation */}
      <ConfirmDialog
        open={deleteDialogOpen}
        title="Delete Device"
        message={`Are you sure you want to delete ${device.name}? This action cannot be undone.`}
        confirmText="Delete"
        confirmColor="error"
        onConfirm={handleDelete}
        onCancel={() => setDeleteDialogOpen(false)}
      />
      </Box>
    </Fade>
  );
};