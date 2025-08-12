import { useMutation } from '@apollo/client';
import { ALERT_MUTATIONS } from '@/features/alerts/api/alertMutations';
import { ALERT_QUERIES } from '@/features/alerts/api/alertQueries';
import { useToast } from '@/shared/contexts/ToastContext';

export const useAlertOperations = () => {
  const { showToast } = useToast();

  const [acknowledgeAlert] = useMutation(ALERT_MUTATIONS.ACKNOWLEDGE, {
    onCompleted: () => {
      showToast('Alert acknowledged', 'success');
    },
    onError: (error) => {
      showToast(`Failed to acknowledge alert: ${error.message}`, 'error');
    },
    refetchQueries: [
      { query: ALERT_QUERIES.ALL_ALERTS },
      { query: ALERT_QUERIES.UNACKNOWLEDGED_ALERTS },
    ],
  });

  const [resolveAlert] = useMutation(ALERT_MUTATIONS.RESOLVE, {
    onCompleted: () => {
      showToast('Alert resolved', 'success');
    },
    onError: (error) => {
      showToast(`Failed to resolve alert: ${error.message}`, 'error');
    },
    refetchQueries: [
      { query: ALERT_QUERIES.ALL_ALERTS },
      { query: ALERT_QUERIES.UNRESOLVED_ALERTS },
      { query: ALERT_QUERIES.UNACKNOWLEDGED_ALERTS },
    ],
  });

  const [deleteAlert] = useMutation(ALERT_MUTATIONS.DELETE, {
    onCompleted: () => {
      showToast('Alert deleted', 'success');
    },
    onError: (error) => {
      showToast(`Failed to delete alert: ${error.message}`, 'error');
    },
    refetchQueries: [
      { query: ALERT_QUERIES.ALL_ALERTS },
      { query: ALERT_QUERIES.UNRESOLVED_ALERTS },
      { query: ALERT_QUERIES.UNACKNOWLEDGED_ALERTS },
    ],
  });

  const acknowledge = async (id: string) => {
    await acknowledgeAlert({ variables: { alertId: id } });
  };

  const resolve = async (id: string) => {
    await resolveAlert({ variables: { alertId: id } });
  };

  const remove = async (id: string) => {
    await deleteAlert({ variables: { alertId: id } });
  };

  return {
    acknowledge,
    resolve,
    remove,
  };
};