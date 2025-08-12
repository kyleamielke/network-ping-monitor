import { gql } from '@apollo/client';

export const PING_HISTORY_QUERY = gql`
  query GetPingHistory($deviceId: ID!, $timeRange: TimeRange!) {
    pingHistory(deviceId: $deviceId, timeRange: $timeRange) {
      deviceId
      results {
        timestamp
        success
        responseTimeMs
        errorMessage
      }
      statistics {
        totalPings
        successfulPings
        failedPings
        successRate
        averageResponseTime
        minResponseTime
        maxResponseTime
        uptime
        packetLoss
      }
    }
  }
`;

export const PING_HISTORY_BY_TIME_QUERY = gql`
  query GetPingHistoryByTime($deviceId: ID!, $timeRange: TimeRangeInput!) {
    pingHistoryByTime(deviceId: $deviceId, timeRange: $timeRange) {
      deviceId
      results {
        timestamp
        success
        responseTimeMs
        errorMessage
      }
      statistics {
        totalPings
        successfulPings
        failedPings
        successRate
        averageResponseTime
        minResponseTime
        maxResponseTime
        uptime
        packetLoss
      }
    }
  }
`;