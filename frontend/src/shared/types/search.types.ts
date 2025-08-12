import { Device } from '@/shared/types/device.types';

export interface DeviceSearchCriteria {
  uuid?: string;
  endpointId?: string;
  assetTag?: string;
  name?: string;
  ipAddress?: string;
  macAddress?: string;
  deviceType?: string;
  os?: string;
  osType?: string;
  make?: string;
  model?: string;
  site?: string;
  isAssigned?: boolean;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: string;
}

export interface DeviceSearchResult {
  devices: Device[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
  hasNext: boolean;
  hasPrevious: boolean;
}