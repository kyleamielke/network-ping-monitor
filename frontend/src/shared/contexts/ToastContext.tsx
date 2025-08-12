import React, { createContext, useContext, useState, useCallback, ReactNode } from 'react';
import { Snackbar, Alert, AlertColor, Button } from '@mui/material';
import { useNavigate } from 'react-router-dom';

export interface Toast {
  id: string;
  message: string;
  severity: AlertColor;
  duration?: number;
  action?: {
    label: string;
    onClick: () => void;
  };
  clickable?: boolean;
  onClick?: () => void;
}

interface ToastContextType {
  showToast: (message: string, severity?: AlertColor, duration?: number, options?: Partial<Toast>) => void;
  showSuccess: (message: string) => void;
  showError: (message: string) => void;
  showWarning: (message: string) => void;
  showInfo: (message: string) => void;
  showAlertToast: (message: string, alertId: string, severity?: AlertColor) => void;
}

const ToastContext = createContext<ToastContextType | undefined>(undefined);

export const useToast = () => {
  const context = useContext(ToastContext);
  if (!context) {
    throw new Error('useToast must be used within a ToastProvider');
  }
  return context;
};

interface ToastProviderProps {
  children: ReactNode;
}

export const ToastProvider: React.FC<ToastProviderProps> = ({ children }) => {
  const navigate = useNavigate();
  const [, setToasts] = useState<Toast[]>([]);
  const [currentToast, setCurrentToast] = useState<Toast | null>(null);

  const showToast = useCallback((message: string, severity: AlertColor = 'info', duration: number = 5000, options?: Partial<Toast>) => {
    const id = Date.now().toString();
    const newToast: Toast = { id, message, severity, duration, ...options };
    
    setToasts(prev => [...prev, newToast]);
    
    // If no toast is currently showing, show this one immediately
    if (!currentToast) {
      setCurrentToast(newToast);
    }
  }, [currentToast]);

  const showSuccess = useCallback((message: string) => {
    showToast(message, 'success');
  }, [showToast]);

  const showError = useCallback((message: string) => {
    showToast(message, 'error');
  }, [showToast]);

  const showWarning = useCallback((message: string) => {
    showToast(message, 'warning');
  }, [showToast]);

  const showInfo = useCallback((message: string) => {
    showToast(message, 'info');
  }, [showToast]);

  const showAlertToast = useCallback((message: string, alertId: string, severity: AlertColor = 'error') => {
    showToast(message, severity, 10000, {
      clickable: true,
      onClick: () => {
        navigate(`/alerts/${alertId}`);
        handleClose();
      },
      action: {
        label: 'View Alert',
        onClick: () => {
          navigate(`/alerts/${alertId}`);
          handleClose();
        }
      }
    });
  }, [showToast, navigate]);

  const handleClose = useCallback(() => {
    setCurrentToast(null);
    
    // After closing, check if there are more toasts in the queue
    setTimeout(() => {
      setToasts(prev => {
        const remaining = prev.slice(1);
        if (remaining.length > 0) {
          setCurrentToast(remaining[0]);
        }
        return remaining;
      });
    }, 100);
  }, []);

  return (
    <ToastContext.Provider value={{ showToast, showSuccess, showError, showWarning, showInfo, showAlertToast }}>
      {children}
      {currentToast && (
        <Snackbar
          open={true}
          autoHideDuration={currentToast.duration}
          onClose={handleClose}
          anchorOrigin={{ vertical: 'top', horizontal: 'right' }}
        >
          <Alert 
            onClose={handleClose} 
            severity={currentToast.severity} 
            sx={{ 
              width: '100%',
              cursor: currentToast.clickable ? 'pointer' : 'default',
              '&:hover': currentToast.clickable ? {
                opacity: 0.9,
              } : {}
            }}
            variant="filled"
            onClick={currentToast.clickable ? currentToast.onClick : undefined}
            action={currentToast.action ? (
              <Button 
                color="inherit" 
                size="small" 
                onClick={(e) => {
                  e.stopPropagation();
                  currentToast.action?.onClick();
                }}
              >
                {currentToast.action.label}
              </Button>
            ) : undefined}
          >
            {currentToast.message}
          </Alert>
        </Snackbar>
      )}
    </ToastContext.Provider>
  );
};