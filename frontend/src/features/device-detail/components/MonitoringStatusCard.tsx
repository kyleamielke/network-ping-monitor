import React from 'react';
import { Card, CardContent, Typography, Grid, Divider, Chip } from '@mui/material';
import { formatDistanceToNow } from 'date-fns';
import { DeviceStatus } from '@/shared/types/monitoring.types';

interface MonitoringStatusCardProps {
  status: DeviceStatus;
}

export const MonitoringStatusCard: React.FC<MonitoringStatusCardProps> = ({ status }) => {
  return (
    <Card>
      <CardContent>
        <Typography variant="h6" gutterBottom>
          Monitoring Status
        </Typography>
        <Divider sx={{ mb: 2 }} />
        <Grid container spacing={2}>
          <Grid item xs={6}>
            <Typography variant="body2" color="textSecondary">Status</Typography>
            <Chip 
              label={status.status} 
              size="small"
              color={status.online ? 'success' : 'error'}
            />
          </Grid>
          <Grid item xs={6}>
            <Typography variant="body2" color="textSecondary">Response Time</Typography>
            <Typography variant="body1">
              {status.responseTime ? `${status.responseTime}ms` : 'N/A'}
            </Typography>
          </Grid>
          <Grid item xs={6}>
            <Typography variant="body2" color="textSecondary">Last Status Change</Typography>
            <Typography variant="body1">
              {status.lastStatusChange 
                ? formatDistanceToNow(new Date(status.lastStatusChange), { addSuffix: true })
                : 'N/A'}
            </Typography>
          </Grid>
          <Grid item xs={6}>
            <Typography variant="body2" color="textSecondary">Consecutive Failures</Typography>
            <Typography variant="body1">{status.consecutiveFailures || 0}</Typography>
          </Grid>
        </Grid>
      </CardContent>
    </Card>
  );
};