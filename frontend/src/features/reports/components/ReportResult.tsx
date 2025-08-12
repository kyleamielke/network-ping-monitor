import React from 'react';
import { Card, CardContent, Typography, Box, Button, Grow, Zoom } from '@mui/material';
import { Download } from '@mui/icons-material';
import { ReportResponse, REPORT_TYPE_LABELS } from '@/features/reports/types/report.types';
import { useLocale } from '@/shared/contexts/LocaleContext';

interface ReportResultProps {
  report: ReportResponse;
}

const formatFileSize = (bytes: number): string => {
  if (bytes < 1024) return bytes + ' B';
  if (bytes < 1048576) return (bytes / 1024).toFixed(1) + ' KB';
  return (bytes / 1048576).toFixed(1) + ' MB';
};

export const ReportResult: React.FC<ReportResultProps> = ({ report }) => {
  const { formatDate } = useLocale();
  
  return (
    <Zoom in={true} timeout={500}>
      <Card sx={{ 
        transition: 'all 0.3s ease',
        '&:hover': {
          boxShadow: 4,
          transform: 'scale(1.02)',
        }
      }}>
        <CardContent>
        <Typography variant="h6" gutterBottom>
          Generated Report
        </Typography>

        <Box sx={{ mt: 2 }}>
          <Typography variant="body2" color="textSecondary" gutterBottom>
            Report ID: {report.reportId}
          </Typography>
          <Typography variant="body1" gutterBottom>
            <strong>Filename:</strong> {report.filename}
          </Typography>
          <Typography variant="body1" gutterBottom>
            <strong>Type:</strong> {REPORT_TYPE_LABELS[report.reportType]}
          </Typography>
          <Typography variant="body1" gutterBottom>
            <strong>Format:</strong> {report.format}
          </Typography>
          <Typography variant="body1" gutterBottom>
            <strong>Size:</strong> {formatFileSize(report.fileSizeBytes)}
          </Typography>
          <Typography variant="body1" gutterBottom>
            <strong>Generated:</strong> {formatDate(report.generatedAt, true)}
          </Typography>

          <Grow in={true} timeout={800}>
            <Button
              variant="contained"
              color="primary"
              startIcon={<Download />}
              component="a"
              href={report.downloadUrl}
              download={report.filename}
              sx={{ 
                mt: 2,
                transition: 'all 0.3s ease',
                '&:hover': {
                  transform: 'translateY(-2px)',
                  boxShadow: 4,
                },
                '&:active': {
                  transform: 'translateY(0)',
                }
              }}
            >
              Download Report
            </Button>
          </Grow>
        </Box>
        </CardContent>
      </Card>
    </Zoom>
  );
};