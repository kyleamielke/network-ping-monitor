import {
  Dashboard as DashboardIcon,
  Devices as DevicesIcon,
  Warning as AlertsIcon,
  Assessment as ReportsIcon,
} from '@mui/icons-material';

export interface NavigationItem {
  path: string;
  label: string;
  icon: React.ComponentType;
}

export const NAVIGATION_ITEMS: NavigationItem[] = [
  { path: '/dashboard', label: 'Dashboard', icon: DashboardIcon },
  { path: '/devices', label: 'Devices', icon: DevicesIcon },
  { path: '/alerts', label: 'Alerts', icon: AlertsIcon },
  { path: '/reports', label: 'Reports', icon: ReportsIcon },
];

export const DRAWER_WIDTH = 240;