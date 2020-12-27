package edu.kpi.service;

import edu.kpi.dto.IssueEvent;
import edu.kpi.model.Issue;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IssueService {
    Flux<Issue> findSimilarIssue(IssueEvent issue);

    Mono<Issue> saveIssueEvent(IssueEvent issue);
}
