import { useState, useEffect } from 'react';
import { useQuery } from '@apollo/client';
import { DASHBOARD_QUERIES } from '@/features/dashboard/api/dashboardQueries';
import { ALERT_QUERIES } from '@/features/alerts/api/alertQueries';
import { DeviceMonitoring, DeviceStatusEnum } from '@/shared/types/monitoring.types';
import { DeviceWithMonitoring } from '@/shared/types/device.types';
import { Alert } from '@/shared/types/alert.types';

interface DashboardStats {
  totalDevices: number;
  onlineDevices: number;
  offlineDevices: number;
  alertingDevices: number;
  avgResponseTime: number;
}

interface DashboardData {
  stats: DashboardStats;
  monitoredDevices: DeviceMonitoring[];
  monitoredDeviceIds: string[];
  loading: boolean;
  error: string | null;
  refetch: () => void;
}

export const useDashboardData = (): DashboardData => {
  const [stats, setStats] = useState<DashboardStats>({
    totalDevices: 0,
    onlineDevices: 0,
    offlineDevices: 0,
    alertingDevices: 0,
    avgResponseTime: 0,
  });

  const { 
    data: monitoringData, 
    loading: monitoringLoading, 
    error: monitoringError,
    refetch: refetchMonitoring
  } = useQuery(DASHBOARD_QUERIES.MONITORING_DASHBOARD);

  const {
    data: devicesData,
    loading: devicesLoading,
    error: devicesError,
    refetch: refetchDevices
  } = useQuery(DASHBOARD_QUERIES.DEVICES_WITH_MONITORING);

  const {
    data: alertsData,
    loading: alertsLoading,
    error: alertsError,
    refetch: refetchAlerts
  } = useQuery(ALERT_QUERIES.UNRESOLVED_ALERTS);

  useEffect(() => {
    if (monitoringData?.monitoringDashboard) {
      const dashboardData = monitoringData.monitoringDashboard;
      
      // Calculate stats from actual device data
      let avgResponseTime = 0;
      let onlineCount = 0;
      let offlineCount = 0;
      
      if (devicesData?.devicesWithMonitoring) {
        const monitoredDevices = (devicesData.devicesWithMonitoring as DeviceWithMonitoring[])
          .filter((d) => d.pingTarget?.monitored);
        
        // Calculate online/offline from actual device statuses
        monitoredDevices.forEach((device) => {
          if (device.currentStatus?.online) {
            onlineCount++;
          } else {
            offlineCount++;
          }
        });
        
        // Calculate average response time
        const responseTimes = monitoredDevices
          .filter((d) => d.currentStatus?.responseTime)
          .map((d) => d.currentStatus!.responseTime!);
        
        if (responseTimes.length > 0) {
          avgResponseTime = Math.round(
            responseTimes.reduce((a: number, b: number) => a + b, 0) / responseTimes.length
          );
        }
      }
      
      // Count unique devices with unresolved alerts
      const alertingDevices = new Set(
        (alertsData?.unresolvedAlerts as Alert[] || []).map(alert => alert.deviceId)
      ).size;
      
      // Use calculated values instead of server values for real-time updates
      setStats({
        totalDevices: dashboardData.totalDevices || 0,
        onlineDevices: onlineCount,
        offlineDevices: offlineCount,
        alertingDevices: alertingDevices,
        avgResponseTime: avgResponseTime,
      });
    }
  }, [monitoringData, devicesData, alertsData]);

  const refetch = () => {
    refetchMonitoring();
    refetchDevices();
    refetchAlerts();
  };

  // Get monitored devices from the devicesWithMonitoring query
  const monitoredDevices: DeviceMonitoring[] = devicesData?.devicesWithMonitoring
    ? (devicesData.devicesWithMonitoring as DeviceWithMonitoring[])
        .filter((device) => device.pingTarget?.monitored)
        .map((device) => {
          // Get alerts for this device from unresolved alerts
          const deviceAlerts = (alertsData?.unresolvedAlerts as Alert[] || [])
            .filter(alert => alert.deviceId === device.id);
            
          return {
            device: device,
            pingTarget: device.pingTarget!,
            currentStatus: device.currentStatus ? {
              deviceId: device.id,
              deviceName: device.name,
              ipAddress: device.ipAddress,
              online: device.currentStatus.online,
              lastStatusChange: device.currentStatus.lastStatusChange,
              responseTime: device.currentStatus.responseTime,
              status: device.currentStatus.online ? DeviceStatusEnum.ONLINE : DeviceStatusEnum.OFFLINE,
            } : undefined,
            recentAlerts: deviceAlerts,
            pingStatistics: undefined,
          };
        })
    : [];
  
  const monitoredDeviceIds = monitoredDevices.map(m => m.device.id);

  return {
    stats,
    monitoredDevices,
    monitoredDeviceIds,
    loading: monitoringLoading || devicesLoading || alertsLoading,
    error: monitoringError?.message || devicesError?.message || alertsError?.message || null,
    refetch,
  };
};