package io.thatworked.support.alert.api.dto.request;

/**
 * Alert type for API requests.
 * Maps to domain AlertType but kept separate to maintain layer independence.
 */
public enum AlertType {
    DEVICE_DOWN,
    DEVICE_RECOVERED,
    HIGH_RESPONSE_TIME,
    PACKET_LOSS,
    CUSTOM
}