package edu.kpi.configuration;

import edu.kpi.service.processing.EventProcessingService;
import edu.kpi.service.processing.impl.IssueCommentEventProcessingService;
import edu.kpi.service.processing.impl.IssueEventProcessingService;
import edu.kpi.service.processing.impl.PullRequestEventProcessingService;
import edu.kpi.service.processing.impl.ReleaseEventProcessingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class EventProcessingConfiguration {

    private final IssueEventProcessingService issueEventProcessingService;
    private final PullRequestEventProcessingService pullRequestEventProcessingService;
    private final ReleaseEventProcessingService releaseEventProcessingService;
    private final IssueCommentEventProcessingService issueCommentEventProcessingService;

    public EventProcessingConfiguration(final IssueEventProcessingService issueEventProcessingService,
                                        final PullRequestEventProcessingService pullRequestEventProcessingService,
                                        final ReleaseEventProcessingService releaseEventProcessingService,
                                        final IssueCommentEventProcessingService issueCommentEventProcessingService) {

        this.issueEventProcessingService = issueEventProcessingService;
        this.pullRequestEventProcessingService = pullRequestEventProcessingService;
        this.releaseEventProcessingService = releaseEventProcessingService;
        this.issueCommentEventProcessingService = issueCommentEventProcessingService;
    }

    @Bean
    public Map<String, EventProcessingService> eventProcessingServiceMap() {

        return Map.ofEntries(
                Map.entry("issues", issueEventProcessingService),
                Map.entry("pull_request", pullRequestEventProcessingService),
                Map.entry("release", releaseEventProcessingService),
                Map.entry("issue_comment", issueCommentEventProcessingService));
    }
}
