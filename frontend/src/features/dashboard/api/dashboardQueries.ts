import { gql } from '@apollo/client';

export const DASHBOARD_QUERIES = {
  MONITORING_DASHBOARD: gql`
    query GetMonitoringDashboard {
      monitoringDashboard {
        totalDevices
        monitoredDevices
        onlineDevices
        offlineDevices
        totalAlerts
        unresolvedAlerts
        systemHealth {
          apiGateway {
            name
            status
            message
          }
          deviceService {
            name
            status
            message
          }
          pingService {
            name
            status
            message
          }
          alertService {
            name
            status
            message
          }
        }
      }
    }
  `,

  DEVICES_WITH_MONITORING: gql`
    query GetDevicesWithMonitoring {
      devicesWithMonitoring {
        id
        name
        ipAddress
        hostname
        type
        pingTarget {
          deviceId
          ipAddress
          hostname
          monitored
        }
        currentStatus {
          online
          responseTime
          lastStatusChange
        }
        recentPings {
          timestamp
          success
          responseTimeMs
        }
      }
    }
  `,
};