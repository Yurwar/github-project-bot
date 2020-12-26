package edu.kpi.service.integration.impl;

import edu.kpi.JwtUtils;
import edu.kpi.dto.issue.CommentDto;
import edu.kpi.dto.issue.LabelsDto;
import edu.kpi.model.IssueEvent;
import edu.kpi.service.integration.IssueIntegrationService;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@Service
public class GitHubIssueIntegrationService extends CommonGitHubIntegrationService implements IssueIntegrationService {

    private static final String OWNER_PLACEHOLDER = "{owner}";
    private static final String REPO_PLACEHOLDER = "{repo}";
    private static final String ISSUE_NUMBER_PLACEHOLDER = "{issueNumber}";

    @Value("${github.api.issue.label.url}")
    private String githubIssueLabelIntegrationUrl;

    @Value("${github.api.issue.comment.url}")
    private String githubIssueCommentIntegrationUrl;

    public GitHubIssueIntegrationService(final JwtUtils jwtUtils) {

        super(jwtUtils);
    }

    @Override
    public Mono<Void> addLabelsForIssue(final IssueEvent data) {

        return WebClient.create(getServiceUrl(githubIssueLabelIntegrationUrl, data))
                .post()
                .body(BodyInserters.fromValue(LabelsDto.builder().labels(data.getLabels()).build()))
                .headers(headers -> getHeaders(data.getInstallationId()).forEach(headers::add))
                .exchangeToMono(resp -> Mono.empty());
    }

    @Override
    public Mono<Void> addCommentForIssue(IssueEvent data) {

        return WebClient.create(getServiceUrl(githubIssueCommentIntegrationUrl, data))
                .post()
                .body(BodyInserters.fromValue(CommentDto.builder().body(data.getComment()).build()))
                .headers(headers -> getHeaders(data.getInstallationId()).forEach(headers::add))
                .exchangeToMono(resp -> Mono.empty());
    }

    private String getServiceUrl(final String templateUrl, final IssueEvent data) {

        return Optional.ofNullable(templateUrl)
                .orElse(StringUtil.EMPTY_STRING)
                .replace(OWNER_PLACEHOLDER, data.getOwner())
                .replace(REPO_PLACEHOLDER, data.getRepo())
                .replace(ISSUE_NUMBER_PLACEHOLDER, data.getIssueNumber());
    }
}
