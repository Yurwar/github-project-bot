package edu.kpi.service.integration;

import edu.kpi.model.IssueEvent;
import reactor.core.publisher.Mono;

public interface IssueIntegrationService {

    Mono<Void> addLabelsForIssue(final IssueEvent data);

    Mono<Void> addCommentForIssue(final IssueEvent data);
}
