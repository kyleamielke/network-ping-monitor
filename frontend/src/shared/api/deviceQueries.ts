import { gql } from '@apollo/client';

// Device fragment for consistent field selection
export const DEVICE_FRAGMENT = gql`
  fragment DeviceFragment on Device {
    id
    name
    ipAddress
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
`;

// Device Status fragment
export const DEVICE_STATUS_FRAGMENT = gql`
  fragment DeviceStatusFragment on DeviceStatus {
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
`;

export const GET_ALL_DEVICES = gql`
  ${DEVICE_FRAGMENT}
  query GetAllDevices {
    devices {
      ...DeviceFragment
    }
  }
`;

export const GET_ALL_DEVICES_WITH_STATUS = gql`
  ${DEVICE_FRAGMENT}
  ${DEVICE_STATUS_FRAGMENT}
  query GetAllDevicesWithStatus {
    devicesWithMonitoring {
      ...DeviceFragment
      pingTarget {
        deviceId
        ipAddress
        monitored
        pingIntervalSeconds
      }
      currentStatus {
        ...DeviceStatusFragment
      }
    }
  }
`;

export const SEARCH_DEVICES = gql`
  query SearchDevices($criteria: DeviceSearchCriteria) {
    searchDevices(criteria: $criteria) {
      devices {
        id
        name
        ipAddress
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
    }
  }
`;

export interface DeviceSearchCriteria {
  name?: string;
  ipAddress?: string;
  macAddress?: string;
  type?: string;
  location?: string;
  page?: number;
  size?: number;
}