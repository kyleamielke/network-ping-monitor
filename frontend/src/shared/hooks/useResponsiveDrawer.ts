import { useState } from 'react';
import { useMediaQuery, useTheme } from '@mui/material';

export const useResponsiveDrawer = () => {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));
  const [mobileOpen, setMobileOpen] = useState(false);

  const handleDrawerToggle = () => {
    setMobileOpen(!mobileOpen);
  };

  const handleMobileClose = () => {
    if (isMobile) {
      setMobileOpen(false);
    }
  };

  return {
    isMobile,
    mobileOpen,
    handleDrawerToggle,
    handleMobileClose,
  };
};