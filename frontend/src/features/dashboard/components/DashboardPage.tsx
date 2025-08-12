import React, { useState, useMemo, useCallback } from 'react';
import { Grid, Grow } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import {
  Computer as ComputerIcon,
  CheckCircle as CheckCircleIcon,
  Error as ErrorIcon,
  Speed as SpeedIcon,
  Warning as AlertIcon,
} from '@mui/icons-material';
import { PageLayout } from '@/shared/components/PageLayout';
import { AsyncContent } from '@/shared/components/AsyncContent';
import { StatCard } from '@/features/dashboard/components/StatCard';
import { DeviceStatusGrid } from '@/features/dashboard/components/DeviceStatusGrid';
import { useDashboardData } from '@/features/dashboard/hooks/useDashboardData';
import { useDashboardSubscriptions } from '@/features/dashboard/hooks/useDashboardSubscriptions';
import { useLocale } from '@/shared/contexts/LocaleContext';

type FilterType = 'all' | 'online' | 'offline' | 'alerting' | 'high_latency';

export const DashboardPage: React.FC = () => {
  const navigate = useNavigate();
  const { stats, monitoredDevices, loading, error, refetch } = useDashboardData();
  const [activeFilter, setActiveFilter] = useState<FilterType>('all');
  const { t } = useLocale();
  
  // Subscribe to real-time updates
  useDashboardSubscriptions();

  // Filter handlers
  const handleFilterChange = useCallback((filter: FilterType) => {
    setActiveFilter(filter);
  }, []);

  // Device selection navigates to device details
  const handleDeviceSelect = useCallback((deviceId: string, deviceName?: string) => {
    navigate(`/devices/${deviceId}`, {
      state: {
        from: 'dashboard',
        breadcrumbs: [
          { label: 'Dashboard', path: '/dashboard' },
          { label: deviceName || 'Device' }
        ]
      }
    });
  }, [navigate]);

  // Filter devices based on active filter
  const filteredDevices = useMemo(() => {
    switch (activeFilter) {
      case 'online':
        return monitoredDevices.filter(d => d.currentStatus?.online);
      case 'offline':
        return monitoredDevices.filter(d => !d.currentStatus?.online);
      case 'alerting':
        return monitoredDevices.filter(d => d.recentAlerts && d.recentAlerts.length > 0);
      case 'high_latency':
        return monitoredDevices.filter(d => 
          d.currentStatus?.responseTime && d.currentStatus.responseTime > 120
        );
      case 'all':
      default:
        return monitoredDevices;
    }
  }, [monitoredDevices, activeFilter]);

  // Get devices to display (show all filtered devices)
  const devicesToDisplay = filteredDevices;

  return (
    <PageLayout
      title={t('dashboard.title')}
      subtitle={t('dashboard.subtitle', { count: stats.onlineDevices + stats.offlineDevices })}
      onRefresh={refetch}
    >
      <AsyncContent loading={loading} error={error}>
        {/* Statistics Cards */}
        <Grid container spacing={3} mb={5}>
          <Grid item xs={12} sm={6} lg={2.4}>
            <Grow in={true} timeout={600}>
              <div>
                <StatCard
                  title={t('dashboard.totalDevices')}
                  value={stats.totalDevices}
                  color="primary"
                  icon={<ComputerIcon />}
                  onClick={() => handleFilterChange('all')}
                  active={activeFilter === 'all'}
                />
              </div>
            </Grow>
          </Grid>
          <Grid item xs={12} sm={6} lg={2.4}>
            <Grow in={true} timeout={700}>
              <div>
                <StatCard
                  title={t('dashboard.onlineDevices')}
                  value={stats.onlineDevices}
                  color="success"
                  icon={<CheckCircleIcon />}
                  onClick={() => handleFilterChange('online')}
                  active={activeFilter === 'online'}
                />
              </div>
            </Grow>
          </Grid>
          <Grid item xs={12} sm={6} lg={2.4}>
            <Grow in={true} timeout={800}>
              <div>
                <StatCard
                  title={t('dashboard.offlineDevices')}
                  value={stats.offlineDevices}
                  color="error"
                  icon={<ErrorIcon />}
                  onClick={() => handleFilterChange('offline')}
                  active={activeFilter === 'offline'}
                />
              </div>
            </Grow>
          </Grid>
          <Grid item xs={12} sm={6} lg={2.4}>
            <Grow in={true} timeout={900}>
              <div>
                <StatCard
                  title={t('dashboard.alertingDevices')}
                  value={stats.alertingDevices}
                  color="warning"
                  icon={<AlertIcon />}
                  onClick={() => handleFilterChange('alerting')}
                  active={activeFilter === 'alerting'}
                />
              </div>
            </Grow>
          </Grid>
          <Grid item xs={12} sm={6} lg={2.4}>
            <Grow in={true} timeout={1000}>
              <div>
                <StatCard
                  title={t('dashboard.avgResponseTime')}
                  value={`${stats.avgResponseTime}ms`}
                  color="secondary"
                  icon={<SpeedIcon />}
                  onClick={() => handleFilterChange('high_latency')}
                  active={activeFilter === 'high_latency'}
                  subtitle={activeFilter === 'high_latency' ? '>120ms' : undefined}
                />
              </div>
            </Grow>
          </Grid>
        </Grid>

        {/* Device Grid */}
        <Grid container spacing={3}>
          <Grid item xs={12}>
            <Grow in={true} timeout={1200}>
              <div>
                <DeviceStatusGrid 
                  devices={devicesToDisplay} 
                  filterType={activeFilter}
                  totalCount={filteredDevices.length}
                  onDeviceSelect={handleDeviceSelect}
                  columns={3}
                />
              </div>
            </Grow>
          </Grid>
        </Grid>
      </AsyncContent>
    </PageLayout>
  );
};