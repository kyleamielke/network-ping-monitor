import React, { useMemo, useCallback } from 'react';
import {
  PlayArrow as PlayIcon,
  Stop as StopIcon,
  Edit as EditIcon,
  Visibility as ViewIcon,
  Delete as DeleteIcon,
} from '@mui/icons-material';
import { DataTable, Column, Action } from '@/shared/components/DataTable';
import { StatusChip } from '@/shared/components/StatusChip';
import { DeviceWithMonitoring } from '@/shared/types/device.types';

interface DeviceListProps {
  devices: DeviceWithMonitoring[];
  loading: boolean;
  error: Error | null;
  onDeviceClick: (device: DeviceWithMonitoring) => void;
  onEdit: (device: DeviceWithMonitoring) => void;
  onDelete: (device: DeviceWithMonitoring) => void;
  onToggleMonitoring: (device: DeviceWithMonitoring) => void;
  onFilteredDataChange?: (filteredData: DeviceWithMonitoring[]) => void;
  selectable?: boolean;
  selectedDevices?: string[];
  onSelectionChange?: (selectedIds: string[]) => void;
}

export const DeviceList: React.FC<DeviceListProps> = React.memo(({
  devices,
  loading,
  error,
  onDeviceClick,
  onEdit,
  onDelete,
  onToggleMonitoring,
  onFilteredDataChange,
  selectable = false,
  selectedDevices = [],
  onSelectionChange,
}) => {
  // Memoize event handlers to prevent unnecessary re-renders
  const handleDeviceClick = useCallback((device: DeviceWithMonitoring) => {
    onDeviceClick(device);
  }, [onDeviceClick]);

  const handleEdit = useCallback((device: DeviceWithMonitoring) => {
    onEdit(device);
  }, [onEdit]);

  const handleDelete = useCallback((device: DeviceWithMonitoring) => {
    onDelete(device);
  }, [onDelete]);

  const handleToggleMonitoring = useCallback((device: DeviceWithMonitoring) => {
    onToggleMonitoring(device);
  }, [onToggleMonitoring]);

  // Memoize columns configuration to prevent DataTable re-renders
  const columns = useMemo<Column<DeviceWithMonitoring>[]>(() => [
    {
      id: 'name',
      label: 'Name',
      sortable: true,
      format: (value, row) => (
        <>
          {value}
          {row.endpointId && (
            <div style={{ fontSize: '0.85em', color: 'text.secondary' }}>
              {row.endpointId}
            </div>
          )}
        </>
      ),
    },
    {
      id: 'ipAddress',
      label: 'Network Address',
      sortable: true,
      sortValue: (row) => row.hostname || row.ipAddress || '',
      format: (value, row) => {
        if (row.hostname && value) {
          return (
            <>
              <div>{row.hostname}</div>
              <div style={{ fontSize: '0.85em', color: 'text.secondary' }}>
                {value}
              </div>
            </>
          );
        }
        return row.hostname || value || '-';
      },
    },
    {
      id: 'type',
      label: 'Type',
      sortable: true,
      filterable: true,
      format: (value) => value || '-',
    },
    {
      id: 'os',
      label: 'Operating System',
      sortable: true,
      format: (value, row) => {
        if (!value) return '-';
        return row.osType ? `${value} (${row.osType})` : value;
      },
    },
    {
      id: 'assetTag',
      label: 'Asset Tag',
      sortable: true,
      format: (value) => value || '-',
    },
    {
      id: 'status',
      label: 'Status',
      sortable: true,
      filterable: true,
      filterOptions: [
        { value: 'online', label: 'Online' },
        { value: 'offline', label: 'Offline' },
        { value: 'not_monitored', label: 'Not Monitored' },
      ],
      filterValue: (row) => {
        const isMonitoring = row.pingTarget?.monitored ?? false;
        if (!isMonitoring) return 'not_monitored';
        return row.currentStatus?.online ? 'online' : 'offline';
      },
      sortValue: (row) => {
        const isMonitoring = row.pingTarget?.monitored ?? false;
        if (!isMonitoring) return 0; // Not monitored sorts first
        return row.currentStatus?.online ? 2 : 1; // Online sorts last, offline in middle
      },
      format: (_, row) => {
        const isMonitoring = row.pingTarget?.monitored ?? false;
        if (!isMonitoring) {
          return <StatusChip status="inactive" />;
        }
        return row.currentStatus?.online ? (
          <StatusChip status="online" />
        ) : (
          <StatusChip status="offline" />
        );
      },
    },
  ], []); // Empty dependency array since columns don't depend on props

  // Memoize actions configuration to prevent DataTable re-renders
  const actions = useMemo<Action<DeviceWithMonitoring>[]>(() => [
    {
      icon: (row: DeviceWithMonitoring) => row.pingTarget?.monitored ? <StopIcon /> : <PlayIcon />,
      label: (row: DeviceWithMonitoring) => row.pingTarget?.monitored ? 'Stop Monitoring' : 'Start Monitoring',
      onClick: handleToggleMonitoring,
      color: (row: DeviceWithMonitoring) => (row.pingTarget?.monitored ? 'warning' : 'success') as 'warning' | 'success',
    },
    {
      icon: <EditIcon />,
      label: 'Edit',
      onClick: handleEdit,
    },
    {
      icon: <ViewIcon />,
      label: 'View Details',
      onClick: handleDeviceClick,
      color: 'primary',
    },
    {
      icon: <DeleteIcon />,
      label: 'Delete',
      onClick: handleDelete,
      color: 'error',
    },
  ], [handleToggleMonitoring, handleEdit, handleDeviceClick, handleDelete]); // Depend on memoized handlers

  return (
    <DataTable
      data={devices}
      columns={columns}
      actions={actions}
      loading={loading}
      error={error}
      onRowClick={handleDeviceClick}
      emptyMessage="No devices found. Add your first device to get started."
      stickyHeader
      onFilteredDataChange={onFilteredDataChange}
      selectable={selectable}
      selectedRows={selectedDevices}
      onSelectionChange={onSelectionChange}
    />
  );
});