import { gql } from '@apollo/client';

const ALERT_FIELDS = gql`
  fragment AlertFields on Alert {
    id
    deviceId
    deviceName
    alertType
    message
    timestamp
    createdAt
    updatedAt
    acknowledged
    acknowledgedAt
    acknowledgedBy
    resolved
    resolvedAt
  }
`;

export const ALERT_QUERIES = {
  ALL_ALERTS: gql`
    ${ALERT_FIELDS}
    query GetAllAlerts {
      alerts {
        ...AlertFields
      }
    }
  `,

  UNRESOLVED_ALERTS: gql`
    ${ALERT_FIELDS}
    query GetUnresolvedAlerts {
      unresolvedAlerts {
        ...AlertFields
      }
    }
  `,

  UNACKNOWLEDGED_ALERTS: gql`
    ${ALERT_FIELDS}
    query GetUnacknowledgedAlerts {
      alerts {
        ...AlertFields
      }
    }
  `,

  DEVICE_ALERTS: gql`
    ${ALERT_FIELDS}
    query GetDeviceAlerts($deviceId: ID!) {
      deviceAlerts(deviceId: $deviceId) {
        ...AlertFields
      }
    }
  `,
};