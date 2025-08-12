import React from 'react';
import { IconButton, Tooltip } from '@mui/material';
import { 
  Brightness4 as DarkModeIcon,
  Brightness7 as LightModeIcon,
} from '@mui/icons-material';
import { useTheme } from '@/shared/contexts/ThemeContext';

export const ThemeModeToggle: React.FC = () => {
  const { themeSettings, toggleThemeMode } = useTheme();
  const isDark = themeSettings.mode === 'dark';
  
  return (
    <Tooltip title={`Switch to ${isDark ? 'light' : 'dark'} mode`}>
      <IconButton
        onClick={toggleThemeMode}
        color="inherit"
        aria-label={`Switch to ${isDark ? 'light' : 'dark'} mode`}
        sx={{
          transition: 'all 0.3s ease',
          '&:hover': {
            transform: 'rotate(180deg)',
          }
        }}
      >
        {isDark ? <LightModeIcon /> : <DarkModeIcon />}
      </IconButton>
    </Tooltip>
  );
};