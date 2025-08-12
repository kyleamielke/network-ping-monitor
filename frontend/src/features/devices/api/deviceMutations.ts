import { gql } from '@apollo/client';
import { DEVICE_FIELDS } from '@/features/devices/api/deviceQueries';

// Input types
export interface CreateDeviceInput {
  name: string;
  ipAddress?: string;
  hostname?: string;
  macAddress?: string;
  type?: string;
  os?: string;
  // osType?: string;
  make?: string;
  model?: string;
  endpointId?: string;
  assetTag?: string;
  description?: string;
  location?: string;
  // site?: string;
}

export interface UpdateDeviceInput {
  expectedVersion: number;
  name?: string;
  ipAddress?: string;
  hostname?: string;
  macAddress?: string;
  type?: string;
  os?: string;
  // osType?: string;
  make?: string;
  model?: string;
  endpointId?: string;
  assetTag?: string;
  description?: string;
  location?: string;
  // site?: string;
}

export interface DeviceFilterInput {
  type?: string;
  // site?: string;
  make?: string;
  os?: string;
  // osType?: string;
  status?: string;
}

// Basic CRUD mutations (for backward compatibility)
export const CREATE_DEVICE = gql`
  mutation CreateDevice($input: CreateDeviceInput!) {
    createDevice(input: $input) {
      ...DeviceFields
    }
  }
  ${DEVICE_FIELDS}
`;

export const UPDATE_DEVICE = gql`
  mutation UpdateDevice($id: ID!, $input: UpdateDeviceInput!) {
    updateDevice(id: $id, input: $input) {
      ...DeviceFields
    }
  }
  ${DEVICE_FIELDS}
`;

export const DELETE_DEVICE = gql`
  mutation DeleteDevice($deviceId: ID!) {
    deleteDevice(id: $deviceId)
  }
`;

// Bulk operations
export const BULK_DELETE_DEVICES = gql`
  mutation BulkDeleteDevices($deviceIds: [ID!]!) {
    bulkDeleteDevices(deviceIds: $deviceIds) {
      totalRequested
      successful
      failed
      errors
    }
  }
`;

export const BULK_UPDATE_DEVICES = gql`
  mutation BulkUpdateDevices($input: BulkUpdateDevicesInput!) {
    bulkUpdateDevices(input: $input) {
      totalRequested
      successful
      failed
      errors
    }
  }
`;

// Monitoring mutations
export const START_PING_MONITORING = gql`
  mutation StartPingMonitoring($deviceId: ID!) {
    startPingMonitoring(deviceId: $deviceId) {
      deviceId
      monitored
      ipAddress
      hostname
      pingIntervalSeconds
      createdAt
      updatedAt
    }
  }
`;

export const STOP_PING_MONITORING = gql`
  mutation StopPingMonitoring($deviceId: ID!) {
    stopPingMonitoring(deviceId: $deviceId)
  }
`;

// Bulk monitoring mutations
export const START_MONITORING_ALL = gql`
  mutation StartMonitoringAll {
    startMonitoringAll {
      successful
      failed
      totalDevices
    }
  }
`;

export const STOP_MONITORING_ALL = gql`
  mutation StopMonitoringAll {
    stopMonitoringAll {
      successful
      failed
      totalDevices
    }
  }
`;

// Collected mutations object
export const DEVICE_MUTATIONS = {
  CREATE: CREATE_DEVICE,
  UPDATE: UPDATE_DEVICE,
  DELETE: DELETE_DEVICE,
  BULK_DELETE: BULK_DELETE_DEVICES,
  BULK_UPDATE: BULK_UPDATE_DEVICES,
  START_MONITORING: START_PING_MONITORING,
  STOP_MONITORING: STOP_PING_MONITORING,
  START_MONITORING_ALL,
  STOP_MONITORING_ALL,
};