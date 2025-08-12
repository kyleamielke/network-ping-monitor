import React from 'react';
import { Box, CircularProgress, Alert, Fade } from '@mui/material';
import { EmptyState } from '@/shared/components/EmptyState';

interface AsyncContentProps {
  loading: boolean;
  error?: Error | string | null;
  empty?: boolean;
  emptyMessage?: string;
  emptyIcon?: React.ReactNode;
  children: React.ReactNode;
  loadingSize?: number;
  minHeight?: string | number;
}

export const AsyncContent: React.FC<AsyncContentProps> = ({
  loading,
  error,
  empty = false,
  emptyMessage = 'No data found',
  emptyIcon,
  children,
  loadingSize = 60,
  minHeight = 400,
}) => {
  if (loading) {
    return (
      <Fade in={true} timeout={300}>
        <Box
          display="flex"
          justifyContent="center"
          alignItems="center"
          minHeight={minHeight}
        >
          <CircularProgress size={loadingSize} />
        </Box>
      </Fade>
    );
  }

  if (error) {
    const errorMessage = typeof error === 'string' ? error : error.message;
    return (
      <Fade in={true} timeout={300}>
        <Box p={2}>
          <Alert severity="error">
            {errorMessage || 'An unexpected error occurred'}
          </Alert>
        </Box>
      </Fade>
    );
  }

  if (empty) {
    return (
      <Fade in={true} timeout={500}>
        <div>
          <EmptyState message={emptyMessage} icon={emptyIcon} />
        </div>
      </Fade>
    );
  }

  return (
    <Fade in={true} timeout={500}>
      <div>{children}</div>
    </Fade>
  );
};