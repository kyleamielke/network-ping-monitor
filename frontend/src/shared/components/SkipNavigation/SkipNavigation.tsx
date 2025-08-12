import React from 'react';
import { Box, Link } from '@mui/material';
import { useLocale } from '@/shared/contexts/LocaleContext';

export const SkipNavigation: React.FC = () => {
  const { t } = useLocale();
  
  return (
    <Box
      sx={{
        position: 'absolute',
        left: '-9999px',
        top: '10px',
        zIndex: 9999,
        '&:focus-within': {
          left: '10px',
        },
      }}
    >
      <Link
        href="#main-content"
        sx={{
          backgroundColor: 'primary.main',
          color: 'primary.contrastText',
          padding: 2,
          textDecoration: 'none',
          borderRadius: 1,
          '&:hover': {
            backgroundColor: 'primary.dark',
          },
        }}
      >
        {t('navigation.skipToMain')}
      </Link>
    </Box>
  );
};