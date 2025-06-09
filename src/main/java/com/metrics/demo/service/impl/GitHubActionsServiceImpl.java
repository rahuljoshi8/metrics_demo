package com.metrics.demo.service.impl;


import com.metrics.demo.dto.external.GitHubWorkflowRun;
import com.metrics.demo.entity.Deployment;
import com.metrics.demo.enums.DeploymentStatus;
import com.metrics.demo.repository.DeploymentRepository;
import com.metrics.demo.service.GitHubActionsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of GitHubActionsService.
 *
 * Handles integration with GitHub Actions API to fetch workflow run data
 * and synchronize it with the local database as deployment data.
 *
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GitHubActionsServiceImpl implements GitHubActionsService {

    private final DeploymentRepository deploymentRepository;
    private final WebClient.Builder webClientBuilder;


    @Value("${github.api.token}")
    private String apiToken;

    @Value("${github.api.url}")
    private String baseUrl;

    @Value("${github.repository.owner}")
    private String repositoryOwner;

    @Value("${github.repository.name}")
    private String repositoryName;

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;


    @Override
    public List<GitHubWorkflowRun> fetchWorkflowRuns(LocalDateTime since, LocalDateTime until) {
        return fetchWorkflowRunsForRepository(repositoryOwner, repositoryName, since, until);
    }

    @Override
    public List<GitHubWorkflowRun> fetchWorkflowRunsForRepository(String owner, String repo,
                                                                  LocalDateTime since, LocalDateTime until) {
        log.info("Fetching GitHub workflow runs for {}/{} from {} to {}", owner, repo, since, until);

        try {
            WebClient webClient = webClientBuilder
                    .baseUrl(baseUrl)
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiToken)
                    .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
                    .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
                    .build();

            Map<String, Object> response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/repos/{owner}/{repo}/actions/runs")
                            .queryParam("created", since.format(ISO_FORMATTER) + ".." + until.format(ISO_FORMATTER))
                            .queryParam("per_page", 100)
                            .build(owner, repo))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> workflowRuns = (List<Map<String, Object>>) response.get("workflow_runs");

            List<GitHubWorkflowRun> result = new ArrayList<>();
            if (workflowRuns != null) {
                for (Map<String, Object> runData : workflowRuns) {
                    result.add(convertMapToWorkflowRun(runData));
                }
            }

            log.info("Fetched {} workflow runs from GitHub for {}/{}", result.size(), owner, repo);
            return result;

        } catch (WebClientResponseException e) {
            log.error("Error fetching workflow runs from GitHub: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to fetch workflow runs from GitHub", e);
        } catch (Exception e) {
            log.error("Unexpected error fetching workflow runs from GitHub", e);
            throw new RuntimeException("Failed to fetch workflow runs from GitHub", e);
        }
    }

    @Override
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    @Transactional
    public void syncDeployments() {
        log.info("Starting scheduled deployment synchronization");

        try {
            // Fetch workflow runs from the last 24 hours
            LocalDateTime until = LocalDateTime.now();
            LocalDateTime since = until.minusHours(24);

            List<GitHubWorkflowRun> workflowRuns = fetchWorkflowRuns(since, until);

            for (GitHubWorkflowRun workflowRun : workflowRuns) {
                syncDeployment(workflowRun);
            }

            log.info("Completed deployment synchronization, processed {} workflow runs", workflowRuns.size());

        } catch (Exception e) {
            log.error("Error during scheduled deployment synchronization", e);
        }
    }

    @Override
    public Deployment convertToEntity(GitHubWorkflowRun workflowRun) {
        return Deployment.builder()
                .deploymentId("gh-" + workflowRun.getId())
                .timestamp(parseDateTime(workflowRun.getCreatedAt()))
                .status(parseDeploymentStatus(workflowRun.getConclusion()))
                .applicationName(workflowRun.getRepository() != null ?
                        workflowRun.getRepository().getName() : repositoryName)
                .version(workflowRun.getHeadCommit() != null ?
                        workflowRun.getHeadCommit().getId() : null)
                .workflowRunId(workflowRun.getId())
                .repositoryName(workflowRun.getRepository() != null ?
                        workflowRun.getRepository().getName() : null)
                .workflowName(workflowRun.getName())
                .build();
    }


    @Override
    public boolean isHealthy() {
        try {
            WebClient webClient = webClientBuilder
                    .baseUrl(baseUrl)
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiToken)
                    .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
                    .build();

            webClient.get()
                    .uri("/repos/{owner}/{repo}/actions/runs?per_page=1", repositoryOwner, repositoryName)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return true;
        } catch (Exception e) {
            log.warn("GitHub Actions health check failed", e);
            return false;
        }
    }

    private void syncDeployment(GitHubWorkflowRun workflowRun) {
        String deploymentId = "gh-" + workflowRun.getId();
        Optional<Deployment> existing = deploymentRepository.findByDeploymentId(deploymentId);

        if (existing.isPresent()) {
            // Update existing deployment
            Deployment deployment = existing.get();
            deployment.setStatus(parseDeploymentStatus(workflowRun.getConclusion()));
            // Update other fields as needed

            deploymentRepository.save(deployment);
            log.debug("Updated existing deployment: {}", deployment.getDeploymentId());
        } else {
            // Create new deployment
            Deployment newDeployment = convertToEntity(workflowRun);
            deploymentRepository.save(newDeployment);
            log.debug("Created new deployment: {}", newDeployment.getDeploymentId());
        }
    }

    private GitHubWorkflowRun convertMapToWorkflowRun(Map<String, Object> runData) {

        @SuppressWarnings("unchecked")
        Map<String, Object> repository = (Map<String, Object>) runData.get("repository");

        GitHubWorkflowRun.GitHubRepository githubRepository = repository != null ?
                GitHubWorkflowRun.GitHubRepository.builder()
                        .id(((Number) repository.get("id")).longValue())
                        .name((String) repository.get("name"))
                        .fullName((String) repository.get("full_name"))
                        .build() : null;

        @SuppressWarnings("unchecked")
        Map<String, Object> headCommit = (Map<String, Object>) runData.get("head_commit");

        GitHubWorkflowRun.GitHubCommit commit = headCommit != null ?
                GitHubWorkflowRun.GitHubCommit.builder()
                        .id((String) headCommit.get("id"))
                        .message((String) headCommit.get("message"))
                        .timestamp((String) headCommit.get("timestamp"))
                        .build() : null;

        return GitHubWorkflowRun.builder()
                .id(((Number) runData.get("id")).longValue())
                .name((String) runData.get("name"))
                .status((String) runData.get("status"))
                .conclusion((String) runData.get("conclusion"))
                .htmlUrl((String) runData.get("html_url"))
                .createdAt((String) runData.get("created_at"))
                .updatedAt((String) runData.get("updated_at"))
                .runStartedAt((String) runData.get("run_started_at"))
                .headBranch((String) runData.get("head_branch"))
                .repository(githubRepository)
                .headCommit(commit)
                .event((String) runData.get("event"))
                .build();
    }

    private DeploymentStatus parseDeploymentStatus(String conclusion) {
        if (conclusion == null) return DeploymentStatus.SUCCESS; // Running workflows

        return switch (conclusion.toLowerCase()) {
            case "success" -> DeploymentStatus.SUCCESS;
            case "failure" -> DeploymentStatus.FAILURE;
            case "cancelled" -> DeploymentStatus.CANCELLED;
            default -> DeploymentStatus.FAILURE;
        };
    }


    private LocalDateTime parseDateTime(String dateTimeString) {
        if (dateTimeString == null) return LocalDateTime.now();

        try {
            // Remove 'Z' suffix and parse
            String cleanDateTime = dateTimeString.replace("Z", "");
            return LocalDateTime.parse(cleanDateTime, ISO_FORMATTER);
        } catch (Exception e) {
            log.warn("Failed to parse datetime: {}, using current time", dateTimeString);
            return LocalDateTime.now();
        }
    }
}
