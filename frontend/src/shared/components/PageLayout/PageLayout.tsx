import React from 'react';
import { Box, Typography, IconButton, Tooltip, Fade, Zoom } from '@mui/material';
import { Refresh as RefreshIcon } from '@mui/icons-material';
import { Breadcrumbs } from '@/shared/components/Breadcrumbs';

interface PageLayoutProps {
  title: string | React.ReactNode;
  subtitle?: string;
  actions?: React.ReactNode;
  onRefresh?: () => void;
  children: React.ReactNode;
  breadcrumbs?: boolean;
}

export const PageLayout: React.FC<PageLayoutProps> = ({
  title,
  subtitle,
  actions,
  onRefresh,
  children,
  breadcrumbs = true,
}) => {
  return (
    <Fade in={true} timeout={500}>
      <Box>
        {breadcrumbs && <Breadcrumbs />}
        <Box
        display="flex"
        justifyContent="space-between"
        alignItems="center"
        mb={3}
      >
        <Box>
          {typeof title === 'string' ? (
            <Typography variant="h4" component="h1">
              {title}
            </Typography>
          ) : (
            title
          )}
          {subtitle && (
            <Typography variant="body2" color="text.secondary" mt={0.5}>
              {subtitle}
            </Typography>
          )}
        </Box>
        <Box display="flex" gap={2} alignItems="center">
          {onRefresh && (
            <Zoom in={true} timeout={600}>
              <Tooltip title="Refresh">
                <IconButton 
                  onClick={onRefresh} 
                  size="small"
                  sx={{
                    transition: 'all 0.3s ease',
                    '&:hover': {
                      transform: 'rotate(180deg)',
                      color: 'primary.main',
                    }
                  }}
                >
                  <RefreshIcon />
                </IconButton>
              </Tooltip>
            </Zoom>
          )}
          {actions}
        </Box>
      </Box>
        {children}
      </Box>
    </Fade>
  );
};