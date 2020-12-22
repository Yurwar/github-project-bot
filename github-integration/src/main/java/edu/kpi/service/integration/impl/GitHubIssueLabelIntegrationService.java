package edu.kpi.service.integration.impl;

import edu.kpi.JwtUtils;
import edu.kpi.dto.IssueLabelDto;
import edu.kpi.dto.LabelsDto;
import edu.kpi.service.integration.IssueLabelIntegrationService;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@Service
public class GitHubIssueLabelIntegrationService extends CommonGitHubIntegrationService implements IssueLabelIntegrationService {

    private static final String OWNER_PLACEHOLDER = "{owner}";
    private static final String REPO_PLACEHOLDER = "{repo}";
    private static final String ISSUE_NUMBER_PLACEHOLDER = "{issueNumber}";

    @Value("${github.api.issue.label.url}")
    private String githubIssueLabelIntegrationUrl;

    public GitHubIssueLabelIntegrationService(final JwtUtils jwtUtils, final Environment environment) {

        super(jwtUtils);
    }

    @Override
    public Mono<Void> addLabelsForIssue(final IssueLabelDto data) {

        return WebClient.create(getServiceUrl(data))
                .post()
                .body(BodyInserters.fromValue(LabelsDto.builder().labels(data.getLabels()).build()))
                .headers(headers -> getHeaders(data.getInstallationId()).forEach(headers::add))
                .exchangeToMono(resp -> Mono.empty());
    }

    private String getServiceUrl(final IssueLabelDto data) {

        return Optional.ofNullable(githubIssueLabelIntegrationUrl)
                .orElse(StringUtil.EMPTY_STRING)
                .replace(OWNER_PLACEHOLDER, data.getOwner())
                .replace(REPO_PLACEHOLDER, data.getRepo())
                .replace(ISSUE_NUMBER_PLACEHOLDER, data.getIssueNumber());
    }
}
