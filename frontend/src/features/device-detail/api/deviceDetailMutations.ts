// Re-export device mutations that are used in device detail
export { 
  DELETE_DEVICE, 
  START_PING_MONITORING as START_MONITORING,
  STOP_PING_MONITORING as STOP_MONITORING 
} from '@/features/devices/api/deviceMutations';