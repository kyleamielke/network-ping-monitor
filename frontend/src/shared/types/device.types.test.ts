import { describe, it, expect } from 'vitest';
import type { 
  Role, 
  Device, 
  CreateDeviceInput, 
  UpdateDeviceInput, 
  DeviceWithMonitoring, 
  PingUpdate, 
  DeviceStatusUpdate, 
  AlertUpdate 
} from './device.types';

describe('Device Types Tests', () => {
  it('should allow valid Role objects', () => {
    const role: Role = {
      id: 'role-1',
      name: 'Admin',
      description: 'Administrator role'
    };
    
    expect(role.id).toBe('role-1');
    expect(role.name).toBe('Admin');
    expect(role.description).toBe('Administrator role');
  });

  it('should allow Role without optional description', () => {
    const role: Role = {
      id: 'role-2', 
      name: 'User'
    };
    
    expect(role.id).toBe('role-2');
    expect(role.name).toBe('User');
    expect(role.description).toBeUndefined();
  });

  it('should allow valid Device objects', () => {
    const device: Device = {
      id: 'device-1',
      version: 1,
      name: 'Test Server',
      ipAddress: '192.168.1.100',
      type: 'server'
    };
    
    expect(device.id).toBe('device-1');
    expect(device.version).toBe(1);
    expect(device.name).toBe('Test Server');
    expect(device.ipAddress).toBe('192.168.1.100');
    expect(device.type).toBe('server');
  });

  it('should allow Device with only required fields', () => {
    const device: Device = {
      id: 'device-2',
      version: 1,
      name: 'Minimal Device'
    };
    
    expect(device.id).toBe('device-2');
    expect(device.version).toBe(1);
    expect(device.name).toBe('Minimal Device');
  });

  it('should allow valid CreateDeviceInput objects', () => {
    const input: CreateDeviceInput = {
      name: 'New Device',
      ipAddress: '10.0.0.1',
      type: 'router'
    };
    
    expect(input.name).toBe('New Device');
    expect(input.ipAddress).toBe('10.0.0.1');
    expect(input.type).toBe('router');
  });

  it('should allow CreateDeviceInput with only name', () => {
    const input: CreateDeviceInput = {
      name: 'Basic Device'
    };
    
    expect(input.name).toBe('Basic Device');
  });

  it('should allow valid UpdateDeviceInput objects', () => {
    const input: UpdateDeviceInput = {
      expectedVersion: 2,
      name: 'Updated Device',
      ipAddress: '192.168.1.200'
    };
    
    expect(input.expectedVersion).toBe(2);
    expect(input.name).toBe('Updated Device');
    expect(input.ipAddress).toBe('192.168.1.200');
  });

  it('should allow valid DeviceWithMonitoring objects', () => {
    const device: DeviceWithMonitoring = {
      id: 'device-3',
      version: 1,
      name: 'Monitored Device',
      currentStatus: {
        online: true,
        responseTime: 25,
        lastStatusChange: '2025-01-01T12:00:00Z'
      },
      pingTarget: {
        deviceId: 'device-3',
        monitored: true,
        pingIntervalSeconds: 30
      }
    };
    
    expect(device.currentStatus?.online).toBe(true);
    expect(device.currentStatus?.responseTime).toBe(25);
    expect(device.pingTarget?.monitored).toBe(true);
    expect(device.pingTarget?.pingIntervalSeconds).toBe(30);
  });

  it('should allow valid PingUpdate objects', () => {
    const update: PingUpdate = {
      deviceId: 'device-4',
      timestamp: '2025-01-01T12:00:00Z',
      success: true,
      responseTimeMs: 15,
      previousStatus: 'DOWN',
      currentStatus: 'UP'
    };
    
    expect(update.deviceId).toBe('device-4');
    expect(update.success).toBe(true);
    expect(update.responseTimeMs).toBe(15);
    expect(update.previousStatus).toBe('DOWN');
    expect(update.currentStatus).toBe('UP');
  });

  it('should allow valid DeviceStatusUpdate objects', () => {
    const update: DeviceStatusUpdate = {
      deviceId: 'device-5',
      previousStatus: 'OFFLINE',
      currentStatus: 'ONLINE',
      timestamp: '2025-01-01T12:00:00Z'
    };
    
    expect(update.deviceId).toBe('device-5');
    expect(update.previousStatus).toBe('OFFLINE');
    expect(update.currentStatus).toBe('ONLINE');
    expect(update.timestamp).toBe('2025-01-01T12:00:00Z');
  });

  it('should allow valid AlertUpdate objects', () => {
    const alert: AlertUpdate = {
      id: 'alert-1',
      deviceId: 'device-6',
      deviceName: 'Alert Device',
      alertType: 'PING_FAILURE',
      message: 'Device is not responding',
      timestamp: '2025-01-01T12:00:00Z',
      resolved: false,
      acknowledged: false,
      createdAt: '2025-01-01T12:00:00Z',
      updatedAt: '2025-01-01T12:00:00Z'
    };
    
    expect(alert.id).toBe('alert-1');
    expect(alert.deviceId).toBe('device-6');
    expect(alert.alertType).toBe('PING_FAILURE');
    expect(alert.resolved).toBe(false);
    expect(alert.acknowledged).toBe(false);
  });

  it('should allow Device with roles array', () => {
    const device: Device = {
      id: 'device-7',
      version: 1,
      name: 'Device with Roles',
      roles: [
        { id: 'role-1', name: 'Admin' },
        { id: 'role-2', name: 'Monitor' }
      ]
    };
    
    expect(device.roles).toHaveLength(2);
    expect(device.roles?.[0].name).toBe('Admin');
    expect(device.roles?.[1].name).toBe('Monitor');
  });

  it('should handle legacy Device fields', () => {
    const device: Device = {
      id: 'device-8',
      version: 1,
      name: 'Legacy Device',
      uuid: 'legacy-uuid-123',
      deviceType: 'LEGACY_TYPE'
    };
    
    expect(device.uuid).toBe('legacy-uuid-123');
    expect(device.deviceType).toBe('LEGACY_TYPE');
  });
});