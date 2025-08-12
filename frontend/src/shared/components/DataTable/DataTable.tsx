import React, { useState, useMemo, useEffect } from 'react';
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TablePagination,
  Paper,
  IconButton,
  Box,
  Tooltip,
  TableSortLabel,
  Menu,
  // MenuItem,
  Chip,
  FormControl,
  FormLabel,
  FormGroup,
  FormControlLabel,
  Checkbox,
  Button,
  // Divider,
  Typography,
} from '@mui/material';
import { visuallyHidden } from '@mui/utils';
import { FilterList as FilterIcon } from '@mui/icons-material';
import { AsyncContent } from '@/shared/components/AsyncContent';
import { usePagination } from '@/shared/hooks/usePagination';
import { ErrorBoundary } from '@/shared/components/ErrorBoundary';

export interface Column<T> {
  id: string;
  label: string;
  minWidth?: number;
  align?: 'left' | 'center' | 'right';
  format?: (value: any, row: T) => React.ReactNode;
  sortable?: boolean;
  sortValue?: (row: T) => any;
  filterable?: boolean;
  filterOptions?: { value: string; label: string }[];
  filterValue?: (row: T) => string;
}

export interface Action<T> {
  icon: React.ReactNode | ((row: T) => React.ReactNode);
  label: string | ((row: T) => string);
  onClick: (row: T) => void;
  color?: 'inherit' | 'primary' | 'secondary' | 'error' | 'warning' | 'success' | ((row: T) => 'inherit' | 'primary' | 'secondary' | 'error' | 'warning' | 'success');
  disabled?: (row: T) => boolean;
}

interface DataTableProps<T> {
  data: T[];
  columns: Column<T>[];
  loading?: boolean;
  error?: Error | string | null;
  actions?: Action<T>[];
  onRowClick?: (row: T) => void;
  rowsPerPageOptions?: number[];
  defaultRowsPerPage?: number;
  emptyMessage?: string;
  getRowId?: (row: T) => string;
  stickyHeader?: boolean;
  size?: 'small' | 'medium';
  onFilteredDataChange?: (filteredData: T[]) => void;
  selectable?: boolean;
  selectedRows?: string[];
  onSelectionChange?: (selectedIds: string[]) => void;
}

type Order = 'asc' | 'desc';

export function DataTable<T extends Record<string, any>>({
  data,
  columns,
  loading = false,
  error = null,
  actions = [],
  onRowClick,
  rowsPerPageOptions = [5, 10, 25, 50, 100],
  defaultRowsPerPage = 25,
  emptyMessage = 'No data available',
  getRowId = (row) => row.id,
  stickyHeader = false,
  size = 'medium',
  onFilteredDataChange,
  selectable = false,
  selectedRows = [],
  onSelectionChange,
}: DataTableProps<T>) {
  // Find the first sortable column to use as default sort
  const defaultSortColumn = columns.find(col => col.sortable !== false)?.id || '';
  
  const [order, setOrder] = useState<Order>('asc');
  const [orderBy, setOrderBy] = useState<string>(defaultSortColumn);
  const [filters, setFilters] = useState<Record<string, string[]>>({});
  const [filterAnchorEl, setFilterAnchorEl] = useState<{ [key: string]: HTMLElement | null }>({});
  const [selected, setSelected] = useState<string[]>(selectedRows);
  
  const {
    page,
    rowsPerPage,
    handleChangePage,
    handleChangeRowsPerPage,
  } = usePagination(defaultRowsPerPage);

  const handleRequestSort = (property: string) => {
    const isAsc = orderBy === property && order === 'asc';
    setOrder(isAsc ? 'desc' : 'asc');
    setOrderBy(property);
    // Reset to first page when sorting changes
    handleChangePage(null, 0);
  };

  const handleFilterClick = (event: React.MouseEvent<HTMLElement>, columnId: string) => {
    event.stopPropagation();
    setFilterAnchorEl({ ...filterAnchorEl, [columnId]: event.currentTarget });
  };

  const handleFilterClose = (columnId: string) => {
    setFilterAnchorEl({ ...filterAnchorEl, [columnId]: null });
  };

  const handleFilterChange = (columnId: string, value: string, checked: boolean) => {
    const currentFilters = filters[columnId] || [];
    let newFilters: string[];
    
    if (checked) {
      newFilters = [...currentFilters, value];
    } else {
      newFilters = currentFilters.filter(f => f !== value);
    }
    
    if (newFilters.length === 0) {
      const { [columnId]: _, ...rest } = filters;
      setFilters(rest);
    } else {
      setFilters({ ...filters, [columnId]: newFilters });
    }
    
    // Reset to first page when filters change
    handleChangePage(null, 0);
  };

  const clearFilters = () => {
    setFilters({});
    handleChangePage(null, 0);
  };

  const filteredData = useMemo(() => {
    if (Object.keys(filters).length === 0) return data;
    
    return data.filter(row => {
      return Object.entries(filters).every(([columnId, filterValues]) => {
        if (filterValues.length === 0) return true;
        
        const column = columns.find(col => col.id === columnId);
        if (!column) return true;
        
        const value = column.filterValue ? column.filterValue(row) : String(row[columnId] || '');
        return filterValues.includes(value);
      });
    });
  }, [data, filters, columns]);

  const sortedData = useMemo(() => {
    if (!orderBy) return filteredData;
    
    const column = columns.find(col => col.id === orderBy);
    if (!column) return filteredData;
    
    return [...filteredData].sort((a, b) => {
      let aValue = column.sortValue ? column.sortValue(a) : a[orderBy];
      let bValue = column.sortValue ? column.sortValue(b) : b[orderBy];
      
      // Handle null/undefined values
      if (aValue == null) aValue = '';
      if (bValue == null) bValue = '';
      
      // Convert to strings for comparison if not numbers
      if (typeof aValue !== 'number' && typeof bValue !== 'number') {
        aValue = String(aValue).toLowerCase();
        bValue = String(bValue).toLowerCase();
      }
      
      if (aValue < bValue) {
        return order === 'asc' ? -1 : 1;
      }
      if (aValue > bValue) {
        return order === 'asc' ? 1 : -1;
      }
      return 0;
    });
  }, [filteredData, order, orderBy, columns]);

  const paginatedData = sortedData.slice(
    page * rowsPerPage,
    page * rowsPerPage + rowsPerPage
  );

  // Get unique filter options for each column
  const getFilterOptions = (column: Column<T>) => {
    if (column.filterOptions) return column.filterOptions;
    
    const values = new Set<string>();
    data.forEach(row => {
      const value = column.filterValue ? column.filterValue(row) : String(row[column.id] || '');
      if (value) values.add(value);
    });
    
    return Array.from(values).sort().map(value => ({ value, label: value }));
  };

  // Notify parent component when filtered data changes
  useEffect(() => {
    if (onFilteredDataChange) {
      onFilteredDataChange(filteredData);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [filters, data.length]); // Only trigger when filters or data length changes, not filteredData itself

  const handleRowClick = (row: T) => {
    if (onRowClick) {
      onRowClick(row);
    }
  };

  // Selection handlers
  const handleSelectAllClick = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.checked) {
      const newSelected = paginatedData.map((row) => getRowId(row));
      setSelected(newSelected);
      onSelectionChange?.(newSelected);
    } else {
      setSelected([]);
      onSelectionChange?.([]);
    }
  };

  const handleSelectClick = (event: React.MouseEvent<unknown>, id: string) => {
    event.stopPropagation();
    const selectedIndex = selected.indexOf(id);
    let newSelected: string[] = [];

    if (selectedIndex === -1) {
      newSelected = newSelected.concat(selected, id);
    } else if (selectedIndex === 0) {
      newSelected = newSelected.concat(selected.slice(1));
    } else if (selectedIndex === selected.length - 1) {
      newSelected = newSelected.concat(selected.slice(0, -1));
    } else if (selectedIndex > 0) {
      newSelected = newSelected.concat(
        selected.slice(0, selectedIndex),
        selected.slice(selectedIndex + 1),
      );
    }

    setSelected(newSelected);
    onSelectionChange?.(newSelected);
  };

  const isSelected = (id: string) => selected.indexOf(id) !== -1;

  // Update selected when selectedRows prop changes
  useEffect(() => {
    setSelected(selectedRows);
  }, [selectedRows]);

  return (
    <Paper elevation={2} role="region" aria-label="Data table">
      <AsyncContent
        loading={loading}
        error={error}
        empty={data.length === 0}
        emptyMessage={emptyMessage}
      >
        {Object.keys(filters).length > 0 && (
          <Box sx={{ p: 2, borderBottom: 1, borderColor: 'divider', bgcolor: 'action.hover' }}>
            <Box display="flex" alignItems="center" gap={1} flexWrap="wrap">
              <Typography variant="body2" sx={{ mr: 1 }}>Active filters:</Typography>
              {Object.entries(filters).map(([columnId, values]) => {
                const column = columns.find(col => col.id === columnId);
                return values.map(value => (
                  <Chip
                    key={`${columnId}-${value}`}
                    label={`${column?.label}: ${value}`}
                    size="small"
                    onDelete={() => handleFilterChange(columnId, value, false)}
                  />
                ));
              })}
              <Button size="small" onClick={clearFilters} sx={{ ml: 'auto' }}>
                Clear all
              </Button>
            </Box>
          </Box>
        )}
        <TableContainer>
          <Table 
            stickyHeader={stickyHeader} 
            size={size}
            aria-label="Data table"
          >
            <TableHead>
              <TableRow>
                {selectable && (
                  <TableCell padding="checkbox">
                    <Checkbox
                      color="primary"
                      indeterminate={selected.length > 0 && selected.length < paginatedData.length}
                      checked={paginatedData.length > 0 && selected.length === paginatedData.length}
                      onChange={handleSelectAllClick}
                      inputProps={{
                        'aria-label': 'select all items',
                      }}
                    />
                  </TableCell>
                )}
                {columns.map((column) => (
                  <TableCell
                    key={column.id}
                    align={column.align || 'left'}
                    style={{ minWidth: column.minWidth }}
                    sortDirection={orderBy === column.id ? order : false}
                  >
                    <Box display="flex" alignItems="center" gap={0.5}>
                      {column.sortable !== false ? (
                        <TableSortLabel
                          active={orderBy === column.id}
                          direction={orderBy === column.id ? order : 'asc'}
                          onClick={() => handleRequestSort(column.id)}
                        >
                          {column.label}
                          {orderBy === column.id ? (
                            <Box component="span" sx={visuallyHidden}>
                              {order === 'desc' ? 'sorted descending' : 'sorted ascending'}
                            </Box>
                          ) : null}
                        </TableSortLabel>
                      ) : (
                        column.label
                      )}
                      {column.filterable !== false && (
                        <>
                          <IconButton
                            size="small"
                            onClick={(e) => handleFilterClick(e, column.id)}
                            sx={{ 
                              ml: 'auto',
                              color: filters[column.id]?.length > 0 ? 'primary.main' : 'action.active'
                            }}
                          >
                            <FilterIcon fontSize="small" />
                          </IconButton>
                          <Menu
                            anchorEl={filterAnchorEl[column.id]}
                            open={Boolean(filterAnchorEl[column.id])}
                            onClose={() => handleFilterClose(column.id)}
                            PaperProps={{ sx: { maxHeight: 300 } }}
                          >
                            <Box sx={{ px: 2, py: 1 }}>
                              <FormControl component="fieldset">
                                <FormLabel component="legend" sx={{ fontSize: '0.875rem', mb: 1 }}>
                                  Filter by {column.label}
                                </FormLabel>
                                <FormGroup>
                                  {getFilterOptions(column).map(option => (
                                    <FormControlLabel
                                      key={option.value}
                                      control={
                                        <Checkbox
                                          checked={filters[column.id]?.includes(option.value) || false}
                                          onChange={(e) => handleFilterChange(column.id, option.value, e.target.checked)}
                                          size="small"
                                        />
                                      }
                                      label={option.label}
                                      sx={{ '& .MuiFormControlLabel-label': { fontSize: '0.875rem' } }}
                                    />
                                  ))}
                                </FormGroup>
                              </FormControl>
                            </Box>
                          </Menu>
                        </>
                      )}
                    </Box>
                  </TableCell>
                ))}
                {actions.length > 0 && (
                  <TableCell align="right">Actions</TableCell>
                )}
              </TableRow>
            </TableHead>
            <TableBody>
              {paginatedData.map((row) => (
                <ErrorBoundary
                  key={getRowId(row)}
                  fallback={
                    <TableRow>
                      <TableCell colSpan={columns.length + (actions.length > 0 ? 1 : 0) + (selectable ? 1 : 0)}>
                        <Box textAlign="center" py={1} color="error.main">
                          Error rendering row
                        </Box>
                      </TableCell>
                    </TableRow>
                  }
                >
                  <TableRow
                    hover
                    onClick={() => !selectable && handleRowClick(row)}
                    tabIndex={onRowClick && !selectable ? 0 : -1}
                    onKeyDown={onRowClick && !selectable ? (e) => {
                      if (e.key === 'Enter' || e.key === ' ') {
                        e.preventDefault();
                        handleRowClick(row);
                      }
                    } : undefined}
                    role={onRowClick && !selectable ? "button" : undefined}
                    aria-label={onRowClick && !selectable ? `View details for ${row.name || row.id}` : undefined}
                    selected={isSelected(getRowId(row))}
                    sx={{ 
                      cursor: onRowClick && !selectable ? 'pointer' : 'default',
                      transition: 'all 0.2s ease',
                      '&:hover': onRowClick && !selectable ? {
                        backgroundColor: 'action.hover',
                        transform: 'scale(1.005)',
                        boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
                      } : {},
                      '&:focus': {
                        outline: '2px solid',
                        outlineColor: 'primary.main',
                        outlineOffset: -2,
                      }
                    }}
                  >
                    {selectable && (
                      <TableCell padding="checkbox">
                        <Checkbox
                          color="primary"
                          checked={isSelected(getRowId(row))}
                          onClick={(event) => handleSelectClick(event, getRowId(row))}
                          inputProps={{
                            'aria-labelledby': `row-${getRowId(row)}`,
                          }}
                        />
                      </TableCell>
                    )}
                    {columns.map((column) => {
                      const value = row[column.id];
                      return (
                        <TableCell key={column.id} align={column.align || 'left'}>
                          {column.format ? column.format(value, row) : value}
                        </TableCell>
                      );
                    })}
                    {actions.length > 0 && (
                      <TableCell align="right">
                        <Box display="flex" gap={1} justifyContent="flex-end">
                          {actions.map((action, index) => {
                            const label = typeof action.label === 'function' ? action.label(row) : action.label;
                            const icon = typeof action.icon === 'function' ? action.icon(row) : action.icon;
                            const color = typeof action.color === 'function' ? action.color(row) : (action.color || 'primary');
                            
                            return (
                              <Tooltip key={index} title={label}>
                                <span>
                                  <IconButton
                                    size="small"
                                    color={color}
                                    onClick={(e) => {
                                      e.stopPropagation();
                                      action.onClick(row);
                                    }}
                                    disabled={action.disabled?.(row)}
                                  >
                                    {icon}
                                  </IconButton>
                                </span>
                              </Tooltip>
                            );
                          })}
                        </Box>
                      </TableCell>
                    )}
                  </TableRow>
                </ErrorBoundary>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
        <TablePagination
          rowsPerPageOptions={rowsPerPageOptions}
          component="div"
          count={sortedData.length}
          rowsPerPage={rowsPerPage}
          page={page}
          onPageChange={handleChangePage}
          onRowsPerPageChange={handleChangeRowsPerPage}
          labelDisplayedRows={({ from, to, count }) => 
            `${from}–${to} of ${count !== -1 ? count : `more than ${to}`}${
              Object.keys(filters).length > 0 ? ` (filtered from ${data.length} total)` : ''
            }`
          }
        />
      </AsyncContent>
    </Paper>
  );
}