package com.metrics.demo.repository;


import com.metrics.demo.entity.Deployment;
import com.metrics.demo.enums.DeploymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Deployment entity operations.
 *
 * Provides methods for querying deployments needed for metrics calculations,
 * particularly for Change Failure Rate calculation.
 *
 * @author Technical Lead Assignment
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

    /**
     * Counts deployments by status within a date range.
     *
     * @param status the deployment status to filter by
     * @param startDate start of the date range (inclusive)
     * @param endDate end of the date range (inclusive)
     * @return count of deployments matching the criteria
     */
    long countByStatusAndTimestampBetween(DeploymentStatus status,
                                          LocalDateTime startDate,
                                          LocalDateTime endDate);

    /**
     * Finds deployments by status within a date range.
     *
     * @param status the deployment status to filter by
     * @param startDate start of the date range (inclusive)
     * @param endDate end of the date range (inclusive)
     * @return list of deployments matching the criteria
     */
    List<Deployment> findByStatusAndTimestampBetween(DeploymentStatus status,
                                                     LocalDateTime startDate,
                                                     LocalDateTime endDate);

//    /**
//     * Finds deployments by environment within a date range.
//     *
//     * @param startDate start of the date range (inclusive)
//     * @param endDate end of the date range (inclusive)
//     * @return list of deployments in the specified environment
//     */
//    List<Deployment> findByEnvironmentAndTimestampBetween(String environment,
//                                                          LocalDateTime startDate,
//                                                          LocalDateTime endDate);

    /**
     * Finds the most recent deployments, limited by count.
     * Used for dashboard display.
     *
     * @param limit maximum number of deployments to return
     * @return list of recent deployments ordered by timestamp descending
     */
    @Query("SELECT d FROM Deployment d ORDER BY d.timestamp DESC")
    List<Deployment> findTopDeployments(@Param("limit") int limit);

    /**
     * Finds deployments by workflow run ID from GitHub Actions.
     *
     * @param workflowRunId the GitHub Actions workflow run ID
     * @return Optional containing the deployment if found
     */
    Optional<Deployment> findByWorkflowRunId(Long workflowRunId);
}