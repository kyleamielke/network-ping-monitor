import { gql } from '@apollo/client';

// Device fragment for consistent field selection
export const DEVICE_FIELDS = gql`
  fragment DeviceFields on Device {
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
    createdBy
    lastModifiedBy
  }
`;

// Device with monitoring fields
export const DEVICE_WITH_MONITORING_FIELDS = gql`
  ${DEVICE_FIELDS}
  fragment DeviceWithMonitoringFields on Device {
    ...DeviceFields
    currentStatus {
      online
      responseTime
      lastStatusChange
    }
    pingTarget {
      deviceId
      ipAddress
      hostname
      monitored
      pingIntervalSeconds
    }
  }
`;

// Queries
export const DEVICE_QUERIES = {
  LIST: gql`
    ${DEVICE_FIELDS}
    query ListDevices {
      devices {
        ...DeviceFields
      }
    }
  `,

  LIST_WITH_MONITORING: gql`
    ${DEVICE_WITH_MONITORING_FIELDS}
    query ListDevicesWithMonitoring {
      devicesWithMonitoring {
        ...DeviceWithMonitoringFields
      }
    }
  `,

  GET_BY_ID: gql`
    ${DEVICE_FIELDS}
    query GetDevice($id: ID!) {
      device(id: $id) {
        ...DeviceFields
      }
    }
  `,

  GET_WITH_MONITORING: gql`
    ${DEVICE_WITH_MONITORING_FIELDS}
    query GetDeviceWithMonitoring($deviceId: ID!) {
      deviceMonitoring(deviceId: $deviceId) {
        device {
          ...DeviceWithMonitoringFields
        }
        pingTarget {
          deviceId
          monitored
        }
        currentStatus {
          online
          responseTime
          lastStatusChange
        }
        recentAlerts {
          id
          type
          severity
          message
          createdAt
          resolved
        }
        pingStatistics {
          totalPings
          successfulPings
          failedPings
          averageResponseTime
          uptime
        }
      }
    }
  `,

  SEARCH: gql`
    ${DEVICE_FIELDS}
    query SearchDevices($criteria: DeviceSearchInput!) {
      searchDevices(criteria: $criteria) {
        devices {
          ...DeviceFields
        }
        totalElements
        totalPages
        currentPage
        pageSize
        hasNext
        hasPrevious
      }
    }
  `,
};