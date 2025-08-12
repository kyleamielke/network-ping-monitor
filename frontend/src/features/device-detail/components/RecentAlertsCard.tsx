import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Card, CardContent, Typography, List, ListItem, ListItemText, Chip, Box, Divider } from '@mui/material';
import { formatDistanceToNow } from 'date-fns';
import { Alert, AlertType } from '@/shared/types/alert.types';

interface RecentAlertsCardProps {
  alerts: Alert[];
  deviceName?: string;
}

const getAlertColor = (alertType: AlertType | string) => {
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

export const RecentAlertsCard: React.FC<RecentAlertsCardProps> = ({ alerts, deviceName }) => {
  const navigate = useNavigate();

  if (!alerts || alerts.length === 0) {
    return (
      <Card>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            Recent Alerts
          </Typography>
          <Divider sx={{ mb: 2 }} />
          <Typography variant="body2" color="textSecondary" align="center" py={3}>
            No recent alerts
          </Typography>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card>
      <CardContent>
        <Typography variant="h6" gutterBottom>
          Recent Alerts
        </Typography>
        <Divider sx={{ mb: 2 }} />
        <List disablePadding>
          {alerts.map((alert, index) => (
            <React.Fragment key={alert.id}>
              <ListItem 
                alignItems="flex-start" 
                disableGutters
                onClick={() => navigate(`/alerts/${alert.id}`, {
                  state: {
                    from: 'device',
                    breadcrumbs: [
                      { label: 'Dashboard', path: '/dashboard' },
                      { label: 'Devices', path: '/devices' },
                      { label: deviceName || 'Device', path: window.location.pathname },
                      { label: 'Alert Details' }
                    ]
                  }
                })}
                sx={{ 
                  cursor: 'pointer',
                  borderRadius: 1,
                  transition: 'all 0.2s ease',
                  '&:hover': {
                    backgroundColor: 'action.hover',
                    transform: 'translateX(4px)',
                  }
                }}
              >
                <ListItemText
                  primary={
                    <Box display="flex" alignItems="center" gap={1}>
                      <Chip 
                        label={alert.alertType} 
                        size="small" 
                        color={getAlertColor(alert.alertType)}
                      />
                      {alert.resolved && (
                        <Chip label="Resolved" size="small" color="success" variant="outlined" />
                      )}
                    </Box>
                  }
                  secondary={
                    <>
                      <Typography variant="body2" component="span">
                        {alert.message}
                      </Typography>
                      <Typography variant="caption" component="div" color="textSecondary">
                        {formatDistanceToNow(new Date(alert.timestamp), { addSuffix: true })}
                      </Typography>
                    </>
                  }
                />
              </ListItem>
              {index < alerts.length - 1 && <Divider component="li" />}
            </React.Fragment>
          ))}
        </List>
      </CardContent>
    </Card>
  );
};