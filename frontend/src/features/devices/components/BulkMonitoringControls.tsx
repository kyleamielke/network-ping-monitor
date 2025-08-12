import React, { useState } from 'react';
import {
  Box,
  Button,
  ButtonGroup,
  Chip,
  Alert,
  Snackbar,
  CircularProgress,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions,
  Typography,
  Paper,
  Slide,
  Zoom,
} from '@mui/material';
import {
  PlayArrow as StartIcon,
  Stop as StopIcon,
  Delete as DeleteIcon,
  Edit as EditIcon,
} from '@mui/icons-material';
import { useMutation } from '@apollo/client';
import {
  DEVICE_MUTATIONS,
} from '@/features/devices/api/deviceMutations';
import { DeviceWithMonitoring } from '@/shared/types/device.types';

interface BulkMonitoringControlsProps {
  allDevices: DeviceWithMonitoring[];
  filteredDevices: DeviceWithMonitoring[];
  selectedDevices?: string[];
  onRefresh: () => void;
  onSelectionClear?: () => void;
  onBulkEdit?: () => void;
}

export const BulkMonitoringControls: React.FC<BulkMonitoringControlsProps> = ({
  allDevices,
  filteredDevices,
  selectedDevices = [],
  onRefresh,
  onSelectionClear,
  onBulkEdit,
}) => {
  const [confirmDialog, setConfirmDialog] = useState<{
    open: boolean;
    action: 'start' | 'stop' | 'delete';
    message: string;
  }>({ open: false, action: 'start', message: '' });
  const [snackbar, setSnackbar] = useState<{
    open: boolean;
    message: string;
    severity: 'success' | 'error' | 'warning';
  }>({ open: false, message: '', severity: 'success' });

  // Mutations - we only have these available
  const [startMonitoringAll, { loading: startingAll }] = useMutation(DEVICE_MUTATIONS.START_MONITORING_ALL);
  const [stopMonitoringAll, { loading: stoppingAll }] = useMutation(DEVICE_MUTATIONS.STOP_MONITORING_ALL);
  
  // Individual device mutations for filtered operations
  const [startPingMonitoring] = useMutation(DEVICE_MUTATIONS.START_MONITORING);
  const [stopPingMonitoring] = useMutation(DEVICE_MUTATIONS.STOP_MONITORING);
  
  // Bulk delete mutation
  const [bulkDeleteDevices, { loading: deletingDevices }] = useMutation(DEVICE_MUTATIONS.BULK_DELETE);
  
  const [operationInProgress, setOperationInProgress] = useState(false);
  const loading = startingAll || stoppingAll || deletingDevices || operationInProgress;

  const isFiltered = filteredDevices.length < allDevices.length;

  const handleStartMonitoring = async () => {
    try {
      if (!isFiltered) {
        // Use the start all mutation
        const result = await startMonitoringAll();
        const data = result.data.startMonitoringAll;
        
        setSnackbar({
          open: true,
          message: `Successfully started monitoring for ${data.successful} out of ${data.totalDevices} devices`,
          severity: 'success',
        });
      } else {
        // For filtered devices, we need to start monitoring individually
        setOperationInProgress(true);
        let successful = 0;
        let failed = 0;
        
        for (const device of filteredDevices) {
          try {
            await startPingMonitoring({ variables: { deviceId: device.id } });
            successful++;
          } catch {
            failed++;
          }
        }
        
        setOperationInProgress(false);
        setSnackbar({
          open: true,
          message: `Started monitoring for ${successful} devices${failed > 0 ? `, ${failed} failed` : ''}`,
          severity: failed > 0 ? 'warning' : 'success',
        });
      }
      
      onRefresh();
    } catch (error) {
      setOperationInProgress(false);
      setSnackbar({
        open: true,
        message: `Error starting monitoring: ${error instanceof Error ? error.message : 'Unknown error'}`,
        severity: 'error',
      });
    }
    setConfirmDialog({ open: false, action: 'start', message: '' });
  };

  const handleStopMonitoring = async () => {
    try {
      if (!isFiltered) {
        // Use stop all for all devices
        const result = await stopMonitoringAll();
        const data = result.data.stopMonitoringAll;
        
        setSnackbar({
          open: true,
          message: `Successfully stopped monitoring for ${data.successful} out of ${data.totalDevices} devices`,
          severity: 'success',
        });
      } else {
        // For filtered devices, we need to stop monitoring individually
        setOperationInProgress(true);
        let successful = 0;
        let failed = 0;
        
        // Only stop monitoring for devices that are currently being monitored
        const monitoredDevices = filteredDevices.filter(d => d.pingTarget?.monitored);
        
        for (const device of monitoredDevices) {
          try {
            await stopPingMonitoring({ variables: { deviceId: device.id } });
            successful++;
          } catch {
            failed++;
          }
        }
        
        setOperationInProgress(false);
        setSnackbar({
          open: true,
          message: `Stopped monitoring for ${successful} devices${failed > 0 ? `, ${failed} failed` : ''}`,
          severity: failed > 0 ? 'warning' : 'success',
        });
      }
      
      onRefresh();
    } catch (error) {
      setOperationInProgress(false);
      setSnackbar({
        open: true,
        message: `Error stopping monitoring: ${error instanceof Error ? error.message : 'Unknown error'}`,
        severity: 'error',
      });
    }
    setConfirmDialog({ open: false, action: 'stop', message: '' });
  };

  const handleBulkDelete = async () => {
    try {
      const result = await bulkDeleteDevices({
        variables: { deviceIds: selectedDevices }
      });
      
      const data = result.data.bulkDeleteDevices;
      
      setSnackbar({
        open: true,
        message: `Successfully deleted ${data.successful} devices${data.failed > 0 ? `, ${data.failed} failed` : ''}`,
        severity: data.failed > 0 ? 'warning' : 'success',
      });
      
      // Clear selection after successful deletion
      if (onSelectionClear) {
        onSelectionClear();
      }
      
      onRefresh();
    } catch (error) {
      setSnackbar({
        open: true,
        message: `Error deleting devices: ${error instanceof Error ? error.message : 'Unknown error'}`,
        severity: 'error',
      });
    }
    setConfirmDialog({ open: false, action: 'delete', message: '' });
  };

  const openConfirmDialog = (action: 'start' | 'stop' | 'delete') => {
    let count = 0;
    let message = '';
    
    switch (action) {
      case 'start':
        count = filteredDevices.length;
        message = `Start monitoring for ${count} devices${isFiltered ? ' (filtered)' : ''}?`;
        break;
      case 'stop':
        count = isFiltered ? filteredDevices.length : allDevices.length;
        message = `Stop monitoring for ${isFiltered ? `${count} filtered` : 'all'} devices?`;
        break;
      case 'delete':
        count = selectedDevices.length;
        message = `Permanently delete ${count} selected device${count !== 1 ? 's' : ''}? This action cannot be undone.`;
        break;
    }
    
    setConfirmDialog({
      open: true,
      action,
      message,
    });
  };

  return (
    <Slide direction="down" in={true} mountOnEnter unmountOnExit>
      <Paper 
        elevation={1} 
        sx={{ 
          p: 2, 
          mb: 3,
          transition: 'all 0.3s ease',
          '&:hover': {
            boxShadow: 3,
          }
        }}
      >
        <Typography variant="h6" gutterBottom>
          Bulk Monitoring Controls
        </Typography>
        
        {isFiltered && (
          <Alert severity="info" sx={{ mb: 2 }}>
            Note: Operations on filtered devices are performed individually and may take longer.
          </Alert>
        )}
      
      <Box sx={{ display: 'flex', gap: 2, alignItems: 'center', flexWrap: 'wrap' }}>
        {/* Device Count */}
        <Chip
          label={isFiltered ? `${filteredDevices.length} of ${allDevices.length} devices (filtered)` : `${allDevices.length} devices`}
          color="primary"
          variant="outlined"
          sx={{
            transition: 'all 0.2s ease',
            '&:hover': {
              transform: 'scale(1.05)',
              backgroundColor: 'primary.light',
              color: 'primary.contrastText',
            }
          }}
        />

        {/* Selected Devices Count */}
        {selectedDevices.length > 0 && (
          <Chip
            label={`${selectedDevices.length} selected`}
            color="secondary"
            variant="filled"
            sx={{
              transition: 'all 0.2s ease',
              fontWeight: 'bold',
            }}
          />
        )}

        {/* Action Buttons */}
        <ButtonGroup 
          variant="contained" 
          disabled={loading}
          sx={{
            '& .MuiButton-root': {
              transition: 'all 0.3s ease',
              '&:hover': {
                transform: 'translateY(-2px)',
                boxShadow: 4,
              }
            }
          }}
        >
          <Button
            color="success"
            startIcon={loading ? <CircularProgress size={20} /> : <StartIcon />}
            onClick={() => openConfirmDialog('start')}
            disabled={filteredDevices.length === 0}
          >
            Start Monitoring {isFiltered ? '(Filtered)' : ''}
          </Button>
          <Button
            color="error"
            startIcon={loading ? <CircularProgress size={20} /> : <StopIcon />}
            onClick={() => openConfirmDialog('stop')}
          >
            Stop {isFiltered ? 'Filtered' : 'All'} Monitoring
          </Button>
        </ButtonGroup>

        {/* Edit and Delete Buttons - only show when devices are selected */}
        {selectedDevices.length > 0 && (
          <>
            <Button
              variant="contained"
              color="primary"
              startIcon={<EditIcon />}
              onClick={onBulkEdit}
              disabled={loading}
              sx={{
                transition: 'all 0.3s ease',
                '&:hover': {
                  transform: 'translateY(-2px)',
                  boxShadow: 4,
                }
              }}
            >
              Edit Selected ({selectedDevices.length})
            </Button>
            <Button
              variant="contained"
              color="error"
              startIcon={loading ? <CircularProgress size={20} /> : <DeleteIcon />}
              onClick={() => openConfirmDialog('delete')}
              disabled={loading}
              sx={{
                transition: 'all 0.3s ease',
                '&:hover': {
                  transform: 'translateY(-2px)',
                  boxShadow: 4,
                }
              }}
            >
              Delete Selected ({selectedDevices.length})
            </Button>
          </>
        )}
      </Box>

      {/* Confirmation Dialog */}
      <Dialog
        open={confirmDialog.open}
        onClose={() => setConfirmDialog({ ...confirmDialog, open: false })}
        TransitionComponent={Zoom}
        transitionDuration={300}
      >
        <DialogTitle>Confirm Bulk Operation</DialogTitle>
        <DialogContent>
          <DialogContentText>{confirmDialog.message}</DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setConfirmDialog({ ...confirmDialog, open: false })}>
            Cancel
          </Button>
          <Button
            onClick={
              confirmDialog.action === 'start' ? handleStartMonitoring : 
              confirmDialog.action === 'stop' ? handleStopMonitoring :
              handleBulkDelete
            }
            color={
              confirmDialog.action === 'start' ? 'success' : 
              confirmDialog.action === 'delete' ? 'error' : 
              'error'
            }
            variant="contained"
          >
            Confirm
          </Button>
        </DialogActions>
      </Dialog>

      {/* Snackbar for feedback */}
      <Snackbar
        open={snackbar.open}
        autoHideDuration={6000}
        onClose={() => setSnackbar({ ...snackbar, open: false })}
      >
        <Alert
          onClose={() => setSnackbar({ ...snackbar, open: false })}
          severity={snackbar.severity}
        >
          {snackbar.message}
        </Alert>
      </Snackbar>
      </Paper>
    </Slide>
  );
};