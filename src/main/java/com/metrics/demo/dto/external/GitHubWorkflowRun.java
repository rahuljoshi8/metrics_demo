package com.metrics.demo.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for GitHub Actions workflow run data from API responses.
 *
 * Maps to the structure returned by GitHub's REST API
 * for workflow run objects.
 *
 * @author Technical Lead Assignment
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubWorkflowRun {

    /**
     * Unique workflow run identifier.
     */
    @JsonProperty("id")
    private Long id;

    /**
     * Name of the workflow.
     */
    @JsonProperty("name")
    private String name;

    /**
     * Current status of the workflow run.
     */
    @JsonProperty("status")
    private String status;

    /**
     * Conclusion of the workflow run (success, failure, cancelled, etc.).
     */
    @JsonProperty("conclusion")
    private String conclusion;

    /**
     * URL to the workflow run.
     */
    @JsonProperty("html_url")
    private String htmlUrl;

    /**
     * When the workflow run was created.
     */
    @JsonProperty("created_at")
    private String createdAt;

    /**
     * When the workflow run was updated.
     */
    @JsonProperty("updated_at")
    private String updatedAt;

    /**
     * When the workflow run started.
     */
    @JsonProperty("run_started_at")
    private String runStartedAt;

    /**
     * Repository information.
     */
    @JsonProperty("repository")
    private GitHubRepository repository;

    /**
     * Head commit information.
     */
    @JsonProperty("head_commit")
    private GitHubCommit headCommit;

    /**
     * Head branch name.
     */
    @JsonProperty("head_branch")
    private String headBranch;

    /**
     * Event that triggered the workflow.
     */
    @JsonProperty("event")
    private String event;

    /**
     * Nested class for GitHub repository information.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GitHubRepository {
        @JsonProperty("id")
        private Long id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("full_name")
        private String fullName;

        @JsonProperty("owner")
        private Owner owner;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Owner {
            @JsonProperty("login")
            private String login;

            @JsonProperty("type")
            private String type;
        }
    }

    /**
     * Nested class for commit information.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GitHubCommit {
        @JsonProperty("id")
        private String id;

        @JsonProperty("message")
        private String message;

        @JsonProperty("timestamp")
        private String timestamp;

        @JsonProperty("author")
        private Author author;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Author {
            @JsonProperty("name")
            private String name;

            @JsonProperty("email")
            private String email;
        }
    }
}