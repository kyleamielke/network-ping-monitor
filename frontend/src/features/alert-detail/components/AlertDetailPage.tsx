import React from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { 
  Box, 
  Grid, 
  Card, 
  CardContent, 
  Typography, 
  Chip, 
  Button,
  Divider,
  CircularProgress,
  Alert as MuiAlert,
  IconButton,
  // Tooltip,
} from '@mui/material';
import { 
  ArrowBack as ArrowBackIcon,
  CheckCircle as ResolvedIcon,
  Warning as UnresolvedIcon,
  Person as PersonIcon,
  DeviceHub as DeviceIcon,
} from '@mui/icons-material';
import { formatDistanceToNow, format } from 'date-fns';
import { PageLayout } from '@/shared/components/PageLayout';
import { Breadcrumbs } from '@/shared/components/Breadcrumbs';
// import { NavigationState } from '@/shared/types/navigation.types';
import { useBreadcrumbNavigation } from '@/shared/hooks/useBreadcrumbNavigation';
import { useAlertDetail } from '@/features/alert-detail/hooks/useAlertDetail';
import { useAlertOperations } from '@/features/alerts/hooks/useAlertOperations';
import { AlertType } from '@/shared/types/alert.types';

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

export const AlertDetailPage: React.FC = () => {
  const { alertId } = useParams<{ alertId: string }>();
  const navigate = useNavigate();
  const location = useLocation();
  const { getBreadcrumbs } = useBreadcrumbNavigation();
  const { alert, loading, error, refetch } = useAlertDetail(alertId!);
  const { acknowledge, resolve } = useAlertOperations();

  const handleAcknowledge = async () => {
    if (!alert) return;
    try {
      await acknowledge(alert.id);
      refetch();
    } catch (err) {
      console.error('Failed to acknowledge alert:', err);
    }
  };

  const handleResolve = async () => {
    if (!alert) return;
    try {
      await resolve(alert.id);
      refetch();
    } catch (err) {
      console.error('Failed to resolve alert:', err);
    }
  };

  const handleNavigateToDevice = () => {
    if (alert?.deviceId) {
      // Build breadcrumbs based on where we came from
      const breadcrumbs = location.state?.from === 'device' 
        ? location.state.breadcrumbs?.slice(0, -1) // Remove "Alert Details" from end
        : [
            { label: 'Dashboard', path: '/dashboard' },
            { label: 'Alerts', path: '/alerts' },
            { label: 'Alert Details', path: `/alerts/${alert.id}` },
            { label: alert.deviceName }
          ];
      
      navigate(`/devices/${alert.deviceId}`, {
        state: {
          from: 'alert',
          breadcrumbs
        }
      });
    }
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress />
      </Box>
    );
  }

  if (error || !alert) {
    return (
      <Box p={2}>
        <MuiAlert severity="error">
          {error ? `Error loading alert: ${error.message}` : 'Alert not found'}
        </MuiAlert>
        <Button 
          startIcon={<ArrowBackIcon />} 
          onClick={() => navigate(-1)}
          sx={{ mt: 2 }}
        >
          Go Back
        </Button>
      </Box>
    );
  }

  return (
    <>
      <Breadcrumbs 
        items={getBreadcrumbs([
          { label: 'Dashboard', path: '/dashboard' },
          { label: 'Alerts', path: '/alerts' },
          { label: alert.deviceName || 'Alert Details' }
        ])}
      />
      <PageLayout
        title={
          <Box display="flex" alignItems="center" gap={1}>
            <IconButton onClick={() => navigate(-1)} size="small">
              <ArrowBackIcon />
            </IconButton>
            Alert Details
          </Box>
        }
        subtitle={`Alert ID: ${alert.id}`}
        onRefresh={refetch}
        breadcrumbs={false}
      >
      <Grid container spacing={3}>
        {/* Alert Overview Card */}
        <Grid item xs={12}>
          <Card>
            <CardContent>
              <Box display="flex" justifyContent="space-between" alignItems="flex-start" mb={3}>
                <Box>
                  <Box display="flex" alignItems="center" gap={2} mb={1}>
                    <Chip 
                      label={alert.alertType} 
                      color={getAlertColor(alert.alertType)}
                      size="medium"
                    />
                    {alert.resolved ? (
                      <Chip 
                        icon={<ResolvedIcon />} 
                        label="Resolved" 
                        color="success" 
                        variant="outlined" 
                      />
                    ) : (
                      <Chip 
                        icon={<UnresolvedIcon />} 
                        label="Unresolved" 
                        color="error" 
                        variant="outlined" 
                      />
                    )}
                    {alert.acknowledged && (
                      <Chip 
                        icon={<PersonIcon />} 
                        label="Acknowledged" 
                        color="info" 
                        variant="outlined" 
                      />
                    )}
                  </Box>
                  <Typography variant="h6" gutterBottom>
                    {alert.message}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Created {formatDistanceToNow(new Date(alert.timestamp), { addSuffix: true })}
                  </Typography>
                </Box>
                <Box display="flex" gap={1}>
                  {!alert.acknowledged && (
                    <Button
                      variant="outlined"
                      color="primary"
                      onClick={handleAcknowledge}
                    >
                      Acknowledge
                    </Button>
                  )}
                  {!alert.resolved && (
                    <Button
                      variant="contained"
                      color="success"
                      onClick={handleResolve}
                    >
                      Resolve
                    </Button>
                  )}
                </Box>
              </Box>

              <Divider sx={{ my: 2 }} />

              {/* Device Information */}
              <Box mb={3}>
                <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                  Device Information
                </Typography>
                <Box display="flex" alignItems="center" gap={1}>
                  <DeviceIcon color="action" />
                  <Typography variant="body1">
                    {alert.deviceName}
                  </Typography>
                  <Button
                    size="small"
                    variant="text"
                    onClick={handleNavigateToDevice}
                  >
                    View Device
                  </Button>
                </Box>
              </Box>

              {/* Timeline */}
              <Box>
                <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                  Timeline
                </Typography>
                <Box sx={{ pl: 2 }}>
                  <Box mb={2}>
                    <Typography variant="body2" fontWeight="medium">
                      Alert Created
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      {format(new Date(alert.timestamp), 'PPpp')}
                    </Typography>
                  </Box>
                  
                  {alert.acknowledged && alert.acknowledgedAt && (
                    <Box mb={2}>
                      <Typography variant="body2" fontWeight="medium">
                        Acknowledged by {alert.acknowledgedBy || 'System'}
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        {format(new Date(alert.acknowledgedAt), 'PPpp')}
                      </Typography>
                    </Box>
                  )}
                  
                  {alert.resolved && alert.resolvedAt && (
                    <Box mb={2}>
                      <Typography variant="body2" fontWeight="medium">
                        Resolved
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        {format(new Date(alert.resolvedAt), 'PPpp')}
                      </Typography>
                    </Box>
                  )}
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        {/* Additional Details Card */}
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Alert Details
              </Typography>
              <Box sx={{ '& > div': { mb: 2 } }}>
                <Box>
                  <Typography variant="body2" color="text.secondary">
                    Alert ID
                  </Typography>
                  <Typography variant="body2" fontFamily="monospace">
                    {alert.id}
                  </Typography>
                </Box>
                <Box>
                  <Typography variant="body2" color="text.secondary">
                    Device ID
                  </Typography>
                  <Typography variant="body2" fontFamily="monospace">
                    {alert.deviceId}
                  </Typography>
                </Box>
                <Box>
                  <Typography variant="body2" color="text.secondary">
                    Created At
                  </Typography>
                  <Typography variant="body2">
                    {format(new Date(alert.createdAt), 'PPpp')}
                  </Typography>
                </Box>
                {alert.updatedAt && (
                  <Box>
                    <Typography variant="body2" color="text.secondary">
                      Last Updated
                    </Typography>
                    <Typography variant="body2">
                      {format(new Date(alert.updatedAt), 'PPpp')}
                    </Typography>
                  </Box>
                )}
              </Box>
            </CardContent>
          </Card>
        </Grid>

        {/* Related Information */}
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Related Information
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Additional context and related alerts will appear here
              </Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </PageLayout>
    </>
  );
};