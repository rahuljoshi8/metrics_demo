package com.metrics.demo.repository;

import com.metrics.demo.entity.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Incident entity operations.
 *
 * Provides methods for querying incidents needed for metrics calculations,
 * particularly for Change Failure Rate and Mean Time to Recovery.
 *
 *
 */
@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {

    /**
     * Finds an incident by its PagerDuty incident ID.
     *
     * @param incidentId the PagerDuty incident ID
     * @return Optional containing the incident if found
     */
    Optional<Incident> findByIncidentId(String incidentId);

    /**
     * Counts total incidents created within a date range.
     * Used for Change Failure Rate calculation.
     *
     * @param startDate start of the date range (inclusive)
     * @param endDate end of the date range (inclusive)
     * @return count of incidents in the date range
     */
    long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Finds all resolved incidents within a date range.
     * Used for Mean Time to Recovery calculation.
     *
     * @param startDate start of the date range (inclusive)
     * @param endDate end of the date range (inclusive)
     * @return list of resolved incidents in the date range
     */
    @Query("SELECT i FROM Incident i WHERE i.createdAt >= :startDate AND i.createdAt <= :endDate AND i.status = 'RESOLVED'")
    List<Incident> findResolvedIncidentsBetween(@Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate);



}