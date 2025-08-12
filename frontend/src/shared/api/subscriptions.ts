import { gql } from '@apollo/client';

// Alert subscriptions
export const ALERT_STREAM_SUBSCRIPTION = gql`
  subscription AlertStream {
    alertStream {
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
`;

// Monitoring subscriptions
export const PING_UPDATES_SUBSCRIPTION = gql`
  subscription PingUpdates($deviceId: ID) {
    pingUpdates(deviceId: $deviceId) {
      deviceId
      timestamp
      success
      responseTimeMs
      previousStatus
      currentStatus
    }
  }
`;

export const DEVICE_STATUS_UPDATES_SUBSCRIPTION = gql`
  subscription DeviceStatusUpdates($deviceId: ID) {
    deviceStatusUpdates(deviceId: $deviceId) {
      deviceId
      previousStatus
      currentStatus
      timestamp
    }
  }
`;