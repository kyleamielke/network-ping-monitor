import React, { useState, useCallback } from 'react';
import { useSubscription } from '@apollo/client';
import { Fade } from '@mui/material';
import { PageLayout } from '@/shared/components/PageLayout';
import { ConfirmDialog } from '@/shared/components/ConfirmDialog';
import { AlertList } from '@/features/alerts/components/AlertList';
import { AlertTabs } from '@/features/alerts/components/AlertTabs';
import { useAlerts } from '@/features/alerts/hooks/useAlerts';
import { useAlertOperations } from '@/features/alerts/hooks/useAlertOperations';
import { useDialog } from '@/shared/hooks/useDialog';
import { ALERT_STREAM_SUBSCRIPTION } from '@/shared/api/subscriptions';
import { Alert } from '@/shared/types/alert.types';
import { useLocale } from '@/shared/contexts/LocaleContext';

export const AlertsPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState(0);
  const confirmDialog = useDialog<{ action: string; alert: Alert | null }>();
  const { t } = useLocale();

  // Get alerts based on active tab
  
  const allAlerts = useAlerts('all');
  const unresolvedAlerts = useAlerts('unresolved');
  const unacknowledgedAlerts = useAlerts('unacknowledged');
  
  const currentAlerts = [allAlerts, unresolvedAlerts, unacknowledgedAlerts][activeTab];
  const { acknowledge, resolve, remove } = useAlertOperations();

  // Subscribe to alert updates
  useSubscription(ALERT_STREAM_SUBSCRIPTION, {
    onData: () => {
      // Refetch all queries when new alerts arrive
      allAlerts.refetch();
      unresolvedAlerts.refetch();
      unacknowledgedAlerts.refetch();
    },
  });

  // Memoize event handlers to prevent unnecessary re-renders
  const handleAlertAction = useCallback((action: string, alert: Alert) => {
    confirmDialog.openDialog({ action, alert });
  }, [confirmDialog]);

  const handleConfirmAction = useCallback(async () => {
    if (!confirmDialog.data?.alert) return;

    const { action, alert } = confirmDialog.data;
    
    try {
      switch (action) {
        case 'acknowledge':
          await acknowledge(alert.id);
          break;
        case 'resolve':
          await resolve(alert.id);
          break;
        case 'delete':
          await remove(alert.id);
          break;
      }
      confirmDialog.closeDialog();
    } catch (error) {
      console.error(`Failed to ${action} alert:`, error);
    }
  }, [confirmDialog, acknowledge, resolve, remove]);

  const getConfirmMessage = useCallback(() => {
    if (!confirmDialog.data) return '';
    const { action, alert } = confirmDialog.data;
    return `Are you sure you want to ${action} this alert for ${alert?.deviceName}?`;
  }, [confirmDialog.data]);

  const counts = {
    all: allAlerts.alerts.length,
    unresolved: unresolvedAlerts.alerts.length,
    unacknowledged: unacknowledgedAlerts.alerts.length,
  };

  const refetchAll = useCallback(() => {
    allAlerts.refetch();
    unresolvedAlerts.refetch();
    unacknowledgedAlerts.refetch();
  }, [allAlerts, unresolvedAlerts, unacknowledgedAlerts]);

  return (
    <PageLayout
      title={t('alerts.title')}
      subtitle={t('alerts.subtitle', { count: counts.unresolved })}
      onRefresh={refetchAll}
    >
      <AlertTabs
        activeTab={activeTab}
        onTabChange={setActiveTab}
        counts={counts}
      />

      <Fade in={true} timeout={800}>
        <div>
          <AlertList
            alerts={currentAlerts.alerts}
            loading={currentAlerts.loading}
            error={currentAlerts.error || null}
            onAcknowledge={(alert) => handleAlertAction('acknowledge', alert)}
            onResolve={(alert) => handleAlertAction('resolve', alert)}
            onDelete={(alert) => handleAlertAction('delete', alert)}
          />
        </div>
      </Fade>

      <ConfirmDialog
        open={confirmDialog.open}
        title={`Confirm ${confirmDialog.data?.action}`}
        message={getConfirmMessage()}
        confirmText={confirmDialog.data?.action}
        confirmColor={confirmDialog.data?.action === 'delete' ? 'error' : 'primary'}
        onConfirm={handleConfirmAction}
        onCancel={confirmDialog.closeDialog}
      />
    </PageLayout>
  );
};