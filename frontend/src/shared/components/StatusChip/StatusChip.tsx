import React from 'react';
import { Chip, ChipProps } from '@mui/material';

export type StatusType = 'online' | 'offline' | 'monitoring' | 'inactive' | 'error' | 'warning' | 'success';

interface StatusChipProps {
  status: StatusType | string;
  label?: string;
  size?: ChipProps['size'];
}

const statusConfig: Record<StatusType, { label: string; color: ChipProps['color'] }> = {
  online: { label: 'Online', color: 'success' },
  offline: { label: 'Offline', color: 'error' },
  monitoring: { label: 'Monitoring', color: 'primary' },
  inactive: { label: 'Inactive', color: 'default' },
  error: { label: 'Error', color: 'error' },
  warning: { label: 'Warning', color: 'warning' },
  success: { label: 'Success', color: 'success' },
};

export const StatusChip: React.FC<StatusChipProps> = React.memo(({
  status,
  label,
  size = 'small',
}) => {
  const config = statusConfig[status as StatusType] || {
    label: label || status,
    color: 'default' as ChipProps['color'],
  };

  return (
    <Chip
      label={label || config.label}
      color={config.color}
      size={size}
      sx={{
        transition: 'all 0.3s ease',
        '&:hover': {
          transform: 'scale(1.05)',
          boxShadow: 1,
        }
      }}
    />
  );
});