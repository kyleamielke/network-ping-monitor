import React from 'react';
import { Card, CardContent, Typography, Box, CardActionArea } from '@mui/material';

interface StatCardProps {
  title: string;
  value: string | number;
  subtitle?: string;
  color?: 'primary' | 'secondary' | 'success' | 'warning' | 'error';
  icon?: React.ReactNode;
  onClick?: () => void;
  active?: boolean;
}

export const StatCard: React.FC<StatCardProps> = React.memo(({
  title,
  value,
  subtitle,
  color = 'primary',
  icon,
  onClick,
  active = false,
}) => {
  const cardSx = {
    cursor: onClick ? 'pointer' : 'default',
    transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
    border: active ? 2 : 0,
    borderColor: active ? `${color}.main` : 'transparent',
    transform: active ? 'translateY(-4px)' : 'none',
    boxShadow: active ? 6 : 2,
    backgroundColor: active ? `${color}.main` : 'background.paper',
    '& .MuiCardContent-root': {
      transition: 'background-color 0.3s ease',
    },
    '&:hover': onClick ? {
      transform: 'translateY(-4px)',
      boxShadow: 6,
      borderColor: `${color}.light`,
      '& .stat-icon': {
        transform: 'scale(1.1) rotate(5deg)',
      }
    } : {},
    '&:active': onClick ? {
      transform: 'translateY(-2px)',
      boxShadow: 3,
    } : {},
  };

  const content = (
    <CardContent>
      <Box display="flex" alignItems="center" justifyContent="space-between">
        <Box>
          <Typography 
            color={active ? 'primary.contrastText' : 'textSecondary'} 
            gutterBottom 
            variant="body2"
            sx={{ transition: 'color 0.3s ease' }}
          >
            {title}
          </Typography>
          <Typography 
            variant="h4" 
            color={active ? 'primary.contrastText' : color}
            sx={{ transition: 'color 0.3s ease', fontWeight: 600 }}
          >
            {value}
          </Typography>
          {subtitle && (
            <Typography variant="body2" color="textSecondary">
              {subtitle}
            </Typography>
          )}
        </Box>
        {icon && (
          <Box 
            className="stat-icon"
            color={active ? 'primary.contrastText' : `${color}.main`}
            sx={{ 
              transition: 'all 0.3s ease',
              fontSize: '2.5rem',
            }}
          >
            {icon}
          </Box>
        )}
      </Box>
    </CardContent>
  );

  return (
    <Card sx={cardSx}>
      {onClick ? (
        <CardActionArea onClick={onClick}>
          {content}
        </CardActionArea>
      ) : (
        content
      )}
    </Card>
  );
});