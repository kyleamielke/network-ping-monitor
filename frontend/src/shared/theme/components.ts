import { Components, Theme } from '@mui/material/styles';

export const components: Components<Omit<Theme, 'components'>> = {
  MuiButton: {
    styleOverrides: {
      root: {
        textTransform: 'none',
        borderRadius: 8,
      },
    },
  },
  MuiCard: {
    styleOverrides: {
      root: {
        borderRadius: 12,
        boxShadow: '0 2px 12px rgba(0,0,0,0.08)',
      },
    },
  },
  MuiChip: {
    styleOverrides: {
      root: {
        borderRadius: 16,
      },
    },
  },
  MuiAppBar: {
    styleOverrides: {
      root: {
        boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
      },
    },
  },
  MuiDrawer: {
    styleOverrides: {
      paper: {
        borderRight: '1px solid rgba(0, 0, 0, 0.12)',
      },
    },
  },
  MuiListItemIcon: {
    styleOverrides: {
      root: {
        minWidth: 40,
      },
    },
  },
};