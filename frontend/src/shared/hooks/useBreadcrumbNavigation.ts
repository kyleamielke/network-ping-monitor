import { useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import { BreadcrumbItem } from '@/shared/types/navigation.types';

const BREADCRUMB_STORAGE_KEY = 'navigation-breadcrumbs';

export const useBreadcrumbNavigation = () => {
  const location = useLocation();
  
  // Save breadcrumbs to sessionStorage when they change
  useEffect(() => {
    if (location.state?.breadcrumbs) {
      const key = `${BREADCRUMB_STORAGE_KEY}-${location.pathname}`;
      sessionStorage.setItem(key, JSON.stringify(location.state.breadcrumbs));
    }
  }, [location]);
  
  const getBreadcrumbs = (defaultBreadcrumbs: BreadcrumbItem[]): BreadcrumbItem[] => {
    // First check if we have breadcrumbs in navigation state
    if (location.state?.breadcrumbs) {
      return location.state.breadcrumbs;
    }
    
    // Then check sessionStorage for this path
    const key = `${BREADCRUMB_STORAGE_KEY}-${location.pathname}`;
    const stored = sessionStorage.getItem(key);
    if (stored) {
      try {
        return JSON.parse(stored);
      } catch {
        // Invalid JSON, ignore
      }
    }
    
    // Fall back to default breadcrumbs
    return defaultBreadcrumbs;
  };
  
  const clearBreadcrumbs = () => {
    // Clear all breadcrumb entries from sessionStorage
    Object.keys(sessionStorage)
      .filter(key => key.startsWith(BREADCRUMB_STORAGE_KEY))
      .forEach(key => sessionStorage.removeItem(key));
  };
  
  return {
    getBreadcrumbs,
    clearBreadcrumbs,
    navigationState: location.state
  };
};