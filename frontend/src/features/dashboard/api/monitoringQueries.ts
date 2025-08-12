import { gql } from '@apollo/client';

export const GET_MONITORING_DASHBOARD = gql`
  query GetMonitoringDashboard {
    monitoringDashboard {
      totalDevices
      onlineDevices
      offlineDevices
      monitoredDevices
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
`;

export const GET_DEVICES_WITH_MONITORING = gql`
  query GetDevicesWithMonitoring {
    devicesWithMonitoring {
      device {
        id
        name
        ipAddress
        type
        os
        location
        site
      }
      pingTarget {
        deviceId
        ipAddress
        monitored
        pingIntervalSeconds
      }
      currentStatus {
        deviceId
        deviceName
        ipAddress
        online
        lastStatusChange
        responseTime
        status
        consecutiveSuccesses
        consecutiveFailures
      }
      pingStatistics {
        totalPings
        successfulPings
        failedPings
        averageResponseTime
        minResponseTime
        maxResponseTime
        uptime
        packetLoss
      }
    }
  }
`;