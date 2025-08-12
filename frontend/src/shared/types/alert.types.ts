export interface Alert {
  id: string;
  deviceId: string;
  deviceName: string;
  alertType: AlertType;
  message: string;
  timestamp: string;
  resolved: boolean;
  resolvedAt?: string;
  acknowledged: boolean;
  acknowledgedBy?: string;
  acknowledgedAt?: string;
  createdAt: string;
  updatedAt: string;
  // Legacy support
  type?: AlertType;
}

export interface CreateAlertInput {
  deviceId: string;
  alertType: AlertType;
  message: string;
}

export enum AlertType {
  DEVICE_DOWN = 'DEVICE_DOWN',
  DEVICE_RECOVERED = 'DEVICE_RECOVERED',
  HIGH_RESPONSE_TIME = 'HIGH_RESPONSE_TIME',
  PACKET_LOSS = 'PACKET_LOSS',
  CUSTOM = 'CUSTOM',
}