import { DeviceWithMonitoring } from '@/shared/types/device.types';
import { Alert } from '@/shared/types/alert.types';

export enum DeviceStatusEnum {
  ONLINE = 'ONLINE',
  OFFLINE = 'OFFLINE',
  UNKNOWN = 'UNKNOWN',
  ALERTING = 'ALERTING',
  NOT_MONITORED = 'NOT_MONITORED',
}

export interface DeviceStatus {
  deviceId: string;
  deviceName: string;
  ipAddress?: string;
  hostname?: string;
  online: boolean;
  lastStatusChange?: string;
  responseTime?: number;
  status: DeviceStatusEnum;
  consecutiveSuccesses?: number;
  consecutiveFailures?: number;
}

export interface PingStatistics {
  deviceId: string;
  totalPings: number;
  successfulPings: number;
  failedPings: number;
  averageResponseTime?: number;
  minResponseTime?: number;
  maxResponseTime?: number;
  uptime: number;
  packetLoss: number;
  lastPingTime?: string;
  // Legacy support
  avgResponseTime?: number;
}

export interface PingResult {
  deviceId: string;
  timestamp: string;
  responseTime?: number;
  success: boolean;
  errorMessage?: string;
}

export interface DeviceMonitoring {
  device: DeviceWithMonitoring;
  pingTarget?: PingTarget;
  currentStatus?: DeviceStatus;
  recentAlerts: Alert[];
  statistics?: PingStatistics;
  // Legacy support
  pingStatistics?: PingStatistics;
  pingHistory?: PingResult[];
  deviceId?: string;
  isOnline?: boolean;
  lastPingTime?: string;
  lastResponseTime?: number;
}

export interface MonitoringDashboard {
  totalDevices: number;
  onlineDevices: number;
  offlineDevices: number;
  monitoredDevices: number;
  totalAlerts: number;
  unresolvedAlerts: number;
  systemHealth: SystemHealth;
  // Legacy support
  devices?: DeviceMonitoring[];
  summary?: DashboardSummary;
}

export interface DashboardSummary {
  totalDevices: number;
  onlineDevices: number;
  offlineDevices: number;
  alertingDevices: number;
  avgResponseTime: number;
}

export interface SystemHealth {
  apiGateway: ServiceHealth;
  deviceService: ServiceHealth;
  pingService: ServiceHealth;
  alertService: ServiceHealth;
  // Legacy support
  servicesStatus?: ServiceStatus[];
  kafkaConnected?: boolean;
  databaseConnected?: boolean;
}

export interface ServiceHealth {
  name: string;
  status: HealthStatus;
  message?: string;
}

export interface ServiceStatus {
  serviceName: string;
  status: ServiceHealthStatus;
  lastChecked: string;
}

export enum HealthStatus {
  UP = 'UP',
  DOWN = 'DOWN',
  UNKNOWN = 'UNKNOWN',
}

export enum ServiceHealthStatus {
  UP = 'UP',
  DOWN = 'DOWN',
  DEGRADED = 'DEGRADED',
  UNKNOWN = 'UNKNOWN',
}

export interface PingTarget {
  deviceId: string;
  ipAddress?: string;
  hostname?: string;
  monitored: boolean;
  pingIntervalSeconds: number;
  createdAt?: string;
  updatedAt?: string;
}

export enum TimeRange {
  LAST_HOUR = 'LAST_HOUR',
  LAST_6_HOURS = 'LAST_6_HOURS',
  LAST_24_HOURS = 'LAST_24_HOURS',
  LAST_7_DAYS = 'LAST_7_DAYS',
  LAST_30_DAYS = 'LAST_30_DAYS',
}