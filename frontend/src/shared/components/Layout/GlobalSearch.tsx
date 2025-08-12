import React, { useState, useEffect, useRef } from 'react';
import {
  Box,
  TextField,
  InputAdornment,
  Paper,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Typography,
  Chip,
  Divider,
  IconButton,
  CircularProgress,
} from '@mui/material';
import {
  Search as SearchIcon,
  Computer as DeviceIcon,
  Close as CloseIcon,
  Warning as AlertIcon,
  Description as ReportIcon,
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { useLazyQuery } from '@apollo/client';
import { SEARCH_DEVICES } from '@/shared/api/deviceQueries';
import { useDebounce } from '@/shared/hooks/useDebounce';
import { Device } from '@/shared/types/device.types';

interface SearchResult {
  id: string;
  type: 'device' | 'report' | 'alert';
  title: string;
  subtitle: string;
  description?: string;
  metadata?: Record<string, any>;
  relevanceScore: number;
}


interface GlobalSearchProps {
  className?: string;
}

export const GlobalSearch: React.FC<GlobalSearchProps> = ({ className }) => {
  const [searchQuery, setSearchQuery] = useState('');
  const [isOpen, setIsOpen] = useState(false);
  const [results, setResults] = useState<SearchResult[]>([]);
  const [loading, setLoading] = useState(false);
  const searchRef = useRef<HTMLDivElement>(null);
  const navigate = useNavigate();

  // Debounce search query to avoid too many API calls
  const debouncedSearchQuery = useDebounce(searchQuery, 300);

  // Perform search when debounced query changes
  useEffect(() => {
    if (debouncedSearchQuery.trim().length >= 2) {
      performSearch(debouncedSearchQuery);
    } else {
      setResults([]);
    }
  }, [debouncedSearchQuery]);

  // GraphQL search query
  const [searchDevices, { loading: searchLoading }] = useLazyQuery(SEARCH_DEVICES, {
    fetchPolicy: 'network-only',
    onCompleted: (data) => {
      if (data?.searchDevices?.devices) {
        // Transform device results to SearchResult format
        const deviceResults: SearchResult[] = data.searchDevices.devices.map((device: Device) => ({
          id: device.id,
          type: 'device' as const,
          title: device.name,
          subtitle: device.ipAddress,
          description: `${device.type || 'Unknown'} - ${device.location || 'No location'}`,
          metadata: {
            macAddress: device.macAddress,
            os: device.os,
            make: device.make,
            model: device.model,
          },
          relevanceScore: 1,
        }));
        setResults(deviceResults);
      } else {
        setResults([]);
      }
    },
    onError: (error) => {
      console.error('Search error:', error);
      setResults([]);
    },
  });

  const performSearch = async (query: string) => {
    setLoading(true);
    try {
      // Simple search: IP address or name
      const isIPAddress = /^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$/.test(query);
      
      await searchDevices({
        variables: {
          criteria: isIPAddress ? {
            ipAddress: query,
          } : {
            name: query,
          },
        },
      });
    } finally {
      setLoading(false);
    }
  };

  // Handle click outside to close search
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (searchRef.current && !searchRef.current.contains(event.target as Node)) {
        setIsOpen(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleSearchChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const value = event.target.value;
    setSearchQuery(value);
    setIsOpen(value.length >= 2);
  };

  const handleResultClick = (result: SearchResult) => {
    // Navigate based on result type
    switch (result.type) {
      case 'device':
        navigate(`/devices/${result.id}`);
        break;
      case 'alert':
        navigate(`/alerts?id=${result.id}`);
        break;
      case 'report':
        navigate(`/reports?id=${result.id}`);
        break;
    }
    setIsOpen(false);
    setSearchQuery('');
  };

  const handleClear = () => {
    setSearchQuery('');
    setIsOpen(false);
    setResults([]);
  };

  const handleKeyDown = (event: React.KeyboardEvent) => {
    if (event.key === 'Escape') {
      setIsOpen(false);
    }
  };

  const getIconForType = (type: string) => {
    switch (type) {
      case 'device':
        return <DeviceIcon />;
      case 'alert':
        return <AlertIcon />;
      case 'report':
        return <ReportIcon />;
      default:
        return <SearchIcon />;
    }
  };

  const getChipColorForType = (type: string): 'default' | 'primary' | 'warning' | 'info' => {
    switch (type) {
      case 'device':
        return 'primary';
      case 'alert':
        return 'warning';
      case 'report':
        return 'info';
      default:
        return 'default';
    }
  };

  return (
    <Box ref={searchRef} position="relative" className={className}>
      <TextField
        size="small"
        placeholder="Search devices"
        title="Search by device name or IP address"
        value={searchQuery}
        onChange={handleSearchChange}
        onKeyDown={handleKeyDown}
        onFocus={() => searchQuery.length >= 2 && setIsOpen(true)}
        inputProps={{ 'data-testid': 'global-search-input' }}
        sx={{
          minWidth: { xs: 200, sm: 300 },
          '& .MuiOutlinedInput-root': {
            backgroundColor: 'background.paper',
            '&.Mui-focused': {
              backgroundColor: 'background.paper',
            },
          },
        }}
        InputProps={{
          startAdornment: (
            <InputAdornment position="start">
              {(loading || searchLoading) ? (
                <CircularProgress size={20} />
              ) : (
                <SearchIcon />
              )}
            </InputAdornment>
          ),
          endAdornment: searchQuery && (
            <InputAdornment position="end">
              <IconButton size="small" onClick={handleClear} edge="end">
                <CloseIcon />
              </IconButton>
            </InputAdornment>
          ),
        }}
      />

      {/* Search Results Dropdown */}
      {isOpen && (
        <Paper
          elevation={8}
          sx={{
            position: 'absolute',
            top: '100%',
            left: 0,
            right: 0,
            mt: 1,
            maxHeight: 400,
            overflow: 'auto',
            zIndex: 1300,
          }}
        >
          {results.length > 0 ? (
            <List disablePadding>
              {/* Group results by type */}
              {['device', 'alert', 'report'].map(type => {
                const typeResults = results.filter(r => r.type === type);
                if (typeResults.length === 0) return null;

                return (
                  <React.Fragment key={type}>
                    <ListItem dense>
                      <Typography variant="overline" color="textSecondary">
                        {type}s
                      </Typography>
                    </ListItem>
                    {typeResults.map((result) => (
                      <ListItem
                        key={result.id}
                        button
                        onClick={() => handleResultClick(result)}
                        dense
                      >
                        <ListItemIcon sx={{ minWidth: 36 }}>
                          {getIconForType(result.type)}
                        </ListItemIcon>
                        <ListItemText
                          primary={result.title}
                          secondary={result.subtitle}
                          primaryTypographyProps={{ variant: 'body2' }}
                          secondaryTypographyProps={{ variant: 'caption' }}
                        />
                        <Chip
                          label={result.type}
                          size="small"
                          color={getChipColorForType(result.type)}
                          variant="outlined"
                          sx={{ ml: 1 }}
                        />
                      </ListItem>
                    ))}
                  </React.Fragment>
                );
              })}
              
              {results.length > 0 && (
                <>
                  <Divider />
                  <ListItem dense>
                    <Typography variant="caption" color="textSecondary">
                      Showing {results.length} result{results.length !== 1 ? 's' : ''}
                    </Typography>
                  </ListItem>
                </>
              )}
            </List>
          ) : searchQuery.length >= 2 && !loading && !searchLoading ? (
            <Box p={2} textAlign="center">
              <Typography variant="body2" color="textSecondary">
                No results found for "{searchQuery}"
              </Typography>
              <Typography variant="caption" color="textSecondary">
                Try searching for device names or IP addresses
              </Typography>
            </Box>
          ) : searchQuery.length < 2 ? (
            <Box p={2} textAlign="center">
              <Typography variant="caption" color="textSecondary">
                Type at least 2 characters to search
              </Typography>
            </Box>
          ) : null}
        </Paper>
      )}
    </Box>
  );
};