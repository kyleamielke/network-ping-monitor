import { gql } from '@apollo/client';

export const ALERT_MUTATIONS = {
  ACKNOWLEDGE: gql`
    mutation AcknowledgeAlert($alertId: ID!) {
      acknowledgeAlert(alertId: $alertId) {
        id
        acknowledged
        acknowledgedAt
        acknowledgedBy
      }
    }
  `,

  RESOLVE: gql`
    mutation ResolveAlert($alertId: ID!) {
      resolveAlert(alertId: $alertId) {
        id
        resolved
        resolvedAt
      }
    }
  `,

  DELETE: gql`
    mutation DeleteAlert($alertId: ID!) {
      deleteAlert(alertId: $alertId)
    }
  `,
};