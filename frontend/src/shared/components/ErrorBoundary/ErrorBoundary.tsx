import React, { Component, ErrorInfo, ReactNode } from 'react';
import {
  Box,
  Typography,
  Button,
  Card,
  CardContent,
  Alert,
  Accordion,
  AccordionSummary,
  AccordionDetails,
} from '@mui/material';
import {
  Error as ErrorIcon,
  Refresh as RefreshIcon,
  ExpandMore as ExpandMoreIcon,
} from '@mui/icons-material';

interface ErrorBoundaryState {
  hasError: boolean;
  error: Error | null;
  errorInfo: ErrorInfo | null;
}

interface ErrorBoundaryProps {
  children: ReactNode;
  fallback?: ReactNode;
  onError?: (error: Error, errorInfo: ErrorInfo) => void;
}

export class ErrorBoundary extends Component<ErrorBoundaryProps, ErrorBoundaryState> {
  constructor(props: ErrorBoundaryProps) {
    super(props);
    this.state = {
      hasError: false,
      error: null,
      errorInfo: null,
    };
  }

  static getDerivedStateFromError(error: Error): Partial<ErrorBoundaryState> {
    return {
      hasError: true,
      error,
    };
  }

  componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    console.error('ErrorBoundary caught an error:', error, errorInfo);
    
    this.setState({
      error,
      errorInfo,
    });

    // Call the optional error callback
    if (this.props.onError) {
      this.props.onError(error, errorInfo);
    }
  }

  handleReset = () => {
    this.setState({
      hasError: false,
      error: null,
      errorInfo: null,
    });
  };

  handleReload = () => {
    window.location.reload();
  };

  render() {
    if (this.state.hasError) {
      // Custom fallback UI
      if (this.props.fallback) {
        return this.props.fallback;
      }

      // Default error UI
      return (
        <Box
          display="flex"
          flexDirection="column"
          alignItems="center"
          justifyContent="center"
          minHeight="400px"
          p={3}
        >
          <Card sx={{ maxWidth: 600, width: '100%' }}>
            <CardContent>
              <Box display="flex" alignItems="center" mb={2}>
                <ErrorIcon color="error" sx={{ fontSize: 40, mr: 2 }} />
                <Typography variant="h5" color="error">
                  Something went wrong
                </Typography>
              </Box>
              
              <Alert severity="error" sx={{ mb: 2 }}>
                An unexpected error occurred. The error has been logged and will be investigated.
              </Alert>

              <Box display="flex" gap={2} mb={3}>
                <Button
                  variant="contained"
                  startIcon={<RefreshIcon />}
                  onClick={this.handleReset}
                >
                  Try Again
                </Button>
                <Button
                  variant="outlined"
                  onClick={this.handleReload}
                >
                  Reload Page
                </Button>
              </Box>

              {/* Error details for development */}
              {process.env.NODE_ENV === 'development' && this.state.error && (
                <Accordion>
                  <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                    <Typography variant="subtitle2">Error Details (Development)</Typography>
                  </AccordionSummary>
                  <AccordionDetails>
                    <Box>
                      <Typography variant="body2" gutterBottom>
                        <strong>Error:</strong> {this.state.error.name}
                      </Typography>
                      <Typography variant="body2" gutterBottom>
                        <strong>Message:</strong> {this.state.error.message}
                      </Typography>
                      {this.state.error.stack && (
                        <Box mt={2}>
                          <Typography variant="body2" gutterBottom>
                            <strong>Stack Trace:</strong>
                          </Typography>
                          <Typography
                            variant="body2"
                            component="pre"
                            sx={{
                              backgroundColor: 'grey.100',
                              p: 1,
                              borderRadius: 1,
                              fontSize: '0.75rem',
                              overflow: 'auto',
                              maxHeight: 200,
                            }}
                          >
                            {this.state.error.stack}
                          </Typography>
                        </Box>
                      )}
                      {this.state.errorInfo && (
                        <Box mt={2}>
                          <Typography variant="body2" gutterBottom>
                            <strong>Component Stack:</strong>
                          </Typography>
                          <Typography
                            variant="body2"
                            component="pre"
                            sx={{
                              backgroundColor: 'grey.100',
                              p: 1,
                              borderRadius: 1,
                              fontSize: '0.75rem',
                              overflow: 'auto',
                              maxHeight: 200,
                            }}
                          >
                            {this.state.errorInfo.componentStack}
                          </Typography>
                        </Box>
                      )}
                    </Box>
                  </AccordionDetails>
                </Accordion>
              )}
            </CardContent>
          </Card>
        </Box>
      );
    }

    return this.props.children;
  }
}

// Higher-order component for easy wrapping
export function withErrorBoundary<P extends object>(
  Component: React.ComponentType<P>,
  errorBoundaryProps?: Omit<ErrorBoundaryProps, 'children'>
) {
  const WrappedComponent = (props: P) => (
    <ErrorBoundary {...errorBoundaryProps}>
      <Component {...props} />
    </ErrorBoundary>
  );

  WrappedComponent.displayName = `withErrorBoundary(${Component.displayName || Component.name})`;
  
  return WrappedComponent;
}