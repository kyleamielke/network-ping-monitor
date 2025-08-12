package io.thatworked.support.gateway.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Relay-compliant Connection type for cursor-based pagination.
 * @param <T> The type of nodes in the connection
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Connection<T> {
    private List<Edge<T>> edges;
    private PageInfo pageInfo;
    private Long totalCount;
}