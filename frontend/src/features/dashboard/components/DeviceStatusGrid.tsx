import React, { useRef, useEffect } from 'react';
import { Grid, Typography, Box, Grow } from '@mui/material';
import { DeviceStatusCard } from './DeviceStatusCard';
import { DeviceMonitoring } from '@/shared/types/monitoring.types';

interface DeviceStatusGridProps {
  devices: DeviceMonitoring[];
  filterType?: string;
  totalCount?: number;
  onDeviceSelect?: (deviceId: string, deviceName?: string) => void;
  selectedDevice?: string | null;
  columns?: number;
}

export const DeviceStatusGrid: React.FC<DeviceStatusGridProps> = ({ 
  devices, 
  filterType = 'all',
  totalCount,
  onDeviceSelect,
  selectedDevice,
  columns = 1
}) => {
  const scrollContainerRef = useRef<HTMLDivElement>(null);
  const selectedDeviceRef = useRef<HTMLDivElement>(null);

  // Scroll selected device into view when in condensed mode (sidebar)
  useEffect(() => {
    if (selectedDevice && columns === 1 && selectedDeviceRef.current && scrollContainerRef.current) {
      // Small delay to ensure the layout has settled
      setTimeout(() => {
        selectedDeviceRef.current?.scrollIntoView({
          behavior: 'smooth',
          block: 'center'
        });
      }, 100);
    }
  }, [selectedDevice, columns]);
  const getFilterTitle = () => {
    switch (filterType) {
      case 'online': return 'Online Devices';
      case 'offline': return 'Offline Devices';
      case 'alerting': return 'Alerting Devices';
      case 'high_latency': return 'High Latency Devices';
      case 'all':
      default: return 'Device Status';
    }
  };

  return (
    <>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
        <Typography variant="h6">
          {getFilterTitle()}
        </Typography>
        <Typography variant="body2" color="text.secondary">
          {totalCount || devices.length} devices
        </Typography>
      </Box>
      
      <Box 
        ref={scrollContainerRef}
        sx={{ 
          height: columns > 1 ? 'calc(100vh - 450px)' : 'calc(100vh - 450px)',
          minHeight: 400,
          overflowY: 'auto',
          pt: 0.5, // Add small padding to prevent clipping
          '&::-webkit-scrollbar': {
            width: '6px',
          },
          '&::-webkit-scrollbar-track': {
            backgroundColor: 'rgba(0,0,0,.1)',
            borderRadius: '10px',
          },
          '&::-webkit-scrollbar-thumb': {
            borderRadius: '10px',
            backgroundColor: 'rgba(0,0,0,.2)',
          },
        }}
      >
        <Grid container spacing={2}>
          {devices.length > 0 ? (
            devices.map((deviceMonitoring, index) => {
              const gridSize = columns === 3 ? 4 : columns === 2 ? 6 : 12; // xs=4 for 3 cols, xs=6 for 2 cols, xs=12 for 1 col
              const isSelected = selectedDevice === deviceMonitoring.device.id;
              
              return (
                <Grow
                  in={true}
                  key={deviceMonitoring.device.id}
                  timeout={{ enter: 300 + (index * 50) }}
                >
                  <Grid 
                    item 
                    xs={gridSize}
                    ref={isSelected ? selectedDeviceRef : null}
                  >
                    <DeviceStatusCard
                      device={deviceMonitoring.device}
                      status={deviceMonitoring.currentStatus}
                      pingTarget={deviceMonitoring.pingTarget}
                      recentPings={deviceMonitoring.device.recentPings}
                      onClick={onDeviceSelect ? () => onDeviceSelect(deviceMonitoring.device.id, deviceMonitoring.device.name) : undefined}
                      selected={isSelected}
                      clickable={!!onDeviceSelect}
                    />
                  </Grid>
                </Grow>
              );
            })
          ) : (
            <Grid item xs={12}>
              <Typography 
                variant="body2" 
                color="text.secondary" 
                textAlign="center"
                py={4}
              >
                No devices match the current filter
              </Typography>
            </Grid>
          )}
        </Grid>
      </Box>
    </>
  );
};