export enum ReportType {
  DEVICE_UPTIME = 'DEVICE_UPTIME',
  SYSTEM_HEALTH = 'SYSTEM_HEALTH',
  NETWORK_PERFORMANCE = 'NETWORK_PERFORMANCE',
  ALERT_SUMMARY = 'ALERT_SUMMARY',
}

export enum ReportFormat {
  PDF = 'PDF',
  CSV = 'CSV',
  EXCEL = 'EXCEL',
}

export interface ReportRequest {
  reportType: ReportType;
  format: ReportFormat;
  startDate?: string;
  endDate?: string;
  deviceIds?: string[];
  title?: string;
}

export interface ReportResponse {
  reportId: string;
  filename: string;
  reportType: ReportType;
  format: ReportFormat;
  generatedAt: string;
  fileSizeBytes: number;
  downloadUrl: string;
}