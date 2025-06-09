package com.metrics.demo.service;


import com.metrics.demo.dto.external.GitHubWorkflowRun;
import com.metrics.demo.entity.Deployment;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for GitHub Actions integration.
 *
 * Provides methods to fetch workflow run data from GitHub Actions API
 * and synchronize it with the local database as deployment data.
 *
 *
 */
public interface GitHubActionsService {

    /**
     * Fetches workflow runs from GitHub Actions API within a date range.
     *
     * @param since start date for workflow run retrieval
     * @param until end date for workflow run retrieval
     * @return list of workflow runs from GitHub Actions API
     */
    List<GitHubWorkflowRun> fetchWorkflowRuns(LocalDateTime since, LocalDateTime until);

    /**
     * Synchronizes deployments from GitHub Actions to local database.
     * This method fetches recent workflow runs and updates the database.
     */
    void syncDeployments();

    /**
     * Converts GitHub workflow run DTO to internal deployment entity.
     *
     * @param workflowRun the external DTO
     * @return converted internal entity
     */
    Deployment convertToEntity(GitHubWorkflowRun workflowRun);



    /**
     * Fetches workflow runs for a specific repository.
     *
     * @param owner repository owner
     * @param repo repository name
     * @param since start date for retrieval
     * @param until end date for retrieval
     * @return list of workflow runs for the repository
     */
    List<GitHubWorkflowRun> fetchWorkflowRunsForRepository(String owner, String repo,
                                                           LocalDateTime since, LocalDateTime until);

    /**
     * Checks the health of GitHub Actions API connection.
     *
     * @return true if API is accessible, false otherwise
     */
    boolean isHealthy();
}