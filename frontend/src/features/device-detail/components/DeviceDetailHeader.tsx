import React from 'react';
import { Box, Typography, Chip, IconButton, Button, Tooltip } from '@mui/material';
import {
  ArrowBack as ArrowBackIcon,
  Refresh as RefreshIcon,
  PlayArrow as PlayIcon,
  Stop as StopIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  CheckCircle as CheckCircleIcon,
  Cancel as CancelIcon,
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { Device } from '@/shared/types/device.types';
import { DeviceStatus } from '@/shared/types/monitoring.types';

interface DeviceDetailHeaderProps {
  device: Device;
  currentStatus?: DeviceStatus;
  isMonitoring: boolean;
  onRefresh: () => void;
  onStartMonitoring: () => void;
  onStopMonitoring: () => void;
  onEdit: () => void;
  onDelete: () => void;
}

export const DeviceDetailHeader: React.FC<DeviceDetailHeaderProps> = ({
  device,
  currentStatus,
  isMonitoring,
  onRefresh,
  onStartMonitoring,
  onStopMonitoring,
  onEdit,
  onDelete,
}) => {
  const navigate = useNavigate();

  return (
    <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
      <Box display="flex" alignItems="center" gap={2}>
        <IconButton onClick={() => navigate(-1)} size="large">
          <ArrowBackIcon />
        </IconButton>
        <Typography variant="h4" component="h1">
          {device.name}
        </Typography>
        {currentStatus && (
          <Chip
            icon={currentStatus.online ? <CheckCircleIcon /> : <CancelIcon />}
            label={currentStatus.online ? 'Online' : 'Offline'}
            color={currentStatus.online ? 'success' : 'error'}
            size="medium"
          />
        )}
      </Box>
      <Box display="flex" gap={1}>
        <Tooltip title="Refresh">
          <IconButton onClick={onRefresh}>
            <RefreshIcon />
          </IconButton>
        </Tooltip>
        {isMonitoring ? (
          <Button
            variant="outlined"
            color="error"
            startIcon={<StopIcon />}
            onClick={onStopMonitoring}
          >
            Stop Monitoring
          </Button>
        ) : (
          <Button
            variant="contained"
            color="primary"
            startIcon={<PlayIcon />}
            onClick={onStartMonitoring}
          >
            Start Monitoring
          </Button>
        )}
        <Button
          variant="outlined"
          startIcon={<EditIcon />}
          onClick={onEdit}
        >
          Edit
        </Button>
        <Button
          variant="outlined"
          color="error"
          startIcon={<DeleteIcon />}
          onClick={onDelete}
        >
          Delete
        </Button>
      </Box>
    </Box>
  );
};