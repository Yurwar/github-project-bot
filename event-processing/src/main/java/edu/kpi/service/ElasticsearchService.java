package edu.kpi.service;

import edu.kpi.model.index.Issue;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ElasticsearchService {
    Mono<Issue> saveIssue(Issue issue);

    Flux<Issue> findIssuesByKeywords(List<String> keywords);
}
