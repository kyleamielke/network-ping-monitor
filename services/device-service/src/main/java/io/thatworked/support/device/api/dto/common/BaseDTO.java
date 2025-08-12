package io.thatworked.support.device.api.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseDTO {
    private UUID id;
    private Long version;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String lastModifiedBy;
}