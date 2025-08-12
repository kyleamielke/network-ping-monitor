import React from 'react';
import { Card, CardContent, Typography, Grid, Divider } from '@mui/material';
import { Device } from '@/shared/types/device.types';
import { useLocale } from '@/shared/contexts/LocaleContext';

interface DeviceInfoCardProps {
  device: Device;
}

export const DeviceInfoCard: React.FC<DeviceInfoCardProps> = React.memo(({ device }) => {
  const { formatMacAddress } = useLocale();
  return (
    <Card>
      <CardContent>
        <Typography variant="h6" gutterBottom>
          Device Information
        </Typography>
        <Divider sx={{ mb: 2 }} />
        <Grid container spacing={2}>
          <Grid item xs={6}>
            <Typography variant="body2" color="textSecondary">IP Address</Typography>
            <Typography variant="body1">{device.ipAddress || 'N/A'}</Typography>
          </Grid>
          <Grid item xs={6}>
            <Typography variant="body2" color="textSecondary">Hostname</Typography>
            <Typography variant="body1">{device.hostname || 'N/A'}</Typography>
          </Grid>
          <Grid item xs={6}>
            <Typography variant="body2" color="textSecondary">MAC Address</Typography>
            <Typography variant="body1">{formatMacAddress(device.macAddress)}</Typography>
          </Grid>
          <Grid item xs={6}>
            <Typography variant="body2" color="textSecondary">Type</Typography>
            <Typography variant="body1">{device.type || 'N/A'}</Typography>
          </Grid>
          <Grid item xs={6}>
            <Typography variant="body2" color="textSecondary">Operating System</Typography>
            <Typography variant="body1">{device.os || 'N/A'}</Typography>
          </Grid>
          <Grid item xs={6}>
            <Typography variant="body2" color="textSecondary">Make</Typography>
            <Typography variant="body1">{device.make || 'N/A'}</Typography>
          </Grid>
          <Grid item xs={6}>
            <Typography variant="body2" color="textSecondary">Model</Typography>
            <Typography variant="body1">{device.model || 'N/A'}</Typography>
          </Grid>
          <Grid item xs={6}>
            <Typography variant="body2" color="textSecondary">Asset Tag</Typography>
            <Typography variant="body1">{device.assetTag || 'N/A'}</Typography>
          </Grid>
          <Grid item xs={6}>
            <Typography variant="body2" color="textSecondary">Endpoint ID</Typography>
            <Typography variant="body1">{device.endpointId || 'N/A'}</Typography>
          </Grid>
          {device.location && (
            <Grid item xs={6}>
              <Typography variant="body2" color="textSecondary">Location</Typography>
              <Typography variant="body1">{device.location}</Typography>
            </Grid>
          )}
          {device.site && (
            <Grid item xs={6}>
              <Typography variant="body2" color="textSecondary">Site</Typography>
              <Typography variant="body1">{device.site}</Typography>
            </Grid>
          )}
        </Grid>
      </CardContent>
    </Card>
  );
});