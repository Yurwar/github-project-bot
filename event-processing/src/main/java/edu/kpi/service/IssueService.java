package edu.kpi.service;

import edu.kpi.dto.IssueEventDto;
import edu.kpi.model.index.Issue;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IssueService {
    Flux<Issue> findSimilarIssue(IssueEventDto issue);

    Mono<Issue> saveIssueEvent(Issue issue);
}
