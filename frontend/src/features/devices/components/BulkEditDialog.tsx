import React, { useState } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Box,
  Typography,
  Alert,
  Chip,
  Stack,
} from '@mui/material';
import { DeviceWithMonitoring } from '@/shared/types/device.types';

interface BulkEditDialogProps {
  open: boolean;
  selectedDevices: DeviceWithMonitoring[];
  onClose: () => void;
  onSave: (updates: BulkEditUpdates) => void;
}

export interface BulkEditUpdates {
  type?: string;
  os?: string;
  // osType?: string;
  make?: string;
  model?: string;
  description?: string;
  location?: string;
  // site?: string;
}

export const BulkEditDialog: React.FC<BulkEditDialogProps> = ({
  open,
  selectedDevices,
  onClose,
  onSave,
}) => {
  const [updates, setUpdates] = useState<BulkEditUpdates>({});
  const [touchedFields, setTouchedFields] = useState<Set<string>>(new Set());

  const handleFieldChange = (field: keyof BulkEditUpdates, value: string) => {
    setUpdates(prev => ({ ...prev, [field]: value }));
    setTouchedFields(prev => new Set([...prev, field]));
  };

  const handleSave = () => {
    // Only include fields that were actually touched/modified
    const updatesToSend: BulkEditUpdates = {};
    touchedFields.forEach(field => {
      const value = updates[field as keyof BulkEditUpdates];
      if (value !== undefined && value !== '') {
        updatesToSend[field as keyof BulkEditUpdates] = value;
      }
    });

    if (Object.keys(updatesToSend).length > 0) {
      onSave(updatesToSend);
      handleClose();
    }
  };

  const handleClose = () => {
    setUpdates({});
    setTouchedFields(new Set());
    onClose();
  };

  // Get unique values from selected devices for each field
  const getUniqueValues = (field: keyof DeviceWithMonitoring): string[] => {
    const values = new Set<string>();
    selectedDevices.forEach(device => {
      const value = device[field];
      if (value && typeof value === 'string') {
        values.add(value);
      }
    });
    return Array.from(values).sort();
  };

  const deviceTypes = getUniqueValues('type');
  const operatingSystems = getUniqueValues('os');
  // const osTypes = getUniqueValues('osType');
  const makes = getUniqueValues('make');
  const models = getUniqueValues('model');
  // const sites = getUniqueValues('site');

  return (
    <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
      <DialogTitle>
        Bulk Edit Devices
        <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
          Editing {selectedDevices.length} device{selectedDevices.length !== 1 ? 's' : ''}
        </Typography>
      </DialogTitle>
      <DialogContent>
        <Alert severity="info" sx={{ mb: 2 }}>
          Only the fields you modify will be updated. Leave fields empty to keep existing values.
        </Alert>

        <Stack spacing={2}>
          {/* Device Type */}
          <FormControl fullWidth>
            <InputLabel>Device Type</InputLabel>
            <Select
              value={updates.type || ''}
              onChange={(e) => handleFieldChange('type', e.target.value)}
              label="Device Type"
            >
              <MenuItem value="">Keep existing</MenuItem>
              <MenuItem value="workstation">Workstation</MenuItem>
              <MenuItem value="server">Server</MenuItem>
              <MenuItem value="network">Network Device</MenuItem>
              <MenuItem value="printer">Printer</MenuItem>
              <MenuItem value="mobile">Mobile Device</MenuItem>
              <MenuItem value="other">Other</MenuItem>
            </Select>
            {deviceTypes.length > 0 && (
              <Typography variant="caption" color="text.secondary" sx={{ mt: 0.5 }}>
                Current values: {deviceTypes.join(', ')}
              </Typography>
            )}
          </FormControl>

          {/* Operating System */}
          <TextField
            fullWidth
            label="Operating System"
            value={updates.os || ''}
            onChange={(e) => handleFieldChange('os', e.target.value)}
            placeholder="e.g., Windows 11, Ubuntu 22.04"
            helperText={operatingSystems.length > 0 ? `Current values: ${operatingSystems.join(', ')}` : undefined}
          />

          {/* OS Type - Commented out for now */}
          {/* <FormControl fullWidth>
            <InputLabel>OS Type</InputLabel>
            <Select
              value={updates.osType || ''}
              onChange={(e) => handleFieldChange('osType', e.target.value)}
              label="OS Type"
            >
              <MenuItem value="">Keep existing</MenuItem>
              <MenuItem value="windows">Windows</MenuItem>
              <MenuItem value="linux">Linux</MenuItem>
              <MenuItem value="macos">macOS</MenuItem>
              <MenuItem value="ios">iOS</MenuItem>
              <MenuItem value="android">Android</MenuItem>
              <MenuItem value="other">Other</MenuItem>
            </Select>
            {osTypes.length > 0 && (
              <Typography variant="caption" color="text.secondary" sx={{ mt: 0.5 }}>
                Current values: {osTypes.join(', ')}
              </Typography>
            )}
          </FormControl> */}

          {/* Make */}
          <TextField
            fullWidth
            label="Make/Manufacturer"
            value={updates.make || ''}
            onChange={(e) => handleFieldChange('make', e.target.value)}
            placeholder="e.g., Dell, HP, Apple"
            helperText={makes.length > 0 ? `Current values: ${makes.join(', ')}` : undefined}
          />

          {/* Model */}
          <TextField
            fullWidth
            label="Model"
            value={updates.model || ''}
            onChange={(e) => handleFieldChange('model', e.target.value)}
            placeholder="e.g., OptiPlex 7090, MacBook Pro"
            helperText={models.length > 0 ? `Current values: ${models.join(', ')}` : undefined}
          />

          {/* Description */}
          <TextField
            fullWidth
            label="Description"
            value={updates.description || ''}
            onChange={(e) => handleFieldChange('description', e.target.value)}
            multiline
            rows={2}
            placeholder="Additional notes or description"
          />

          {/* Location */}
          <TextField
            fullWidth
            label="Location"
            value={updates.location || ''}
            onChange={(e) => handleFieldChange('location', e.target.value)}
            placeholder="e.g., Building A, Floor 2"
          />

          {/* Site - Commented out for now */}
          {/* <TextField
            fullWidth
            label="Site"
            value={updates.site || ''}
            onChange={(e) => handleFieldChange('site', e.target.value)}
            placeholder="e.g., Headquarters, Branch Office"
            helperText={sites.length > 0 ? `Current values: ${sites.join(', ')}` : undefined}
          /> */}
        </Stack>

        {touchedFields.size > 0 && (
          <Box sx={{ mt: 2 }}>
            <Typography variant="body2" color="text.secondary" gutterBottom>
              Fields to update:
            </Typography>
            <Stack direction="row" spacing={1} flexWrap="wrap">
              {Array.from(touchedFields).map(field => (
                <Chip
                  key={field}
                  label={field}
                  size="small"
                  onDelete={() => {
                    setTouchedFields(prev => {
                      const newSet = new Set(prev);
                      newSet.delete(field);
                      return newSet;
                    });
                    setUpdates(prev => {
                      const newUpdates = { ...prev };
                      delete newUpdates[field as keyof BulkEditUpdates];
                      return newUpdates;
                    });
                  }}
                />
              ))}
            </Stack>
          </Box>
        )}
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose}>Cancel</Button>
        <Button
          onClick={handleSave}
          variant="contained"
          disabled={touchedFields.size === 0}
        >
          Update {selectedDevices.length} Device{selectedDevices.length !== 1 ? 's' : ''}
        </Button>
      </DialogActions>
    </Dialog>
  );
};