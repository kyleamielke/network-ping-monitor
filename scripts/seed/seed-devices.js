#!/usr/bin/env node

/**
 * Device Seeding Script
 * Seeds the database with devices for testing and demonstration
 * 
 * Usage:
 *   node seed-devices.js                    # Use default hardcoded devices
 *   node seed-devices.js devices.json       # Use devices from JSON file
 *   node seed-devices.js --file devices.json # Alternative syntax
 *   node seed-devices.js --delay 500        # Custom delay between requests (ms)
 *   node seed-devices.js devices.json --delay 100
 */

const axios = require('axios');
const fs = require('fs');
const path = require('path');

const API_BASE_URL = process.env.API_URL || 'http://localhost:8080';

// Real, publicly reachable devices
const SEED_DEVICES = [
  // Public DNS Servers
  {
    name: 'Google Primary DNS',
    ipAddress: '8.8.8.8',
    deviceType: 'Server',
    os: 'Linux',
    make: 'Google',
    endpointId: 'DNS-GOOGLE-1',
    assetTag: 'GOOGLE-DNS-001'
  },
  {
    name: 'Google Secondary DNS',
    ipAddress: '8.8.4.4',
    deviceType: 'Server',
    os: 'Linux',
    make: 'Google',
    endpointId: 'DNS-GOOGLE-2',
    assetTag: 'GOOGLE-DNS-002'
  },
  {
    name: 'Cloudflare Primary DNS',
    ipAddress: '1.1.1.1',
    deviceType: 'Server',
    os: 'Linux',
    make: 'Cloudflare',
    endpointId: 'DNS-CF-1',
    assetTag: 'CF-DNS-001'
  },
  {
    name: 'Cloudflare Secondary DNS',
    ipAddress: '1.0.0.1',
    deviceType: 'Server',
    os: 'Linux',
    make: 'Cloudflare',
    endpointId: 'DNS-CF-2',
    assetTag: 'CF-DNS-002'
  },
  {
    name: 'Quad9 DNS',
    ipAddress: '9.9.9.9',
    deviceType: 'Server',
    os: 'Linux',
    make: 'Quad9',
    endpointId: 'DNS-QUAD9-1',
    assetTag: 'Q9-DNS-001'
  },
  {
    name: 'OpenDNS Primary',
    ipAddress: '208.67.222.222',
    deviceType: 'Server',
    os: 'Linux',
    make: 'Cisco',
    endpointId: 'DNS-OPENDNS-1',
    assetTag: 'CISCO-DNS-001'
  },

  // NTP Servers
  {
    name: 'NIST Time Server',
    ipAddress: '129.6.15.28',
    deviceType: 'Server',
    os: 'Unix',
    make: 'NIST',
    model: 'Time Server',
    site: 'Boulder, CO',
    endpointId: 'NTP-NIST-1',
    assetTag: 'NIST-NTP-001'
  },
  {
    name: 'US Naval Observatory',
    ipAddress: '192.5.41.40',
    deviceType: 'Server',
    os: 'Unix',
    make: 'US Navy',
    model: 'Time Server',
    site: 'Washington, DC',
    endpointId: 'NTP-USNO-1',
    assetTag: 'USNO-NTP-001'
  },
  {
    name: 'Pool NTP Primary',
    ipAddress: '162.159.200.1',
    deviceType: 'Server',
    os: 'Linux',
    make: 'NTP Pool',
    model: 'Time Server',
    site: 'Global',
    endpointId: 'NTP-POOL-1',
    assetTag: 'POOL-NTP-001'
  },

  // Root Name Servers
  {
    name: 'A Root Name Server',
    ipAddress: '198.41.0.4',
    deviceType: 'Server',
    os: 'Linux',
    make: 'Verisign',
    model: 'Root Server',
    site: 'Dulles, VA',
    endpointId: 'ROOT-A',
    assetTag: 'VERISIGN-ROOT-001'
  },
  {
    name: 'B Root Name Server',
    ipAddress: '199.9.14.201',
    deviceType: 'Server',
    os: 'Linux',
    make: 'USC-ISI',
    model: 'Root Server',
    site: 'Los Angeles, CA',
    endpointId: 'ROOT-B',
    assetTag: 'USC-ROOT-001'
  },
  {
    name: 'C Root Name Server',
    ipAddress: '192.33.4.12',
    deviceType: 'Server',
    os: 'Linux',
    make: 'Cogent',
    model: 'Root Server',
    site: 'Herndon, VA',
    endpointId: 'ROOT-C',
    assetTag: 'COGENT-ROOT-001'
  },

  // Other Infrastructure
  {
    name: 'Level3 DNS',
    ipAddress: '4.2.2.1',
    deviceType: 'Server',
    os: 'Linux',
    make: 'Level3',
    site: 'Broomfield, CO',
    endpointId: 'DNS-LEVEL3-1',
    assetTag: 'L3-DNS-001'
  },
  {
    name: 'Comodo Secure DNS',
    ipAddress: '8.26.56.26',
    deviceType: 'Server',
    os: 'Linux',
    make: 'Comodo',
    site: 'Global',
    endpointId: 'DNS-COMODO-1',
    assetTag: 'COMODO-DNS-001'
  },
  {
    name: 'Yandex DNS',
    ipAddress: '77.88.8.8',
    deviceType: 'Server',
    os: 'Linux',
    make: 'Yandex',
    site: 'Moscow, Russia',
    endpointId: 'DNS-YANDEX-1',
    assetTag: 'YANDEX-DNS-001'
  },
  {
    name: 'AdGuard DNS',
    ipAddress: '94.140.14.14',
    deviceType: 'Server',
    os: 'Linux',
    make: 'AdGuard',
    site: 'Cyprus',
    endpointId: 'DNS-ADGUARD-1',
    assetTag: 'ADGUARD-DNS-001'
  },
  {
    name: 'FreeDNS',
    ipAddress: '37.235.1.174',
    deviceType: 'Server',
    os: 'Linux',
    make: 'FreeDNS',
    site: 'Austria',
    endpointId: 'DNS-FREE-1',
    assetTag: 'FREE-DNS-001'
  },
  {
    name: 'Alternate DNS',
    ipAddress: '76.76.19.19',
    deviceType: 'Server',
    os: 'Linux',
    make: 'Alternate DNS',
    site: 'Global',
    endpointId: 'DNS-ALT-1',
    assetTag: 'ALT-DNS-001'
  },
  {
    name: 'CleanBrowsing DNS',
    ipAddress: '185.228.168.9',
    deviceType: 'Server',
    os: 'Linux',
    make: 'CleanBrowsing',
    site: 'Global',
    endpointId: 'DNS-CLEAN-1',
    assetTag: 'CLEAN-DNS-001'
  }
];

const CREATE_DEVICE_MUTATION = `
  mutation CreateDevice($input: CreateDeviceInput!) {
    createDevice(input: $input) {
      id
      name
      ipAddress
      type
      createdAt
    }
  }
`;

const GET_ALL_DEVICES_QUERY = `
  query GetAllDevices {
    devices {
      id
      name
      ipAddress
    }
  }
`;

async function getAllDevices() {
  try {
    const response = await axios.post(`${API_BASE_URL}/graphql`, {
      query: GET_ALL_DEVICES_QUERY
    }, {
      headers: {
        'Content-Type': 'application/json'
      },
      timeout: 10000
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

async function createDevice(device) {
  try {
    // Map deviceType to type for GraphQL compatibility
    const deviceInput = { ...device };
    if (deviceInput.deviceType) {
      deviceInput.type = deviceInput.deviceType;
      delete deviceInput.deviceType;
    }
    
    // Remove site field if it's not a valid UUID
    if (deviceInput.site && !deviceInput.site.match(/^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i)) {
      delete deviceInput.site;
    }
    
    const response = await axios.post(`${API_BASE_URL}/graphql`, {
      query: CREATE_DEVICE_MUTATION,
      variables: {
        input: deviceInput
      }
    }, {
      headers: {
        'Content-Type': 'application/json'
      },
      timeout: 10000
    });

    if (response.data.errors) {
      throw new Error(response.data.errors[0].message);
    }

    return response.data.data.createDevice;
  } catch (error) {
    if (error.response?.data?.errors) {
      throw new Error(error.response.data.errors[0].message);
    }
    throw error;
  }
}

/**
 * Parse command line arguments
 */
function parseArgs() {
  const args = process.argv.slice(2);
  const options = {
    file: null,
    delay: 200,
    help: false
  };

  for (let i = 0; i < args.length; i++) {
    const arg = args[i];
    
    if (arg === '--help' || arg === '-h') {
      options.help = true;
    } else if (arg === '--file' || arg === '-f') {
      options.file = args[++i];
    } else if (arg === '--delay' || arg === '-d') {
      options.delay = parseInt(args[++i]) || 200;
    } else if (!arg.startsWith('-')) {
      // Assume it's a file path if it doesn't start with -
      options.file = arg;
    }
  }

  return options;
}

/**
 * Load devices from JSON file
 */
function loadDevicesFromFile(filePath) {
  try {
    const absolutePath = path.resolve(filePath);
    console.log(`📂 Loading devices from: ${absolutePath}`);
    
    if (!fs.existsSync(absolutePath)) {
      throw new Error(`File not found: ${absolutePath}`);
    }

    const fileContent = fs.readFileSync(absolutePath, 'utf8');
    const data = JSON.parse(fileContent);
    
    // Support both array directly or object with devices property
    const devices = Array.isArray(data) ? data : data.devices;
    
    if (!Array.isArray(devices)) {
      throw new Error('JSON file must contain an array of devices or an object with a "devices" property');
    }

    console.log(`✅ Loaded ${devices.length} devices from file`);
    return devices;
  } catch (error) {
    if (error.code === 'ENOENT') {
      throw new Error(`File not found: ${filePath}`);
    } else if (error instanceof SyntaxError) {
      throw new Error(`Invalid JSON in file: ${error.message}`);
    }
    throw error;
  }
}

/**
 * Display usage help
 */
function showHelp() {
  console.log(`
📡 Device Seeding Script
Seeds the database with network devices for testing and demonstration

Usage:
  node seed-devices.js [options] [file]

Options:
  --file, -f <path>   Path to JSON file containing devices
  --delay, -d <ms>    Delay between API requests in milliseconds (default: 200)
  --help, -h          Show this help message

Examples:
  node seed-devices.js                        # Use default hardcoded devices
  node seed-devices.js devices.json           # Load devices from JSON file
  node seed-devices.js -f custom.json -d 100  # Custom file with 100ms delay
  
JSON File Format:
  [
    {
      "name": "Device Name",
      "ipAddress": "192.168.1.1",
      "hostname": "device.example.com",
      "type": "Server",
      "os": "Linux",
      "make": "Dell",
      "model": "PowerEdge R740",
      "endpointId": "DEVICE-001",
      "assetTag": "IT-2024-001",
      "description": "Main server"
    }
  ]

Or with wrapper object:
  {
    "devices": [...]
  }

Note: osType field has been removed. Use only the 'os' field.
Site and location fields are temporarily disabled.
  `);
}

async function seedDevices(devicesToSeed, delay = 200) {
  console.log('🌱 Starting device seeding...');
  console.log(`📡 API URL: ${API_BASE_URL}`);
  console.log(`📋 Devices to create: ${devicesToSeed.length}`);
  console.log(`⏱️  Delay between requests: ${delay}ms`);
  console.log('');

  // Get existing devices to avoid duplicates
  console.log('📋 Checking existing devices...');
  let existingDevices = [];
  try {
    existingDevices = await getAllDevices();
    console.log(`Found ${existingDevices.length} existing devices`);
  } catch (error) {
    console.log(`⚠️  Could not fetch existing devices: ${error.message}`);
    console.log('Proceeding anyway...');
  }
  console.log('');

  const existingIPs = new Set(existingDevices.map(d => d.ipAddress));
  const devicesToCreate = devicesToSeed.filter(d => !existingIPs.has(d.ipAddress));
  const skippedCount = devicesToSeed.length - devicesToCreate.length;

  if (skippedCount > 0) {
    console.log(`⏭️  Skipping ${skippedCount} devices that already exist`);
  }
  console.log(`📋 Creating ${devicesToCreate.length} new devices`);
  console.log('');

  let successCount = 0;
  let errorCount = 0;
  const errors = [];

  for (const [index, device] of devicesToCreate.entries()) {
    try {
      console.log(`⏳ Creating device ${index + 1}/${devicesToCreate.length}: ${device.name} (${device.ipAddress})`);
      
      const created = await createDevice(device);
      
      console.log(`✅ Created: ${created.name} - ID: ${created.id}`);
      successCount++;
      
      // Configurable delay to avoid overwhelming the API
      if (delay > 0 && index < devicesToCreate.length - 1) {
        await new Promise(resolve => setTimeout(resolve, delay));
      }
      
    } catch (error) {
      console.log(`❌ Failed: ${device.name} - ${error.message}`);
      errorCount++;
      errors.push({
        device: device.name,
        error: error.message
      });
      
      // Half delay even on error
      if (delay > 0 && index < devicesToCreate.length - 1) {
        await new Promise(resolve => setTimeout(resolve, Math.floor(delay / 2)));
      }
    }
  }

  console.log('');
  console.log('📊 Seeding Summary:');
  console.log(`✅ Successfully created: ${successCount} devices`);
  console.log(`⏭️  Skipped (already exist): ${skippedCount} devices`);
  console.log(`❌ Failed to create: ${errorCount} devices`);
  console.log(`📋 Total processed: ${devicesToSeed.length} devices`);
  
  if (errors.length > 0) {
    console.log('');
    console.log('🚨 Errors:');
    errors.forEach(({ device, error }) => {
      console.log(`   • ${device}: ${error}`);
    });
  }

  console.log('');
  console.log('🎉 Device seeding completed!');
  
  if (successCount > 0) {
    console.log('');
    console.log('💡 Next steps:');
    console.log('   • Visit http://localhost:3000/devices to see the devices');
    console.log('   • Use the global search to find specific devices');
    console.log('   • Start ping monitoring for devices you want to monitor');
  }
}

// Handle command line execution
if (require.main === module) {
  const options = parseArgs();
  
  if (options.help) {
    showHelp();
    process.exit(0);
  }
  
  // Load devices from file or use default
  let devices = SEED_DEVICES;
  if (options.file) {
    try {
      devices = loadDevicesFromFile(options.file);
    } catch (error) {
      console.error('💥 Error loading devices from file:', error.message);
      process.exit(1);
    }
  }
  
  // Run seeding with the loaded devices
  seedDevices(devices, options.delay).catch(error => {
    console.error('💥 Seeding failed:', error.message);
    process.exit(1);
  });
}

module.exports = { seedDevices, SEED_DEVICES };