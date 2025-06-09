package com.metrics.demo.repository;


import com.metrics.demo.entity.Deployment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository interface for Deployment entity operations.
 *
 * Provides methods for querying deployments needed for metrics calculations,
 * particularly for Change Failure Rate calculation.
 *
 *
 */
@Repository
public interface DeploymentRepository extends JpaRepository<Deployment, Long> {

    /**
     * Finds a deployment by its unique deployment ID.
     *
     * @param deploymentId the unique deployment identifier
     * @return Optional containing the deployment if found
     */
    Optional<Deployment> findByDeploymentId(String deploymentId);

    /**
     * Counts total deployments within a date range.
     * Used for Change Failure Rate calculation.
     *
     * @param startDate start of the date range (inclusive)
     * @param endDate end of the date range (inclusive)
     * @return count of deployments in the date range
     */
    long countByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate);

}