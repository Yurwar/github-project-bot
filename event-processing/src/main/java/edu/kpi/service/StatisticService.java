package edu.kpi.service;

import reactor.core.publisher.Mono;

public interface StatisticService {
    Mono<Long> getNumberOfCreatedIssues(long repoId);
    Mono<Long> getNumberOfClosedIssues(long repoId);
    Mono<Long> getAverageResponseTime(long repoId);
    Mono<Long> getAverageCloseTime(long repoId);
}
