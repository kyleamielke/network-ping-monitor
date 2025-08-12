export type ReportType = 'DEVICE_UPTIME' | 'SYSTEM_HEALTH' | 'NETWORK_PERFORMANCE' | 'ALERT_SUMMARY';
export type ReportFormat = 'PDF' | 'CSV' | 'EXCEL';

export interface ReportRequest {
  reportType: ReportType;
  format: ReportFormat;
  title?: string;
  startDate?: string;
  endDate?: string;
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

export const REPORT_TYPE_LABELS: Record<ReportType, string> = {
  DEVICE_UPTIME: 'Device Uptime',
  SYSTEM_HEALTH: 'System Health',
  NETWORK_PERFORMANCE: 'Network Performance',
  ALERT_SUMMARY: 'Alert Summary',
};

export const REPORT_TYPE_DESCRIPTIONS: Record<ReportType, string> = {
  DEVICE_UPTIME: 'Device availability statistics over time',
  SYSTEM_HEALTH: 'Overall system health and performance metrics',
  NETWORK_PERFORMANCE: 'Network latency and response time metrics',
  ALERT_SUMMARY: 'Historical alerts and incident reports',
};