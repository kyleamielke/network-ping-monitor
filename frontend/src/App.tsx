import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { ApolloProvider } from '@apollo/client';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { apolloClient } from '@/shared/services/apollo-client';
import { Layout } from '@/shared/components/Layout';
import { DevicesPage } from '@/features/devices';
import { DashboardPage } from '@/features/dashboard';
import { AlertsPage } from '@/features/alerts';
import { DeviceDetailPage } from '@/features/device-detail';
import { AlertDetailPage } from '@/features/alert-detail/components/AlertDetailPage';
import { ReportsPage } from '@/features/reports';
import { SettingsPage } from '@/features/settings';
import { ToastProvider } from '@/shared/contexts/ToastContext';
import { ThemeProvider } from '@/shared/contexts/ThemeContext';
import { LocaleProvider } from '@/shared/contexts/LocaleContext';
import { ErrorBoundary } from '@/shared/components/ErrorBoundary';

const App: React.FC = () => {
  return (
    <ApolloProvider client={apolloClient}>
      <ThemeProvider>
        <LocaleProvider>
          <LocalizationProvider dateAdapter={AdapterDateFns}>
            <Router>
              <ToastProvider>
                <Layout>
                <ErrorBoundary>
                  <Routes>
                    <Route path="/" element={<Navigate to="/dashboard" replace />} />
                    <Route 
                      path="/dashboard" 
                      element={
                        <ErrorBoundary>
                          <DashboardPage />
                        </ErrorBoundary>
                      } 
                    />
                    <Route 
                      path="/devices" 
                      element={
                        <ErrorBoundary>
                          <DevicesPage />
                        </ErrorBoundary>
                      } 
                    />
                    <Route 
                      path="/devices/:deviceId" 
                      element={
                        <ErrorBoundary>
                          <DeviceDetailPage />
                        </ErrorBoundary>
                      } 
                    />
                    <Route 
                      path="/alerts" 
                      element={
                        <ErrorBoundary>
                          <AlertsPage />
                        </ErrorBoundary>
                      } 
                    />
                    <Route 
                      path="/alerts/:alertId" 
                      element={
                        <ErrorBoundary>
                          <AlertDetailPage />
                        </ErrorBoundary>
                      } 
                    />
                    <Route 
                      path="/reports" 
                      element={
                        <ErrorBoundary>
                          <ReportsPage />
                        </ErrorBoundary>
                      } 
                    />
                    <Route 
                      path="/settings" 
                      element={
                        <ErrorBoundary>
                          <SettingsPage />
                        </ErrorBoundary>
                      } 
                    />
                  </Routes>
                </ErrorBoundary>
              </Layout>
            </ToastProvider>
          </Router>
        </LocalizationProvider>
      </LocaleProvider>
      </ThemeProvider>
    </ApolloProvider>
  );
};

export default App;