import React from 'react';
import { Card, CardContent, Typography, Box, Fade } from '@mui/material';
import { REPORT_TYPE_LABELS, REPORT_TYPE_DESCRIPTIONS } from '@/features/reports/types/report.types';

export const ReportTypeInfo: React.FC = () => {
  return (
    <Fade in={true} timeout={1000}>
      <Card sx={{ 
        transition: 'all 0.3s ease',
        '&:hover': {
          boxShadow: 3,
        }
      }}>
        <CardContent>
        <Typography variant="h6" gutterBottom>
          Report Types
        </Typography>
        <Box sx={{ mt: 1 }}>
          {Object.entries(REPORT_TYPE_LABELS).map(([type, label], index) => (
            <Fade key={type} in={true} timeout={1200 + index * 200}>
              <Typography variant="body2" paragraph>
                <strong>{label}:</strong> {REPORT_TYPE_DESCRIPTIONS[type as keyof typeof REPORT_TYPE_DESCRIPTIONS]}
              </Typography>
            </Fade>
          ))}
        </Box>
        </CardContent>
      </Card>
    </Fade>
  );
};