package io.thatworked.support.ping.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(name = "ping_results")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(PingResultId.class)
public class PingResult {
    @Id
    @Column(name = "time")
    private Instant time;

    @Id
    @Column(name = "device_id", nullable = false)
    private UUID deviceId;

    @Column(name = "round_trip_time")
    private Double roundTripTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PingStatus status;
}