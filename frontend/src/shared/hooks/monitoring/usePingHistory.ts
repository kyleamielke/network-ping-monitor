import { useQuery } from '@apollo/client';
import { PING_HISTORY_QUERY, PING_HISTORY_BY_TIME_QUERY } from '@/shared/api/monitoring/pingHistoryQueries';

export interface PingResult {
  timestamp: string;
  success: boolean;
  responseTimeMs?: number;
  errorMessage?: string;
}

export interface PingStatistics {
  totalPings: number;
  successfulPings: number;
  failedPings: number;
  successRate: number;
  averageResponseTime?: number;
  minResponseTime?: number;
  maxResponseTime?: number;
  uptime: number;
  packetLoss: number;
}

export interface PingHistory {
  deviceId: string;
  results: PingResult[];
  statistics: PingStatistics;
}

export type TimeRange = 'LAST_HOUR' | 'LAST_24_HOURS' | 'LAST_7_DAYS' | 'LAST_30_DAYS';

export interface TimeRangeInput {
  minutes?: number;
  hours?: number;
  days?: number;
}

interface UsePingHistoryResult {
  pingHistory: PingHistory | null;
  loading: boolean;
  error: any;
  refetch: () => void;
}

// Backward-compatible hook using enum
export const usePingHistory = (
  deviceId: string | null, 
  timeRange: TimeRange = 'LAST_HOUR'
): UsePingHistoryResult => {
  const { data, loading, error, refetch } = useQuery(PING_HISTORY_QUERY, {
    variables: { deviceId, timeRange },
    skip: !deviceId,
    fetchPolicy: 'cache-and-network',
    errorPolicy: 'all',
    pollInterval: 30000, // Poll every 30 seconds for semi-real-time updates
  });

  return {
    pingHistory: data?.pingHistory || null,
    loading,
    error,
    refetch,
  };
};

// New flexible hook using custom time ranges
export const usePingHistoryByTime = (
  deviceId: string | null, 
  timeRange: TimeRangeInput
): UsePingHistoryResult => {
  const { data, loading, error, refetch } = useQuery(PING_HISTORY_BY_TIME_QUERY, {
    variables: { deviceId, timeRange },
    skip: !deviceId,
    fetchPolicy: 'cache-and-network',
    errorPolicy: 'all',
    pollInterval: 30000, // Poll every 30 seconds for semi-real-time updates
  });

  return {
    pingHistory: data?.pingHistoryByTime || null,
    loading,
    error,
    refetch,
  };
};