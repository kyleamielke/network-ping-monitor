export interface Role {
  id: string;
  name: string;
  description?: string;
}

export interface Device {
  id: string;
  version: number;
  name: string;
  ipAddress?: string;
  hostname?: string;
  macAddress?: string;
  type?: string;
  os?: string;
  osType?: string;
  make?: string;
  model?: string;
  endpointId?: string;
  assetTag?: string;
  description?: string;
  location?: string;
  site?: string;
  status?: string;
  createdAt?: string;
  updatedAt?: string;
  createdBy?: string;
  lastModifiedBy?: string;
  roles?: Role[];
  // Legacy support
  uuid?: string;
  deviceType?: string;
}

export interface CreateDeviceInput {
  name: string;
  ipAddress?: string;
  hostname?: string;
  macAddress?: string;
  type?: string;
  os?: string;
  osType?: string;
  make?: string;
  model?: string;
  endpointId?: string;
  assetTag?: string;
  description?: string;
  location?: string;
  site?: string;
}

export interface UpdateDeviceInput {
  expectedVersion: number;
  name?: string;
  ipAddress?: string;
  hostname?: string;
  macAddress?: string;
  type?: string;
  os?: string;
  osType?: string;
  make?: string;
  model?: string;
  endpointId?: string;
  assetTag?: string;
  description?: string;
  location?: string;
  site?: string;
}

// Device with monitoring information (from devicesWithMonitoring query)
export interface DeviceWithMonitoring extends Device {
  currentStatus?: {
    online: boolean;
    responseTime?: number;
    lastStatusChange?: string;
  };
  pingTarget?: {
    deviceId: string;
    ipAddress?: string;
    hostname?: string;
    monitored: boolean;
    pingIntervalSeconds: number;
  };
  recentPings?: {
    timestamp: string;
    success: boolean;
    responseTimeMs?: number;
  }[];
}

// Subscription types
export interface PingUpdate {
  deviceId: string;
  timestamp: string;
  success: boolean;
  responseTimeMs?: number;
  previousStatus: string;
  currentStatus: string;
}

export interface DeviceStatusUpdate {
  deviceId: string;
  previousStatus: string;
  currentStatus: string;
  timestamp: string;
}

export interface AlertUpdate {
  id: string;
  deviceId: string;
  deviceName: string;
  alertType: string;
  message: string;
  timestamp: string;
  resolved: boolean;
  resolvedAt?: string;
  acknowledged: boolean;
  acknowledgedBy?: string;
  acknowledgedAt?: string;
  createdAt: string;
  updatedAt: string;
}