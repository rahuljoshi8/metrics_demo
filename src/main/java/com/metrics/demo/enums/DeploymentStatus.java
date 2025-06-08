package com.metrics.demo.enums;

/**
 * Enumeration of possible deployment statuses from Jenkins.
 *
 * These statuses represent the outcome of a deployment:
 * - SUCCESS: Deployment completed successfully
 * - FAILURE: Deployment failed
 * - ABORTED: Deployment was manually aborted
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
     * Deployment was manually aborted.
     * These deployments are typically excluded from CFR calculations.
     */
    ABORTED,

    /**
     * Deployment was cancelled.
     * These deployments are typically excluded from CFR calculations.
     */
    CANCELLED;
}