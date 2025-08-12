import React from 'react';
import { Box, CssBaseline, Drawer, Toolbar } from '@mui/material';
import { AppHeader } from './AppHeader';
import { Sidebar } from './Sidebar';
import { SkipNavigation } from '@/shared/components/SkipNavigation';
import { useResponsiveDrawer } from '@/shared/hooks/useResponsiveDrawer';
import { useDeviceSubscriptions } from '@/shared/hooks/useDeviceSubscriptions';
import { useKeyboardShortcuts } from '@/shared/hooks/useKeyboardShortcuts';
import { DRAWER_WIDTH } from '@/shared/constants/navigation';
import { useSubscription } from '@apollo/client';
import { ALERT_STREAM_SUBSCRIPTION } from '@/shared/api/subscriptions';
import { useToast } from '@/shared/contexts/ToastContext';

interface LayoutProps {
  children: React.ReactNode;
}

export const Layout: React.FC<LayoutProps> = ({ children }) => {
  const { mobileOpen, handleDrawerToggle, handleMobileClose } = useResponsiveDrawer();
  const [subscriptionReady, setSubscriptionReady] = React.useState(false);
  const { showSuccess, showWarning, showAlertToast } = useToast();
  
  // Enable keyboard shortcuts
  useKeyboardShortcuts();
  
  // Wait a moment before starting subscription to ensure Apollo Client is ready
  React.useEffect(() => {
    const timer = setTimeout(() => {
      setSubscriptionReady(true);
    }, 1000); // 1 second delay
    
    return () => {
      clearTimeout(timer);
    };
  }, []);
  
  // Global subscription that stays alive across all page navigation
  useDeviceSubscriptions({
    skip: !subscriptionReady, // Don't start subscription until ready
    onPingUpdate: () => {
      // Individual pages handle their own data updates
    },
    onStatusUpdate: (data) => {
      // Show toast for monitoring state changes
      if (data?.deviceStatusUpdates) {
        const update = data.deviceStatusUpdates;
        
        // Only show toast for monitoring state changes
        if (update.previousStatus === 'NOT_MONITORED' && update.currentStatus !== 'NOT_MONITORED') {
          showSuccess(`Monitoring started for device`);
        } else if (update.previousStatus !== 'NOT_MONITORED' && update.currentStatus === 'NOT_MONITORED') {
          showWarning(`Monitoring stopped for device`);
        }
      }
    },
  });
  
  // Global alert subscription for toast notifications
  useSubscription(ALERT_STREAM_SUBSCRIPTION, {
    skip: !subscriptionReady,
    onData: ({ data }) => {
      if (data?.data?.alertStream) {
        const alert = data.data.alertStream;
        
        // Show clickable toast notification based on alert type
        const emoji = alert.type === 'DEVICE_RECOVERED' ? '✅' : 
                     alert.type === 'DEVICE_DOWN' ? '🔴' : '⚠️';
        
        const message = `${emoji} ${alert.type.replace('_', ' ')} - ${alert.deviceName}`;
        
        // Determine severity based on alert type
        const severity = alert.type === 'DEVICE_RECOVERED' ? 'success' : 
                        alert.type === 'DEVICE_DOWN' ? 'error' : 'warning';
        
        // Show clickable alert toast
        showAlertToast(message, alert.id, severity);
      }
    },
    onError: (error) => {
      console.error('Alert subscription error:', error);
    }
  });

  return (
    <Box sx={{ display: 'flex' }}>
      <CssBaseline />
      <SkipNavigation />
      
      <AppHeader onMenuClick={handleDrawerToggle} />
      
      <Box
        component="nav"
        sx={{ width: { md: DRAWER_WIDTH }, flexShrink: { md: 0 } }}
      >
        {/* Mobile Drawer */}
        <Drawer
          variant="temporary"
          open={mobileOpen}
          onClose={handleDrawerToggle}
          ModalProps={{ keepMounted: true }}
          sx={{
            display: { xs: 'block', md: 'none' },
            '& .MuiDrawer-paper': { boxSizing: 'border-box', width: DRAWER_WIDTH },
          }}
        >
          <Sidebar onNavigate={handleMobileClose} />
        </Drawer>
        
        {/* Desktop Drawer */}
        <Drawer
          variant="permanent"
          sx={{
            display: { xs: 'none', md: 'block' },
            '& .MuiDrawer-paper': { boxSizing: 'border-box', width: DRAWER_WIDTH },
          }}
          open
        >
          <Sidebar />
        </Drawer>
      </Box>
      
      <Box
        component="main"
        role="main"
        aria-label="Main content"
        sx={{
          flexGrow: 1,
          p: 3,
          width: { md: `calc(100% - ${DRAWER_WIDTH}px)` },
        }}
      >
        <Toolbar />
        <div id="main-content" tabIndex={-1}>
          {children}
        </div>
      </Box>
    </Box>
  );
};