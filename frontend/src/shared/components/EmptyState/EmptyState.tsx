import React from 'react';
import { Box, Typography } from '@mui/material';
import { Inbox as InboxIcon } from '@mui/icons-material';

interface EmptyStateProps {
  message?: string;
  icon?: React.ReactNode;
  action?: React.ReactNode;
}

export const EmptyState: React.FC<EmptyStateProps> = React.memo(({
  message = 'No data available',
  icon = <InboxIcon />,
  action,
}) => {
  return (
    <Box
      display="flex"
      flexDirection="column"
      alignItems="center"
      justifyContent="center"
      py={8}
      px={2}
    >
      <Box
        sx={{
          color: 'text.secondary',
          fontSize: 64,
          mb: 2,
          opacity: 0.6,
          animation: 'float 3s ease-in-out infinite',
          '@keyframes float': {
            '0%, 100%': {
              transform: 'translateY(0)',
            },
            '50%': {
              transform: 'translateY(-10px)',
            },
          },
        }}
      >
        {icon}
      </Box>
      <Typography variant="h6" color="text.secondary" gutterBottom>
        {message}
      </Typography>
      {action && <Box mt={2}>{action}</Box>}
    </Box>
  );
});