#!/usr/bin/env node

/**
 * Start Monitoring Script
 * Automatically starts ping monitoring for all devices in the system
 */

const axios = require('axios');

const API_BASE_URL = process.env.API_URL || 'http://localhost:8080';

const GET_ALL_DEVICES_QUERY = `
  query GetAllDevices {
    devices {
      uuid
      name
      ipAddress
      deviceType
    }
  }
`;

const START_MONITORING_MUTATION = `
  mutation StartPingMonitoring($deviceId: String!) {
    startPingMonitoring(deviceId: $deviceId) {
      deviceId
      pingInterval
      timeout
      alertingEnabled
      isActive
    }
  }
`;

async function getAllDevices() {
  try {
    const response = await axios.post(`${API_BASE_URL}/graphql`, {
      query: GET_ALL_DEVICES_QUERY
    });

    if (response.data.errors) {
      throw new Error(response.data.errors[0].message);
    }

    return response.data.data.devices;
  } catch (error) {
    if (error.response?.data?.errors) {
      throw new Error(error.response.data.errors[0].message);
    }
    throw error;
  }
}

async function startMonitoring(deviceId) {
  try {
    const response = await axios.post(`${API_BASE_URL}/graphql`, {
      query: START_MONITORING_MUTATION,
      variables: { deviceId }
    });

    if (response.data.errors) {
      throw new Error(response.data.errors[0].message);
    }

    return response.data.data.startPingMonitoring;
  } catch (error) {
    if (error.response?.data?.errors) {
      throw new Error(error.response.data.errors[0].message);
    }
    throw error;
  }
}

async function startMonitoringForAllDevices() {
  console.log('🚀 Starting monitoring for all devices...');
  console.log(`📡 API URL: ${API_BASE_URL}`);
  console.log('');

  // Get all devices
  console.log('📋 Fetching all devices...');
  const devices = await getAllDevices();
  console.log(`Found ${devices.length} devices`);
  console.log('');

  if (devices.length === 0) {
    console.log('ℹ️  No devices found. Run the seed script first:');
    console.log('   npm run seed-devices');
    return;
  }

  let successCount = 0;
  let errorCount = 0;
  const errors = [];

  for (const [index, device] of devices.entries()) {
    try {
      console.log(`⏳ Starting monitoring ${index + 1}/${devices.length}: ${device.name} (${device.ipAddress})`);
      
      const monitoring = await startMonitoring(device.uuid);
      
      console.log(`✅ Monitoring started: ${device.name} - Interval: ${monitoring.pingInterval}s`);
      successCount++;
      
      // Small delay to avoid overwhelming the API
      await new Promise(resolve => setTimeout(resolve, 200));
      
    } catch (error) {
      console.log(`❌ Failed: ${device.name} - ${error.message}`);
      errorCount++;
      errors.push({
        device: device.name,
        error: error.message
      });
    }
  }

  console.log('');
  console.log('📊 Monitoring Setup Summary:');
  console.log(`✅ Successfully started: ${successCount} monitors`);
  console.log(`❌ Failed to start: ${errorCount} monitors`);
  
  if (errors.length > 0) {
    console.log('');
    console.log('🚨 Errors:');
    errors.forEach(({ device, error }) => {
      console.log(`   • ${device}: ${error}`);
    });
  }

  console.log('');
  console.log('🎉 Monitoring setup completed!');
  
  if (successCount > 0) {
    console.log('');
    console.log('💡 What happens next:');
    console.log('   • Devices will be pinged every 5 seconds (default)');
    console.log('   • Visit http://localhost:3000/dashboard to see real-time status');
    console.log('   • Email alerts will be sent if devices become unreachable');
    console.log('   • Check the ping-service logs for monitoring activity');
  }
}

// Handle command line execution
if (require.main === module) {
  startMonitoringForAllDevices().catch(error => {
    console.error('💥 Monitoring setup failed:', error.message);
    process.exit(1);
  });
}

module.exports = { startMonitoringForAllDevices };