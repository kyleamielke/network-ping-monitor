package io.thatworked.support.gateway.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Relay-compliant Edge type containing a node and its cursor.
 * @param <T> The type of the node
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Edge<T> {
    private T node;
    private String cursor;
}