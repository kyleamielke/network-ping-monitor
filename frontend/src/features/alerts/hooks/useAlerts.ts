import { useQuery } from '@apollo/client';
import { ALERT_QUERIES } from '@/features/alerts/api/alertQueries';
import { Alert } from '@/shared/types/alert.types';

type AlertFilter = 'all' | 'unresolved' | 'unacknowledged';

export const useAlerts = (filter: AlertFilter = 'all') => {
  const queryMap = {
    all: ALERT_QUERIES.ALL_ALERTS,
    unresolved: ALERT_QUERIES.UNRESOLVED_ALERTS,
    unacknowledged: ALERT_QUERIES.UNACKNOWLEDGED_ALERTS,
  };

  const query = queryMap[filter];
  const { data, loading, error, refetch } = useQuery(query, {
    fetchPolicy: 'cache-and-network',
  });

  const fieldMap = {
    all: 'alerts',
    unresolved: 'unresolvedAlerts',
    unacknowledged: 'alerts', // Use alerts and filter on frontend
  };

  let alerts = data?.[fieldMap[filter]] || [];
  
  // Filter unacknowledged alerts on the frontend
  if (filter === 'unacknowledged' && alerts.length > 0) {
    alerts = alerts.filter((alert: Alert) => !alert.acknowledged && !alert.resolved);
  }

  return {
    alerts: alerts as Alert[],
    loading,
    error,
    refetch,
  };
};