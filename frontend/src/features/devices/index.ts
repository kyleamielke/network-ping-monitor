export { DevicesPage } from '@/features/devices/components/DevicesPage';
export { DeviceForm } from '@/features/devices/components/DeviceForm';
export { DeviceList } from '@/features/devices/components/DeviceList';
export { BulkMonitoringControls } from '@/features/devices/components/BulkMonitoringControls';

export { useDevices } from '@/features/devices/hooks/useDevices';
export { useDeviceOperations } from '@/features/devices/hooks/useDeviceOperations';

export { DEVICE_QUERIES } from '@/features/devices/api/deviceQueries';
export { DEVICE_MUTATIONS } from '@/features/devices/api/deviceMutations';
export type { CreateDeviceInput, UpdateDeviceInput } from '@/features/devices/api/deviceMutations';