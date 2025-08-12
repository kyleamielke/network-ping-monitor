import React from 'react';
import { useNavigate } from 'react-router-dom';
import {
  CheckCircle as AcknowledgeIcon,
  Done as ResolveIcon,
  Delete as DeleteIcon,
} from '@mui/icons-material';
import { DataTable, Column, Action } from '@/shared/components/DataTable';
import { StatusChip } from '@/shared/components/StatusChip';
import { Alert, AlertType } from '@/shared/types/alert.types';
import { useLocale } from '@/shared/contexts/LocaleContext';

interface AlertListProps {
  alerts: Alert[];
  loading: boolean;
  error: Error | null;
  onAcknowledge: (alert: Alert) => void;
  onResolve: (alert: Alert) => void;
  onDelete: (alert: Alert) => void;
}

const getAlertTypeColor = (alertType: AlertType | string | undefined) => {
  if (!alertType) return 'default';
  switch (alertType) {
    case AlertType.DEVICE_DOWN:
    case 'DEVICE_DOWN':
      return 'error';
    case AlertType.DEVICE_RECOVERED:
    case 'DEVICE_RECOVERED':
      return 'success';
    case AlertType.HIGH_RESPONSE_TIME:
    case 'HIGH_RESPONSE_TIME':
      return 'warning';
    case AlertType.PACKET_LOSS:
    case 'PACKET_LOSS':
      return 'warning';
    default:
      return 'default';
  }
};

export const AlertList: React.FC<AlertListProps> = ({
  alerts,
  loading,
  error,
  onAcknowledge,
  onResolve,
  onDelete,
}) => {
  const navigate = useNavigate();
  const { formatDate } = useLocale();
  const columns: Column<Alert>[] = [
    {
      id: 'deviceName',
      label: 'Device',
      sortable: true,
    },
    {
      id: 'alertType',
      label: 'Type',
      sortable: true,
      filterable: true,
      filterOptions: [
        { value: 'DEVICE_DOWN', label: 'Device Down' },
        { value: 'DEVICE_RECOVERED', label: 'Device Recovered' },
        { value: 'HIGH_RESPONSE_TIME', label: 'High Response Time' },
        { value: 'PACKET_LOSS', label: 'Packet Loss' },
      ],
      format: (value) => <StatusChip status={getAlertTypeColor(value)} label={value} />,
    },
    {
      id: 'message',
      label: 'Message',
      sortable: true,
    },
    {
      id: 'createdAt',
      label: 'Created',
      sortable: true,
      format: (value) => formatDate(value, true),
    },
    {
      id: 'status',
      label: 'Status',
      sortable: true,
      filterable: true,
      filterOptions: [
        { value: 'active', label: 'Active' },
        { value: 'acknowledged', label: 'Acknowledged' },
        { value: 'resolved', label: 'Resolved' },
      ],
      filterValue: (row) => {
        if (row.resolved) return 'resolved';
        if (row.acknowledged) return 'acknowledged';
        return 'active';
      },
      sortValue: (row) => {
        if (row.resolved) return 2; // Resolved sorts last
        if (row.acknowledged) return 1; // Acknowledged in middle
        return 0; // Active sorts first
      },
      format: (_, row) => {
        if (row.resolved) {
          return <StatusChip status="success" label="Resolved" />;
        }
        if (row.acknowledged) {
          return <StatusChip status="warning" label="Acknowledged" />;
        }
        return <StatusChip status="error" label="Active" />;
      },
    },
  ];

  const actions: Action<Alert>[] = [
    {
      icon: <AcknowledgeIcon />,
      label: 'Acknowledge',
      onClick: onAcknowledge,
      color: 'primary',
      disabled: (row) => row.acknowledged || row.resolved,
    },
    {
      icon: <ResolveIcon />,
      label: 'Resolve',
      onClick: onResolve,
      color: 'success',
      disabled: (row) => row.resolved,
    },
    {
      icon: <DeleteIcon />,
      label: 'Delete',
      onClick: onDelete,
      color: 'error',
    },
  ];

  return (
    <DataTable
      data={alerts}
      columns={columns}
      actions={actions}
      loading={loading}
      error={error}
      emptyMessage="No alerts found"
      defaultRowsPerPage={25}
      rowsPerPageOptions={[10, 25, 50, 100]}
      onRowClick={(alert) => navigate(`/alerts/${alert.id}`)}
    />
  );
};