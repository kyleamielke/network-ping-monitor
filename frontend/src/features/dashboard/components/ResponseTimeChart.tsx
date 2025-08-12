import React from 'react';
import {
  Card,
  CardContent,
  CardHeader,
  Box,
  Typography,
  IconButton,
  Tooltip,
} from '@mui/material';
import { Close as CloseIcon } from '@mui/icons-material';
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip as RechartsTooltip,
  ResponsiveContainer,
} from 'recharts';
import { ErrorBoundary } from '@/shared/components/ErrorBoundary';
import { useLocale } from '@/shared/contexts/LocaleContext';

interface ResponseTimeData {
  time: string;
  responseTime: number;
  deviceName?: string;
}

interface ResponseTimeChartProps {
  data: ResponseTimeData[];
  title?: string;
  height?: number;
  onClearSelection?: () => void;
  showClearButton?: boolean;
}

export const ResponseTimeChart: React.FC<ResponseTimeChartProps> = React.memo(({
  data,
  title = 'Response Time Trends',
  height = 300,
  onClearSelection,
  showClearButton = false,
}) => {
  const { formatTime, formatDate } = useLocale();
  if (data.length === 0) {
    return (
      <Card elevation={2}>
        <CardHeader 
          title={title}
          action={
            showClearButton && onClearSelection ? (
              <Tooltip title="Show all devices">
                <IconButton onClick={onClearSelection} size="small">
                  <CloseIcon />
                </IconButton>
              </Tooltip>
            ) : null
          }
        />
        <CardContent>
          <Box 
            height={height} 
            display="flex" 
            alignItems="center" 
            justifyContent="center"
          >
            <Typography color="textSecondary">
              No response time data available
            </Typography>
          </Box>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card elevation={2}>
      <CardHeader 
        title={title}
        action={
          showClearButton && onClearSelection ? (
            <Tooltip title="Show all devices">
              <IconButton onClick={onClearSelection} size="small">
                <CloseIcon />
              </IconButton>
            </Tooltip>
          ) : null
        }
      />
      <CardContent>
        <Box height={height}>
          <ErrorBoundary
            fallback={
              <Box 
                height={height} 
                display="flex" 
                alignItems="center" 
                justifyContent="center"
              >
                <Typography color="error">
                  Error loading chart
                </Typography>
              </Box>
            }
          >
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={data} margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis 
                  dataKey="time" 
                  tick={{ fontSize: 12 }}
                  tickFormatter={(value) => formatTime(value)}
                />
                <YAxis 
                  tick={{ fontSize: 12 }}
                  label={{ value: 'Response Time (ms)', angle: -90, position: 'insideLeft' }}
                />
                <RechartsTooltip 
                  labelFormatter={(value: any) => formatDate(value, true)}
                  formatter={(value: number) => [`${value}ms`, 'Response Time']}
                />
                <Line 
                  type="monotone" 
                  dataKey="responseTime" 
                  stroke="#1976d2" 
                  strokeWidth={2}
                  dot={{ r: 3 }}
                  activeDot={{ r: 5 }}
                />
              </LineChart>
            </ResponsiveContainer>
          </ErrorBoundary>
        </Box>
      </CardContent>
    </Card>
  );
});