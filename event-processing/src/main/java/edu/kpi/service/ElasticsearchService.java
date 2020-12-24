package edu.kpi.service;

import edu.kpi.model.Issue;
import reactor.core.publisher.Flux;

public interface ElasticsearchService {
    void saveIssue(Issue issue);

    Flux<Issue> findIssuesByKeyword(String keyword);
}
