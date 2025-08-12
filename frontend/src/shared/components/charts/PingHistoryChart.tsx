import React, { useState, useCallback, useMemo } from 'react';
import {
  Card,
  CardContent,
  CardHeader,
  Box,
  Typography,
  IconButton,
  Tooltip,
  FormControl,
  Select,
  MenuItem,
  SelectChangeEvent,
  Chip,
} from '@mui/material';
import { Close as CloseIcon, Refresh as RefreshIcon, ZoomOutMap as ZoomOutIcon } from '@mui/icons-material';
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip as RechartsTooltip,
  ResponsiveContainer,
  ReferenceLine,
  Brush,
} from 'recharts';
import { ErrorBoundary } from '@/shared/components/ErrorBoundary';
import { usePingHistoryByTime, TimeRangeInput } from '@/shared/hooks/monitoring/usePingHistory';
import { useLocale } from '@/shared/contexts/LocaleContext';

interface PingHistoryChartProps {
  deviceId: string | null;
  deviceName?: string;
  title?: string;
  height?: number;
  onClearSelection?: () => void;
  showClearButton?: boolean;
}

interface TimeRangeOption {
  label: string;
  value: TimeRangeInput;
  minutes: number; // for sorting and comparison
}

const TIME_RANGE_OPTIONS: TimeRangeOption[] = [
  { label: '5 minutes', value: { minutes: 5 }, minutes: 5 },
  { label: '15 minutes', value: { minutes: 15 }, minutes: 15 },
  { label: '30 minutes', value: { minutes: 30 }, minutes: 30 },
  { label: '1 hour', value: { hours: 1 }, minutes: 60 },
  { label: '2 hours', value: { hours: 2 }, minutes: 120 },
  { label: '6 hours', value: { hours: 6 }, minutes: 360 },
  { label: '12 hours', value: { hours: 12 }, minutes: 720 },
  { label: '24 hours', value: { hours: 24 }, minutes: 1440 },
  { label: '7 days', value: { days: 7 }, minutes: 10080 },
];

export const PingHistoryChart: React.FC<PingHistoryChartProps> = React.memo(({
  deviceId,
  deviceName,
  title,
  height = 400,
  onClearSelection,
  showClearButton = false,
}) => {
  const { formatTime, formatDate } = useLocale();
  const [selectedTimeRange, setSelectedTimeRange] = useState<TimeRangeInput>({ minutes: 30 });
  const [brushDomain, setBrushDomain] = useState<[number, number] | null>(null);
  
  const { pingHistory, loading, error, refetch } = usePingHistoryByTime(deviceId, selectedTimeRange);

  const handleTimeRangeChange = useCallback((event: SelectChangeEvent<string>) => {
    const optionIndex = parseInt(event.target.value);
    const option = TIME_RANGE_OPTIONS[optionIndex];
    if (option) {
      setSelectedTimeRange(option.value);
    }
  }, []);

  const handleRefresh = useCallback(() => {
    refetch();
  }, [refetch]);

  // Process chart data
  const chartData = useMemo(() => {
    if (!pingHistory?.results) return [];

    return pingHistory.results
      .slice() // Create a copy to avoid mutating original array
      .sort((a, b) => new Date(a.timestamp).getTime() - new Date(b.timestamp).getTime()) // Sort chronologically
      .map((result, index) => ({
        index,
        timestamp: result.timestamp,
        time: new Date(result.timestamp).getTime(),
        responseTime: result.success ? result.responseTimeMs || 0 : null,
        success: result.success,
        formattedTime: formatTime(result.timestamp),
        fullDateTime: formatDate(result.timestamp, true),
      }));
  }, [pingHistory?.results]);

  // Get current time range option for display
  const currentOption = TIME_RANGE_OPTIONS.find(option => 
    JSON.stringify(option.value) === JSON.stringify(selectedTimeRange)
  ) || TIME_RANGE_OPTIONS[2]; // Default to 30 minutes

  // Calculate statistics for display
  const stats = useMemo(() => {
    if (!pingHistory?.statistics) return null;
    
    const stats = pingHistory.statistics;
    return {
      totalPings: stats.totalPings,
      successRate: Math.round(stats.successRate * 100), // Convert to percentage
      avgResponseTime: stats.averageResponseTime ? Math.round(stats.averageResponseTime) : null,
      uptime: Math.round(stats.uptime),
    };
  }, [pingHistory?.statistics]);

  const chartTitle = title || 
    (deviceName ? `${deviceName} - Ping History` : 'Ping History');

  if (error) {
    return (
      <Card elevation={2}>
        <CardHeader 
          title={chartTitle}
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
            <Typography color="error">
              Error loading ping history: {error.message}
            </Typography>
          </Box>
        </CardContent>
      </Card>
    );
  }

  if (!deviceId) {
    return (
      <Card elevation={2}>
        <CardHeader 
          title={chartTitle}
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
              Select a device to view ping history
            </Typography>
          </Box>
        </CardContent>
      </Card>
    );
  }

  if (loading) {
    return (
      <Card elevation={2}>
        <CardHeader 
          title={chartTitle}
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
              Loading ping history...
            </Typography>
          </Box>
        </CardContent>
      </Card>
    );
  }

  if (chartData.length === 0) {
    return (
      <Card elevation={2}>
        <CardHeader 
          title={chartTitle}
          action={
            <Box display="flex" alignItems="center" gap={1}>
              <FormControl size="small" variant="outlined">
                <Select
                  value={TIME_RANGE_OPTIONS.indexOf(currentOption).toString()}
                  onChange={handleTimeRangeChange}
                  displayEmpty
                >
                  {TIME_RANGE_OPTIONS.map((option, index) => (
                    <MenuItem key={index} value={index.toString()}>
                      {option.label}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
              <Tooltip title="Refresh data">
                <IconButton onClick={handleRefresh} size="small">
                  <RefreshIcon />
                </IconButton>
              </Tooltip>
              {showClearButton && onClearSelection && (
                <Tooltip title="Show all devices">
                  <IconButton onClick={onClearSelection} size="small">
                    <CloseIcon />
                  </IconButton>
                </Tooltip>
              )}
            </Box>
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
              No ping data available for the selected time range
            </Typography>
          </Box>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card elevation={2}>
      <CardHeader 
        title={chartTitle}
        subheader={
          stats && (
            <Box display="flex" gap={1} mt={1} flexWrap="wrap">
              <Chip 
                label={`${stats.totalPings} pings`} 
                size="small" 
                variant="outlined" 
              />
              <Chip 
                label={`${stats.successRate}% success`} 
                size="small" 
                color={stats.successRate >= 95 ? 'success' : stats.successRate >= 80 ? 'warning' : 'error'}
                variant="outlined"
              />
              <Chip 
                label={`${stats.uptime}% uptime`} 
                size="small" 
                color={stats.uptime >= 95 ? 'success' : stats.uptime >= 80 ? 'warning' : 'error'}
                variant="outlined"
              />
              {stats.avgResponseTime && (
                <Chip 
                  label={`${stats.avgResponseTime}ms avg`} 
                  size="small" 
                  color={stats.avgResponseTime <= 50 ? 'success' : stats.avgResponseTime <= 120 ? 'warning' : 'error'}
                  variant="outlined"
                />
              )}
            </Box>
          )
        }
        action={
          <Box display="flex" alignItems="center" gap={1}>
            <FormControl size="small" variant="outlined">
              <Select
                value={TIME_RANGE_OPTIONS.indexOf(currentOption).toString()}
                onChange={handleTimeRangeChange}
                displayEmpty
              >
                {TIME_RANGE_OPTIONS.map((option, index) => (
                  <MenuItem key={index} value={index.toString()}>
                    {option.label}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
            <Tooltip title="Refresh data">
              <IconButton onClick={handleRefresh} size="small">
                <RefreshIcon />
              </IconButton>
            </Tooltip>
            {brushDomain && (
              <Tooltip title="Reset zoom">
                <IconButton onClick={() => setBrushDomain(null)} size="small">
                  <ZoomOutIcon />
                </IconButton>
              </Tooltip>
            )}
            {showClearButton && onClearSelection && (
              <Tooltip title="Show all devices">
                <IconButton onClick={onClearSelection} size="small">
                  <CloseIcon />
                </IconButton>
              </Tooltip>
            )}
          </Box>
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
                  Error rendering chart
                </Typography>
              </Box>
            }
          >
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={chartData} margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis 
                  dataKey="time"
                  type="number"
                  domain={['dataMin', 'dataMax']}
                  tick={{ fontSize: 12 }}
                  tickFormatter={(value) => formatTime(new Date(value))}
                  scale="time"
                />
                <YAxis 
                  tick={{ fontSize: 12 }}
                  label={{ value: 'Response Time (ms)', angle: -90, position: 'insideLeft' }}
                />
                <RechartsTooltip 
                  labelFormatter={(value: any, payload: any[]) => {
                    if (payload && payload.length > 0) {
                      return payload[0].payload.fullDateTime;
                    }
                    return formatDate(value, true);
                  }}
                  formatter={(value: any, name: string) => {
                    if (name === 'responseTime') {
                      return value !== null ? [`${value}ms`, 'Response Time'] : ['Failed', 'Ping Failed'];
                    }
                    return [value, name];
                  }}
                  content={({ active, payload, label }) => {
                    if (active && payload && payload.length) {
                      const data = payload[0].payload;
                      return (
                        <Box 
                          sx={{ 
                            backgroundColor: 'background.paper',
                            border: 1,
                            borderColor: 'divider',
                            borderRadius: 1,
                            p: 1,
                            boxShadow: 2
                          }}
                        >
                          <Typography variant="body2" fontWeight="bold">
                            {new Date(label).toLocaleString()}
                          </Typography>
                          <Typography 
                            variant="body2" 
                            color={data.success ? 'success.main' : 'error.main'}
                          >
                            {data.success 
                              ? `${data.responseTime}ms` 
                              : 'Ping Failed'
                            }
                          </Typography>
                        </Box>
                      );
                    }
                    return null;
                  }}
                />
                <Line 
                  type="monotone" 
                  dataKey="responseTime" 
                  stroke="#1976d2" 
                  strokeWidth={2}
                  dot={{ r: 2, fill: '#1976d2' }}
                  activeDot={{ r: 4, fill: '#1976d2' }}
                  connectNulls={false} // Don't connect across failed pings
                />
                {/* Add reference line for high latency threshold */}
                <ReferenceLine 
                  y={120} 
                  stroke="#ff9800" 
                  strokeDasharray="5 5"
                  label={{ value: "High Latency (120ms)", position: "top" }}
                />
                {/* Add brush for zooming and panning when we have many data points */}
                {chartData.length > 30 && (
                  <Brush 
                    dataKey="time"
                    height={30}
                    stroke="#1976d2"
                    fill="#f5f5f5"
                    tickFormatter={(value) => formatTime(new Date(value))}
                    startIndex={brushDomain ? undefined : Math.max(0, chartData.length - 50)}
                    endIndex={brushDomain ? undefined : chartData.length - 1}
                    onChange={(newDomain: any) => {
                      if (newDomain && newDomain.startIndex !== undefined) {
                        setBrushDomain([newDomain.startIndex, newDomain.endIndex]);
                      }
                    }}
                  />
                )}
              </LineChart>
            </ResponsiveContainer>
          </ErrorBoundary>
        </Box>
      </CardContent>
    </Card>
  );
});

PingHistoryChart.displayName = 'PingHistoryChart';