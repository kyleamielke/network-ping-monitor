import React from 'react';
import {
  Card,
  CardContent,
  Typography,
  Box,
  Chip,
  LinearProgress,
  Tooltip,
} from '@mui/material';
import {
  CheckCircle as OnlineIcon,
  Cancel as OfflineIcon,
} from '@mui/icons-material';
import { Device } from '@/shared/types/device.types';
import { DeviceStatus, PingTarget } from '@/shared/types/monitoring.types';
import { MiniPingIndicator } from './MiniPingIndicator';
import { useLocale } from '@/shared/contexts/LocaleContext';

interface DeviceStatusCardProps {
  device: Device;
  status?: DeviceStatus;
  pingTarget?: PingTarget;
  recentPings?: {
    timestamp: string;
    success: boolean;
    responseTimeMs?: number;
  }[];
  onClick?: () => void;
  selected?: boolean;
  clickable?: boolean;
}

export const DeviceStatusCard: React.FC<DeviceStatusCardProps> = React.memo(({
  device,
  status,
  pingTarget,
  recentPings,
  onClick,
  selected = false,
  clickable = false,
}) => {
  const { formatDate, formatRelativeTime } = useLocale();
  const isOnline = status?.online || false;
  const isMonitored = pingTarget?.monitored || false;

  const cardSx = {
    height: '100%',
    cursor: clickable ? 'pointer' : 'default',
    transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
    border: selected ? 2 : 1,
    borderColor: selected ? 'primary.main' : 'divider',
    transform: selected ? 'translateY(-4px)' : 'none',
    boxShadow: selected ? 6 : 1,
    backgroundColor: 'background.paper',
    position: 'relative' as const,
    overflow: 'hidden',
    '&:hover': clickable ? {
      transform: 'translateY(-4px)',
      boxShadow: 6,
      borderColor: 'primary.light',
      '& .status-indicator': {
        transform: 'scale(1.1)',
      }
    } : {},
    '&:active': clickable ? {
      transform: 'translateY(-2px)',
      boxShadow: 3,
    } : {},
  };

  return (
    <Card 
      variant="outlined" 
      sx={cardSx}
      onClick={clickable ? onClick : undefined}
      tabIndex={clickable ? 0 : -1}
      onKeyDown={clickable ? (e) => {
        if (e.key === 'Enter' || e.key === ' ') {
          e.preventDefault();
          onClick?.();
        }
      } : undefined}
      role={clickable ? "button" : undefined}
      aria-label={clickable ? `Device ${device.name}, Status: ${isOnline ? 'Online' : 'Offline'}${isMonitored ? ', Monitoring enabled' : ', Monitoring disabled'}` : undefined}
      aria-describedby={`device-${device.id}-status`}
    >
      <CardContent>
        <Box display="flex" justifyContent="space-between" alignItems="flex-start" mb={2}>
          <Box>
            <Typography variant="h6" component="h3" gutterBottom>
              {device.name}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              {isMonitored && pingTarget ? (
                pingTarget.hostname || pingTarget.ipAddress || 'No address'
              ) : (
                device.hostname || device.ipAddress || 'No address'
              )}
            </Typography>
          </Box>
          <Box display="flex" alignItems="center" gap={1}>
            {isMonitored ? (
              <Chip
                className="status-indicator"
                icon={isOnline ? <OnlineIcon /> : <OfflineIcon />}
                label={isOnline ? 'Online' : 'Offline'}
                color={isOnline ? 'success' : 'error'}
                size="small"
                sx={{
                  transition: 'transform 0.2s ease-in-out',
                  fontWeight: 500,
                }}
              />
            ) : (
              <Chip
                className="status-indicator"
                label="Not Monitored"
                color="default"
                size="small"
                sx={{
                  transition: 'transform 0.2s ease-in-out',
                }}
              />
            )}
          </Box>
        </Box>

        {status && isMonitored && (
          <>
            <Box mb={2}>
              <Box display="flex" justifyContent="space-between" mb={0.5}>
                <Typography variant="body2" color="text.secondary">
                  Response Time
                </Typography>
                <Typography variant="body2">
                  {status.responseTime ? `${status.responseTime}ms` : 'N/A'}
                </Typography>
              </Box>
              {status.responseTime && (
                <LinearProgress
                  variant="determinate"
                  value={Math.min((status.responseTime / 200) * 100, 100)}
                  color={status.responseTime < 100 ? 'success' : status.responseTime < 200 ? 'warning' : 'error'}
                  aria-label={`Response time: ${status.responseTime}ms`}
                  aria-valuenow={status.responseTime}
                  aria-valuemin={0}
                  aria-valuemax={200}
                  sx={{ 
                    height: 6, 
                    borderRadius: 3,
                    backgroundColor: 'action.hover',
                    '& .MuiLinearProgress-bar': {
                      transition: 'transform 0.4s cubic-bezier(0.4, 0, 0.2, 1)',
                    }
                  }}
                />
              )}
            </Box>

            {/* Mini Ping Indicator */}
            {isMonitored && recentPings && recentPings.length > 0 && (
              <Box mb={2}>
                <Typography variant="body2" color="text.secondary" sx={{ mb: 0.5 }}>
                  Ping History
                </Typography>
                <Box sx={{ 
                  p: 1, 
                  backgroundColor: 'rgba(0, 0, 0, 0.02)', 
                  borderRadius: 1,
                  border: '1px solid',
                  borderColor: 'divider',
                }}>
                  <MiniPingIndicator recentPings={recentPings} />
                </Box>
              </Box>
            )}

            <Box display="flex" justifyContent="space-between" mb={1}>
              <Typography variant="body2" color="text.secondary">
                Type
              </Typography>
              <Typography variant="body2">
                {device.type || 'Unknown'}
              </Typography>
            </Box>

            {status.lastStatusChange && (
              <Box display="flex" justifyContent="space-between">
                <Typography variant="body2" color="text.secondary">
                  Last Ping
                </Typography>
                <Tooltip title={formatDate(status.lastStatusChange, true)}>
                  <Typography variant="body2">
                    {formatRelativeTime(status.lastStatusChange)}
                  </Typography>
                </Tooltip>
              </Box>
            )}
          </>
        )}
      </CardContent>
    </Card>
  );
});