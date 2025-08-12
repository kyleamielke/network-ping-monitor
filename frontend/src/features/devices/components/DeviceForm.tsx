import React, { useState, useEffect } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Button,
  Grid,
  Box,
  Alert,
  Autocomplete,
  Zoom,
  Fade,
} from '@mui/material';
import { useMutation } from '@apollo/client';
import { CREATE_DEVICE, UPDATE_DEVICE, CreateDeviceInput, UpdateDeviceInput } from '@/features/devices/api/deviceMutations';
import { Device } from '@/shared/types/device.types';

interface DeviceFormProps {
  open: boolean;
  onClose: () => void;
  onSave: () => void;
  device?: Device | null;
  isEditing?: boolean;
}

export const DeviceForm: React.FC<DeviceFormProps> = ({
  open,
  onClose,
  onSave,
  device,
  isEditing = false,
}) => {
  const [formData, setFormData] = useState<CreateDeviceInput>({
    name: '',
    ipAddress: '',
    hostname: '',
    macAddress: '',
    type: '',
    os: '',
    // osType: '',
    make: '',
    model: '',
    endpointId: '',
    assetTag: '',
    description: '',
    location: '',
    // site: '',
  });
  const [errors, setErrors] = useState<Record<string, string>>({});

  const [createDevice, { loading: creating }] = useMutation(CREATE_DEVICE, {
    onCompleted: () => {
      onSave();
      handleClose();
    },
    onError: (error) => {
      console.error('Error creating device:', error);
      if (error.graphQLErrors && error.graphQLErrors.length > 0) {
        error.graphQLErrors.forEach((gqlError, index) => {
          console.error(`GraphQL Error ${index}:`, gqlError);
          console.error('Message:', gqlError.message);
          console.error('Extensions:', gqlError.extensions);
          console.error('Path:', gqlError.path);
        });
      }
      console.error('Network error:', error.networkError);
      setErrors({ submit: error.message });
    },
  });

  const [updateDevice, { loading: updating }] = useMutation(UPDATE_DEVICE, {
    onCompleted: () => {
      onSave();
      handleClose();
    },
    onError: (error) => {
      console.error('Error updating device:', error);
      
      // Check for optimistic locking error
      if (error.graphQLErrors?.some(e => 
        e.extensions?.code === 'OPTIMISTIC_LOCKING_ERROR' ||
        e.message.toLowerCase().includes('optimistic locking')
      )) {
        setErrors({ 
          submit: 'This device was modified by another user. Please close and reopen this form to get the latest version.' 
        });
      } else {
        setErrors({ submit: error.message });
      }
    },
  });

  const loading = creating || updating;

  // Reset form when dialog opens/closes or device changes
  useEffect(() => {
    if (open) {
      if (device && isEditing) {
        setFormData({
          name: device.name || '',
          ipAddress: device.ipAddress || '',
          hostname: device.hostname || '',
          macAddress: device.macAddress || '',
          type: device.type || '',
          os: device.os || '',
          // osType: device.osType || '',
          make: device.make || '',
          model: device.model || '',
          endpointId: device.endpointId || '',
          assetTag: device.assetTag || '',
          description: device.description || '',
          location: device.location || '',
          // site: device.site || '',
        });
      } else {
        // Reset to empty form for new device
        setFormData({
          name: '',
          ipAddress: '',
          hostname: '',
          macAddress: '',
          type: '',
          os: '',
          // osType: '',
          make: '',
          model: '',
          endpointId: '',
          assetTag: '',
          description: '',
          location: '',
          // site: '',
        });
      }
      setErrors({});
    }
  }, [open, device, isEditing]);

  const handleInputChange = (field: keyof (CreateDeviceInput & { hostname?: string; location?: string; description?: string })) => (
    event: React.ChangeEvent<HTMLInputElement>
  ) => {
    setFormData(prev => ({
      ...prev,
      [field]: event.target.value,
    }));
    // Clear field error when user starts typing
    if (errors[field]) {
      setErrors(prev => ({
        ...prev,
        [field]: '',
      }));
    }
  };

  const validateForm = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!formData.name.trim()) {
      newErrors.name = 'Device name is required';
    }

    // Validate that either IP address or hostname is provided
    const hasIpAddress = formData.ipAddress && formData.ipAddress.trim();
    const hasHostname = formData.hostname && formData.hostname.trim();

    if (!hasIpAddress && !hasHostname) {
      newErrors.address = 'Either IP address or hostname is required';
    } else {
      // Validate IP address if provided
      if (hasIpAddress) {
        const ipRegex = /^(?:[0-9]{1,3}\.){3}[0-9]{1,3}$/;
        if (!ipRegex.test(formData.ipAddress!)) {
          newErrors.ipAddress = 'Please enter a valid IP address';
        }
      }
      
      // Validate hostname if provided
      if (hasHostname) {
        // RFC 1123 compliant hostname validation
        const hostnameRegex = /^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\-]*[a-zA-Z0-9])\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\-]*[A-Za-z0-9])$/;
        if (formData.hostname!.length > 253 || !hostnameRegex.test(formData.hostname!)) {
          newErrors.hostname = 'Please enter a valid hostname';
        }
      }
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async () => {
    if (!validateForm()) {
      return;
    }

    // For updates, send empty strings to clear fields; for creates, remove empty values
    const cleanedData = Object.entries(formData).reduce((acc, [key, value]) => {
      if (isEditing) {
        // For updates, send the value even if empty (to allow clearing fields)
        acc[key as keyof CreateDeviceInput] = value ? value.trim() : '';
      } else {
        // For creates, only send non-empty values
        if (value && value.trim() !== '') {
          acc[key as keyof CreateDeviceInput] = value.trim();
        }
      }
      return acc;
    }, {} as CreateDeviceInput);

    try {
      if (isEditing && device) {
        console.log('Updating device with data:', cleanedData);
        const updateInput: UpdateDeviceInput = {
          ...cleanedData as UpdateDeviceInput,
          expectedVersion: device.version,
        };
        await updateDevice({
          variables: {
            id: device.id,
            input: updateInput,
          },
        });
      } else {
        console.log('Creating device with data:', cleanedData);
        await createDevice({
          variables: {
            input: cleanedData,
          },
        });
      }
    } catch (error) {
      console.error('Mutation error:', error);
      // Error handling is done in mutation callbacks
    }
  };

  const handleClose = () => {
    setFormData({
      name: '',
      ipAddress: '',
      hostname: '',
      macAddress: '',
      type: '',
      os: '',
      // osType: '',
      make: '',
      model: '',
      endpointId: '',
      assetTag: '',
      description: '',
      location: '',
      // site: '',
    });
    setErrors({});
    onClose();
  };

  const deviceTypes = [
    'Network Router',
    'Network Switch',
    'Server',
    'Workstation',
    'Virtual Machine',
    'Security Device',
    'Other',
  ];

  // const osTypes = [
  //   'Windows',
  //   'Linux',
  //   'macOS',
  //   'iOS',
  //   'Android',
  //   'Firmware',
  //   'Other',
  // ];

  return (
    <Dialog 
      open={open} 
      onClose={handleClose} 
      maxWidth="md" 
      fullWidth
      TransitionComponent={Zoom}
      transitionDuration={300}
    >
      <DialogTitle>
        {isEditing ? 'Edit Device' : 'Add New Device'}
      </DialogTitle>
      <DialogContent>
        <Box mt={1}>
          <Fade in={!!errors.submit || !!errors.address} timeout={300}>
            <Box>
              {errors.submit && (
                <Alert severity="error" sx={{ mb: 2 }}>
                  {errors.submit}
                </Alert>
              )}
              {errors.address && (
                <Alert severity="error" sx={{ mb: 2 }}>
                  {errors.address}
                </Alert>
              )}
            </Box>
          </Fade>
          
          <Grid container spacing={3}>
            {/* Required Fields */}
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Device Name"
                value={formData.name}
                onChange={handleInputChange('name')}
                error={!!errors.name}
                helperText={errors.name}
                required
                inputProps={{
                  'aria-label': 'Device name',
                  'aria-required': true,
                  'aria-describedby': errors.name ? 'device-name-error' : undefined
                }}
                FormHelperTextProps={{
                  id: 'device-name-error'
                }}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="MAC Address"
                value={formData.macAddress}
                onChange={handleInputChange('macAddress')}
                placeholder="00:11:22:33:44:55"
              />
            </Grid>

            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="IP Address"
                value={formData.ipAddress}
                onChange={handleInputChange('ipAddress')}
                error={!!errors.ipAddress}
                helperText={errors.ipAddress || 'Optional if hostname is provided'}
                placeholder="192.168.1.100"
                inputProps={{
                  'aria-label': 'IP address',
                  'aria-describedby': errors.ipAddress ? 'ip-address-error' : undefined
                }}
                FormHelperTextProps={{
                  id: 'ip-address-error'
                }}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Hostname"
                value={formData.hostname}
                onChange={handleInputChange('hostname')}
                error={!!errors.hostname}
                helperText={errors.hostname || 'Optional if IP address is provided'}
                placeholder="server.example.com"
                inputProps={{
                  'aria-label': 'Hostname',
                  'aria-describedby': errors.hostname ? 'hostname-error' : undefined
                }}
                FormHelperTextProps={{
                  id: 'hostname-error'
                }}
              />
            </Grid>

            {/* Optional Fields */}
            <Grid item xs={12} sm={6}>
              <Autocomplete
                freeSolo
                options={deviceTypes}
                value={formData.type}
                onChange={(_event, newValue) => {
                  setFormData(prev => ({
                    ...prev,
                    type: newValue || '',
                  }));
                }}
                onInputChange={(_event, newInputValue) => {
                  setFormData(prev => ({
                    ...prev,
                    type: newInputValue,
                  }));
                }}
                renderInput={(params) => (
                  <TextField
                    {...params}
                    label="Device Type"
                    placeholder="Select or enter custom type"
                    fullWidth
                  />
                )}
              />
            </Grid>

            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Operating System"
                value={formData.os}
                onChange={handleInputChange('os')}
                placeholder="Windows 11, Ubuntu 22.04, etc."
              />
            </Grid>
            {/* OS Type - Commented out for now */}
            {/* <Grid item xs={12} sm={6}>
              <Autocomplete
                freeSolo
                options={osTypes}
                value={formData.osType}
                onChange={(_event, newValue) => {
                  setFormData(prev => ({
                    ...prev,
                    osType: newValue || '',
                  }));
                }}
                onInputChange={(_event, newInputValue) => {
                  setFormData(prev => ({
                    ...prev,
                    osType: newInputValue,
                  }));
                }}
                renderInput={(params) => (
                  <TextField
                    {...params}
                    label="OS Type"
                    placeholder="Select or enter custom OS type"
                    fullWidth
                  />
                )}
              />
            </Grid> */}

            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Make"
                value={formData.make}
                onChange={handleInputChange('make')}
                placeholder="Dell, HP, Cisco, etc."
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Model"
                value={formData.model}
                onChange={handleInputChange('model')}
                placeholder="Optiplex 7090, Catalyst 2960, etc."
              />
            </Grid>

            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Endpoint ID"
                value={formData.endpointId}
                onChange={handleInputChange('endpointId')}
                placeholder="WORKSTATION-001"
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Asset Tag"
                value={formData.assetTag}
                onChange={handleInputChange('assetTag')}
                placeholder="IT-2024-001"
              />
            </Grid>

            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Location"
                value={formData.location}
                onChange={handleInputChange('location')}
                placeholder="Building A, Floor 2, Server Room"
              />
            </Grid>

            {/* Site - Commented out for now */}
            {/* <Grid item xs={12}>
              <TextField
                fullWidth
                label="Site/Location"
                value={formData.site}
                onChange={handleInputChange('site')}
                placeholder="Building A, Floor 2, Server Room"
              />
            </Grid> */}
          </Grid>
        </Box>
      </DialogContent>
      <DialogActions>
        <Button 
          onClick={handleClose} 
          disabled={loading}
          sx={{
            transition: 'all 0.3s ease',
            '&:hover': {
              transform: 'translateX(-2px)',
            }
          }}
        >
          Cancel
        </Button>
        <Button
          onClick={handleSubmit}
          variant="contained"
          disabled={loading}
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
          {loading ? 'Saving...' : (isEditing ? 'Update' : 'Create')}
        </Button>
      </DialogActions>
    </Dialog>
  );
};