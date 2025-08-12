import { useQuery, gql } from '@apollo/client';
import { Alert } from '@/shared/types/alert.types';

const ALERT_DETAIL_QUERY = gql`
  query AlertDetail($id: ID!) {
    alert(id: $id) {
      id
      deviceId
      deviceName
      alertType
      message
      timestamp
      resolved
      resolvedAt
      acknowledged
      acknowledgedAt
      acknowledgedBy
      createdAt
      updatedAt
    }
  }
`;

interface AlertDetailQueryResponse {
  alert: Alert | null;
}

interface AlertDetailQueryVariables {
  id: string;
}

export const useAlertDetail = (alertId: string) => {
  const { data, loading, error, refetch } = useQuery<
    AlertDetailQueryResponse,
    AlertDetailQueryVariables
  >(ALERT_DETAIL_QUERY, {
    variables: { id: alertId },
    fetchPolicy: 'cache-and-network',
  });

  return {
    alert: data?.alert || null,
    loading,
    error,
    refetch,
  };
};