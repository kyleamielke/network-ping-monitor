import { gql } from '@apollo/client';

export const DEVICE_DETAIL_QUERY = gql`
  query GetDeviceWithMonitoring($deviceId: ID!) {
    deviceMonitoring(deviceId: $deviceId) {
      device {
        id
        version
        name
        ipAddress
        hostname
        macAddress
        type
        os
        osType
        make
        model
        endpointId
        assetTag
        description
        location
        site
        createdAt
        updatedAt
        roles {
          id
          name
          description
        }
      }
      currentStatus {
        deviceId
        deviceName
        ipAddress
        hostname
        online
        lastStatusChange
        responseTime
        status
        consecutiveSuccesses
        consecutiveFailures
      }
      pingTarget {
        deviceId
        ipAddress
        hostname
        monitored
        pingIntervalSeconds
      }
      statistics(timeRange: LAST_24_HOURS) {
        totalPings
        successfulPings
        failedPings
        averageResponseTime
        minResponseTime
        maxResponseTime
        uptime
        packetLoss
      }
      recentAlerts {
        id
        deviceId
        deviceName
        alertType
        message
        timestamp
        resolved
        resolvedAt
        acknowledged
        acknowledgedBy
        acknowledgedAt
        createdAt
        updatedAt
      }
    }
  }
`;