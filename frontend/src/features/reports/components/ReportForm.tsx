import React, { useState } from 'react';
import {
  Card,
  CardContent,
  Typography,
  Box,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  TextField,
  Button,
  CircularProgress,
  Alert,
  SelectChangeEvent,
  Grow,
  Fade,
} from '@mui/material';
import { FileDownload } from '@mui/icons-material';
import { ReportType, ReportFormat, REPORT_TYPE_LABELS } from '@/features/reports/types/report.types';

interface ReportFormProps {
  onGenerate: (params: {
    reportType: ReportType;
    format: ReportFormat;
    title?: string;
    startDate?: string;
    endDate?: string;
  }) => void;
  loading: boolean;
  error: Error | null;
}

export const ReportForm: React.FC<ReportFormProps> = ({ onGenerate, loading, error }) => {
  const [reportType, setReportType] = useState<ReportType>('DEVICE_UPTIME');
  const [format, setFormat] = useState<ReportFormat>('PDF');
  const [title, setTitle] = useState('');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');

  const handleSubmit = () => {
    onGenerate({
      reportType,
      format,
      title: title || undefined,
      startDate: startDate || undefined,
      endDate: endDate || undefined,
    });
  };

  return (
    <Card sx={{ 
      transition: 'all 0.3s ease',
      '&:hover': {
        boxShadow: 4,
      }
    }}>
      <CardContent>
        <Typography variant="h6" gutterBottom>
          Generate Report
        </Typography>

        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 2 }}>
          <Grow in={true} timeout={600}>
            <FormControl fullWidth>
            <InputLabel>Report Type</InputLabel>
            <Select
              value={reportType}
              label="Report Type"
              onChange={(e: SelectChangeEvent) => setReportType(e.target.value as ReportType)}
            >
              {Object.entries(REPORT_TYPE_LABELS).map(([value, label]) => (
                <MenuItem key={value} value={value}>{label}</MenuItem>
              ))}
            </Select>
            </FormControl>
          </Grow>

          <Grow in={true} timeout={800}>
            <FormControl fullWidth>
            <InputLabel>Format</InputLabel>
            <Select
              value={format}
              label="Format"
              onChange={(e: SelectChangeEvent) => setFormat(e.target.value as ReportFormat)}
            >
              <MenuItem value="PDF">PDF</MenuItem>
              <MenuItem value="CSV">CSV</MenuItem>
              <MenuItem value="EXCEL">Excel</MenuItem>
            </Select>
            </FormControl>
          </Grow>

          <Grow in={true} timeout={1000}>
            <TextField
            fullWidth
            label="Report Title (Optional)"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="Custom report title"
            />
          </Grow>

          <Grow in={true} timeout={1200}>
            <TextField
            fullWidth
            label="Start Date (Optional)"
            type="datetime-local"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
            InputLabelProps={{ shrink: true }}
            />
          </Grow>

          <Grow in={true} timeout={1400}>
            <TextField
            fullWidth
            label="End Date (Optional)"
            type="datetime-local"
            value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
            InputLabelProps={{ shrink: true }}
            />
          </Grow>

          <Grow in={true} timeout={1600}>
            <Button
            variant="contained"
            size="large"
            onClick={handleSubmit}
            disabled={loading}
            startIcon={loading ? <CircularProgress size={20} /> : <FileDownload />}
            sx={{
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
            {loading ? 'Generating...' : 'Generate Report'}
            </Button>
          </Grow>
        </Box>

        <Fade in={!!error} timeout={300}>
          <Box>
            {error && (
              <Alert severity="error" sx={{ mt: 2 }}>
                Failed to generate report: {error.message}
              </Alert>
            )}
          </Box>
        </Fade>
      </CardContent>
    </Card>
  );
};