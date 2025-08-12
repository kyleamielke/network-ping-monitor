import React from 'react';
import { Card, CardContent, Typography, Grid, Divider, LinearProgress, Box } from '@mui/material';
import { PingStatistics } from '@/shared/types/monitoring.types';

interface StatisticsCardProps {
  statistics: PingStatistics;
}

export const StatisticsCard: React.FC<StatisticsCardProps> = ({ statistics }) => {
  // The backend already returns uptime as a percentage (0-100+)
  const uptimePercentage = Math.min(statistics.uptime, 100); // Cap at 100% for display
  const successRate = statistics.totalPings > 0 
    ? (statistics.successfulPings / statistics.totalPings) * 100 
    : 0;

  return (
    <Card>
      <CardContent>
        <Typography variant="h6" gutterBottom>
          24-Hour Statistics
        </Typography>
        <Divider sx={{ mb: 2 }} />
        <Grid container spacing={3}>
          <Grid item xs={6} sm={3}>
            <Typography variant="body2" color="textSecondary">Total Pings</Typography>
            <Typography variant="h6">{statistics.totalPings}</Typography>
          </Grid>
          <Grid item xs={6} sm={3}>
            <Typography variant="body2" color="textSecondary">Successful</Typography>
            <Typography variant="h6" color="success.main">{statistics.successfulPings}</Typography>
          </Grid>
          <Grid item xs={6} sm={3}>
            <Typography variant="body2" color="textSecondary">Failed</Typography>
            <Typography variant="h6" color="error.main">{statistics.failedPings}</Typography>
          </Grid>
          <Grid item xs={6} sm={3}>
            <Typography variant="body2" color="textSecondary">Avg Response</Typography>
            <Typography variant="h6">
              {statistics.averageResponseTime ? `${Math.round(statistics.averageResponseTime)}ms` : 'N/A'}
            </Typography>
          </Grid>

          <Grid item xs={12}>
            <Box sx={{ mt: 2 }}>
              <Box display="flex" justifyContent="space-between" mb={1}>
                <Typography variant="body2" color="textSecondary">Uptime</Typography>
                <Typography variant="body2">{statistics.uptime.toFixed(2)}%</Typography>
              </Box>
              <LinearProgress 
                variant="determinate" 
                value={uptimePercentage} 
                color={uptimePercentage >= 99 ? 'success' : uptimePercentage >= 95 ? 'warning' : 'error'}
                sx={{ height: 8, borderRadius: 1 }}
              />
            </Box>
          </Grid>

          <Grid item xs={12}>
            <Box>
              <Box display="flex" justifyContent="space-between" mb={1}>
                <Typography variant="body2" color="textSecondary">Success Rate</Typography>
                <Typography variant="body2">{successRate.toFixed(2)}%</Typography>
              </Box>
              <LinearProgress 
                variant="determinate" 
                value={successRate} 
                color={successRate >= 99 ? 'success' : successRate >= 95 ? 'warning' : 'error'}
                sx={{ height: 8, borderRadius: 1 }}
              />
            </Box>
          </Grid>
        </Grid>
      </CardContent>
    </Card>
  );
};