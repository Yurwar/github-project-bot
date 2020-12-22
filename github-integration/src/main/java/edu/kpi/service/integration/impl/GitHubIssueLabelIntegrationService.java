package edu.kpi.service.integration.impl;

import edu.kpi.JwtUtils;
import edu.kpi.dto.IssueLabelDto;
import edu.kpi.dto.LabelsDto;
import edu.kpi.service.integration.IssueLabelIntegrationService;
import io.netty.util.internal.StringUtil;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GitHubIssueLabelIntegrationService extends CommonGitHubIntegrationService implements IssueLabelIntegrationService {

    private static final String GITHUB_ISSUE_LABEL_INTEGRATION_URL_KEY = "github.api.issue.label.url";
    private static final String OWNER_PLACEHOLDER = "{owner}";
    private static final String REPO_PLACEHOLDER = "{repo}";
    private static final String ISSUE_NUMBER_PLACEHOLDER = "{issueNumber}";

    public GitHubIssueLabelIntegrationService(final JwtUtils jwtUtils, final Environment environment) {

        super(jwtUtils, environment);
    }

    @Override
    public void addLabelsForIssue(final IssueLabelDto data) {

        HttpHeaders httpHeaders = new HttpHeaders();

        getHeaders(data.getInstallationId()).forEach(httpHeaders::add);

        HttpEntity<LabelsDto> request = new HttpEntity<>(LabelsDto.builder().labels(data.getLabels()).build(), httpHeaders);

        getRestTemplate().postForLocation(getServiceUrl(data), request);
    }

    private String getServiceUrl(final IssueLabelDto data) {

        return Optional.ofNullable(getEnvironment().getProperty(GITHUB_ISSUE_LABEL_INTEGRATION_URL_KEY))
                .orElse(StringUtil.EMPTY_STRING)
                .replace(OWNER_PLACEHOLDER, data.getOwner())
                .replace(REPO_PLACEHOLDER, data.getRepo())
                .replace(ISSUE_NUMBER_PLACEHOLDER, data.getIssueNumber());
    }
}
