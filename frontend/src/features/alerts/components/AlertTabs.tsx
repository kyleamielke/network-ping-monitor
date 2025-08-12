import React from 'react';
import { Tabs, Tab, Chip, Fade, Box } from '@mui/material';
import { useLocale } from '@/shared/contexts/LocaleContext';

interface AlertTabsProps {
  activeTab: number;
  onTabChange: (newValue: number) => void;
  counts: {
    all: number;
    unresolved: number;
    unacknowledged: number;
  };
}

export const AlertTabs: React.FC<AlertTabsProps> = React.memo(({
  activeTab,
  onTabChange,
  counts,
}) => {
  const { t } = useLocale();
  return (
    <Fade in={true} timeout={600}>
      <Box sx={{ 
        borderBottom: 1, 
        borderColor: 'divider', 
        mb: 2,
        '& .MuiTab-root': {
          transition: 'all 0.3s ease',
          '&:hover': {
            transform: 'translateY(-2px)',
            color: 'primary.main',
          }
        }
      }}>
        <Tabs 
          value={activeTab} 
          onChange={(_, newValue) => onTabChange(newValue)}
          sx={{
            '& .MuiTabs-indicator': {
              transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
            }
          }}
        >
          <Tab 
            label={
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <span>{t('alerts.allAlerts')}</span>
                <Chip 
                  label={counts.all} 
                  size="small"
                  sx={{
                    height: 20,
                    minWidth: 20,
                    '& .MuiChip-label': {
                      px: 1,
                      fontSize: '0.75rem',
                    },
                    transition: 'all 0.3s ease',
                  }}
                />
              </Box>
            } 
          />
          <Tab 
            label={
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <span>{t('alerts.unresolved')}</span>
                <Chip 
                  label={counts.unresolved} 
                  size="small"
                  color="error"
                  sx={{
                    height: 20,
                    minWidth: 20,
                    '& .MuiChip-label': {
                      px: 1,
                      fontSize: '0.75rem',
                    },
                    transition: 'all 0.3s ease',
                    animation: counts.unresolved > 0 ? 'pulse 2s infinite' : 'none',
                    '@keyframes pulse': {
                      '0%': {
                        transform: 'scale(1)',
                        opacity: 1,
                      },
                      '50%': {
                        transform: 'scale(1.1)',
                        opacity: 0.8,
                      },
                      '100%': {
                        transform: 'scale(1)',
                        opacity: 1,
                      },
                    },
                  }}
                />
              </Box>
            }
          />
          <Tab 
            label={
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <span>{t('alerts.unacknowledged')}</span>
                <Chip 
                  label={counts.unacknowledged} 
                  size="small"
                  color="warning"
                  sx={{
                    height: 20,
                    minWidth: 20,
                    '& .MuiChip-label': {
                      px: 1,
                      fontSize: '0.75rem',
                    },
                    transition: 'all 0.3s ease',
                  }}
                />
              </Box>
            }
          />
        </Tabs>
      </Box>
    </Fade>
  );
});