package io.thatworked.support.ping.infrastructure.repository.jpa;

import io.thatworked.support.ping.domain.AlertState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AlertStateRepository extends JpaRepository<AlertState, UUID> {
    
    List<AlertState> findByIsAlertingTrue();
}