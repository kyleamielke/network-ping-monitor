package io.thatworked.support.alert.domain.model;

/**
 * Domain enum representing different types of alerts in the system.
 */
public enum AlertType {
    DEVICE_DOWN("Device is not responding to ping requests"),
    DEVICE_RECOVERED("Device has recovered and is responding normally"),
    HIGH_RESPONSE_TIME("Device response time exceeds threshold"),
    PACKET_LOSS("Device experiencing packet loss"),
    CUSTOM("Custom alert type");

    private final String description;

    AlertType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRecoveryType() {
        return this == DEVICE_RECOVERED;
    }

    public boolean isFailureType() {
        return this == DEVICE_DOWN || this == HIGH_RESPONSE_TIME || this == PACKET_LOSS;
    }
}