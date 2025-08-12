import React, { useRef, useEffect, useState } from 'react';
import { Box, Tooltip } from '@mui/material';
import { useLocale } from '@/shared/contexts/LocaleContext';

interface PingData {
  timestamp: string;
  success: boolean;
  responseTimeMs?: number;
}

interface MiniPingIndicatorProps {
  recentPings?: PingData[];
}

export const MiniPingIndicator: React.FC<MiniPingIndicatorProps> = React.memo(({
  recentPings = [],
}) => {
  const containerRef = useRef<HTMLDivElement>(null);
  const [maxDotsToShow, setMaxDotsToShow] = useState(20);
  const { formatRelativeTime } = useLocale();
  
  useEffect(() => {
    const calculateMaxDots = () => {
      if (containerRef.current && containerRef.current.offsetWidth > 0) {
        const containerWidth = containerRef.current.offsetWidth;
        // Each dot is 10px wide + 4px gap = 14px per dot
        const dotsPerRow = Math.floor(containerWidth / 14);
        // Show only 1 row of dots
        setMaxDotsToShow(Math.min(dotsPerRow, recentPings.length));
      }
    };
    
    // Use ResizeObserver for more reliable size detection
    const resizeObserver = new ResizeObserver(() => {
      calculateMaxDots();
    });
    
    if (containerRef.current) {
      resizeObserver.observe(containerRef.current);
    }
    
    // Also calculate on mount with a delay
    const timeout = setTimeout(calculateMaxDots, 100);
    
    return () => {
      resizeObserver.disconnect();
      clearTimeout(timeout);
    };
  }, [recentPings.length]);
  
  // Use calculated number of pings
  const pingsToShow = recentPings.slice(0, maxDotsToShow);

  return (
    <Box 
      ref={containerRef}
      sx={{ 
        display: 'flex',
        gap: '4px',
        flexWrap: 'wrap',
        width: '100%',
        alignItems: 'center',
        justifyContent: 'flex-start',
        height: '10px', // Single row height
        overflow: 'hidden',
      }}
    >
      {pingsToShow.map((ping, index) => {
        const isSuccess = ping.success;
          const responseTime = ping.responseTimeMs;
          const timestamp = new Date(ping.timestamp);
          
          // Color logic: green for success, red for failure
          // Green shade based on response time: darker = faster
          let backgroundColor = 'error.main'; // Red for failed pings
          
          if (isSuccess) {
            if (responseTime !== undefined) {
              if (responseTime <= 30) {
                backgroundColor = 'success.dark'; // Dark green for very fast
              } else if (responseTime <= 100) {
                backgroundColor = 'success.main'; // Medium green for good
              } else if (responseTime <= 200) {
                backgroundColor = 'success.light'; // Light green for acceptable
              } else {
                backgroundColor = 'warning.main'; // Orange for slow but successful
              }
            } else {
              backgroundColor = 'success.main'; // Default green for unknown response time
            }
          }

          const tooltipText = isSuccess 
            ? `Success: ${responseTime}ms (${formatRelativeTime(timestamp)})`
            : `Failed (${formatRelativeTime(timestamp)})`;

          return (
            <Tooltip key={index} title={tooltipText} arrow placement="top">
              <Box
                sx={{
                  width: 10,
                  height: 10,
                  minWidth: 10,
                  borderRadius: '50%',
                  backgroundColor,
                  cursor: 'help',
                  transition: 'all 0.2s ease-in-out',
                  boxShadow: '0 1px 2px rgba(0,0,0,0.1)',
                  animation: 'fadeIn 0.3s ease-in-out',
                  '@keyframes fadeIn': {
                    from: { opacity: 0, transform: 'scale(0.8)' },
                    to: { opacity: 1, transform: 'scale(1)' },
                  },
                  '&:hover': {
                    transform: 'scale(1.3)',
                    boxShadow: '0 2px 4px rgba(0,0,0,0.2)',
                  },
                }}
              />
            </Tooltip>
          );
      })}
    </Box>
  );
});

MiniPingIndicator.displayName = 'MiniPingIndicator';