import React from 'react';
import {
  AppBar,
  Toolbar,
  Typography,
  IconButton,
  Box,
} from '@mui/material';
import { Menu as MenuIcon } from '@mui/icons-material';
import { DRAWER_WIDTH } from '@/shared/constants/navigation';
import { GlobalSearch } from './GlobalSearch';
import { ThemeModeToggle } from '@/shared/components/ThemeModeToggle';
import { useLocale } from '@/shared/contexts/LocaleContext';

interface AppHeaderProps {
  onMenuClick: () => void;
}

export const AppHeader: React.FC<AppHeaderProps> = ({ onMenuClick }) => {
  const { t } = useLocale();
  
  return (
    <AppBar
      position="fixed"
      component="header"
      role="banner"
      sx={{
        width: { md: `calc(100% - ${DRAWER_WIDTH}px)` },
        ml: { md: `${DRAWER_WIDTH}px` },
      }}
    >
      <Toolbar role="navigation" aria-label="Top navigation bar">
        <IconButton
          color="inherit"
          aria-label="open drawer"
          edge="start"
          onClick={onMenuClick}
          sx={{ mr: 2, display: { md: 'none' } }}
        >
          <MenuIcon />
        </IconButton>
        <Typography variant="h6" noWrap component="div" sx={{ flexGrow: 1 }}>
          {t('dashboard.title')}
        </Typography>
        
        {/* Global Search and Theme Toggle */}
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <GlobalSearch />
          <ThemeModeToggle />
        </Box>
      </Toolbar>
    </AppBar>
  );
};