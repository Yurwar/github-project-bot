package edu.kpi.service;

import edu.kpi.model.data.IssueEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StatisticService {
    Mono<Long> getNumberOfIssuesByAction(String action, String repoId);
    Mono<Long> getAverageTimeByAction(String action, String repoId);
    Flux<IssueEvent> getUnclosedEvents();
}
