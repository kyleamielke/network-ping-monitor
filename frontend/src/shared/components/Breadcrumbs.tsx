import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { Breadcrumbs as MuiBreadcrumbs, Typography, Link as MuiLink, Box } from '@mui/material';
import { NavigateNext as NavigateNextIcon } from '@mui/icons-material';
import { BreadcrumbItem } from '@/shared/types/navigation.types';

interface BreadcrumbsProps {
  items?: BreadcrumbItem[];
  current?: string;
}

export const Breadcrumbs: React.FC<BreadcrumbsProps> = ({ items, current }) => {
  const location = useLocation();
  
  // Auto-generate breadcrumbs from path if items not provided
  const generateBreadcrumbs = (): BreadcrumbItem[] => {
    if (items) return items;
    
    const pathSegments = location.pathname.split('/').filter(Boolean);
    const breadcrumbs: BreadcrumbItem[] = [
      { label: 'Dashboard', path: '/dashboard' }
    ];
    
    let currentPath = '';
    pathSegments.forEach((segment, index) => {
      currentPath += `/${segment}`;
      
      // Skip adding dashboard again
      if (segment === 'dashboard') return;
      
      // Format segment for display
      let label = segment.charAt(0).toUpperCase() + segment.slice(1);
      label = label.replace(/-/g, ' ');
      
      // Don't add path for the last segment (current page)
      if (index === pathSegments.length - 1) {
        breadcrumbs.push({ label: current || label });
      } else {
        breadcrumbs.push({ label, path: currentPath });
      }
    });
    
    return breadcrumbs;
  };
  
  const breadcrumbItems = generateBreadcrumbs();
  
  // Don't show breadcrumbs on dashboard
  if (location.pathname === '/dashboard' || breadcrumbItems.length <= 1) {
    return null;
  }
  
  return (
    <Box sx={{ mb: 2 }}>
      <MuiBreadcrumbs
        separator={<NavigateNextIcon fontSize="small" />}
        aria-label="breadcrumb"
        sx={{
          '& .MuiBreadcrumbs-separator': {
            mx: 0.5,
            color: 'text.secondary',
          },
        }}
      >
        {breadcrumbItems.map((item, index) => {
          const isLast = index === breadcrumbItems.length - 1;
          
          if (isLast || !item.path) {
            return (
              <Typography
                key={index}
                color="text.primary"
                variant="body2"
                sx={{ 
                  fontWeight: isLast ? 500 : 400,
                  maxWidth: 200,
                  overflow: 'hidden',
                  textOverflow: 'ellipsis',
                  whiteSpace: 'nowrap',
                }}
              >
                {item.label}
              </Typography>
            );
          }
          
          return (
            <MuiLink
              key={index}
              component={Link}
              to={item.path}
              color="inherit"
              variant="body2"
              underline="hover"
              sx={{
                transition: 'color 0.2s',
                '&:hover': {
                  color: 'primary.main',
                },
              }}
            >
              {item.label}
            </MuiLink>
          );
        })}
      </MuiBreadcrumbs>
    </Box>
  );
};