package edu.kpi.service;

import reactor.core.publisher.Mono;

public interface StatisticService {
    Mono<Long> getNumberOfIssuesByAction(String action, long repoId);
    Mono<Long> getAverageTimeByAction(String action, long repoId);
}
