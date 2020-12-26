package edu.kpi.configuration;

import edu.kpi.service.processing.EventProcessingService;
import edu.kpi.service.processing.impl.IssueEventProcessingService;
import edu.kpi.service.processing.impl.PullRequestEventProcessingService;
import edu.kpi.service.processing.impl.ReleaseEventProcessingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class EventProcessingConfiguration {

    private final IssueEventProcessingService issueEventProcessingService;
    private final PullRequestEventProcessingService pullRequestEventProcessingService;
    private final ReleaseEventProcessingService releaseEventProcessingService;

    public EventProcessingConfiguration(final IssueEventProcessingService issueEventProcessingService,
                                        final PullRequestEventProcessingService pullRequestEventProcessingService,
                                        final ReleaseEventProcessingService releaseEventProcessingService) {

        this.issueEventProcessingService = issueEventProcessingService;
        this.pullRequestEventProcessingService = pullRequestEventProcessingService;
        this.releaseEventProcessingService = releaseEventProcessingService;
    }

    @Bean
    public Map<String, EventProcessingService> eventProcessingServiceMap() {

        final Map<String, EventProcessingService> result = new HashMap<>();

        result.put("issues", issueEventProcessingService);
        result.put("pull_request", pullRequestEventProcessingService);
        result.put("release", releaseEventProcessingService);

        return result;
    }
}
