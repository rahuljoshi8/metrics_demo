package com.metrics.demo.enums;

/**
 * Enumeration of possible deployment statuses from Jenkins.
 *
 * These statuses represent the outcome of a deployment:
 * - SUCCESS: Deployment completed successfully
 * - FAILURE: Deployment failed
 * - CANCELLED: Deployment was manually CANCELLED
 *
 *
 */
public enum DeploymentStatus {

    /**
     * Deployment completed successfully.
     * These deployments are counted in the total for CFR calculation.
     */
    SUCCESS,

    /**
     * Deployment failed.
     * These deployments are counted in the total for CFR calculation.
     */
    FAILURE,

    /**
     * Deployment was cancelled.
     * These deployments are typically excluded from CFR calculations.
     */
    CANCELLED;
}