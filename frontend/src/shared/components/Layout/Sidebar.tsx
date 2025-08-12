import React from 'react';
import {
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Toolbar,
  Typography,
  Box,
  Divider,
} from '@mui/material';
import { Settings as SettingsIcon } from '@mui/icons-material';
import { useLocation, useNavigate } from 'react-router-dom';
import { NAVIGATION_ITEMS } from '@/shared/constants/navigation';
import { useLocale } from '@/shared/contexts/LocaleContext';

interface SidebarProps {
  onNavigate?: () => void;
}

export const Sidebar: React.FC<SidebarProps> = ({ onNavigate }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const { t } = useLocale();

  const handleNavigate = (path: string) => {
    navigate(path);
    onNavigate?.();
  };

  return (
    <Box sx={{ height: '100%', display: 'flex', flexDirection: 'column' }} role="navigation" aria-label="Main navigation">
      <Toolbar>
        <Typography variant="h6" noWrap component="div">
          NetworkPing Monitor
        </Typography>
      </Toolbar>
      
      {/* Main navigation items */}
      <nav aria-label="Primary navigation">
        <List sx={{ flexGrow: 1 }}>
        {NAVIGATION_ITEMS.map((item) => {
          const IconComponent = item.icon;
          return (
            <ListItem key={item.path} disablePadding>
              <ListItemButton
                selected={location.pathname === item.path}
                onClick={() => handleNavigate(item.path)}
                aria-label={item.label}
                aria-current={location.pathname === item.path ? 'page' : undefined}
              >
                <ListItemIcon aria-hidden="true">
                  <IconComponent />
                </ListItemIcon>
                <ListItemText primary={t(`nav.${item.path.slice(1)}`)} />
              </ListItemButton>
            </ListItem>
          );
        })}
        </List>
      </nav>
      
      {/* Bottom section with Settings */}
      <Box>
        <Divider />
        <nav aria-label="Settings navigation">
          <List>
          <ListItem disablePadding>
            <ListItemButton
              selected={location.pathname === '/settings'}
              onClick={() => handleNavigate('/settings')}
              aria-label="Settings"
              aria-current={location.pathname === '/settings' ? 'page' : undefined}
              sx={{
                '&.Mui-selected': {
                  backgroundColor: 'action.selected',
                },
              }}
            >
              <ListItemIcon aria-hidden="true">
                <SettingsIcon />
              </ListItemIcon>
              <ListItemText primary={t('nav.settings')} />
            </ListItemButton>
          </ListItem>
          </List>
        </nav>
      </Box>
    </Box>
  );
};