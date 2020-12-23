package edu.kpi.service;

import edu.kpi.model.Issue;
import reactor.core.publisher.Flux;

public interface IssueService {
    Flux<Issue> findSimilarIssuesInPast(Issue issue);

    void saveIssue(Issue issue);
}
