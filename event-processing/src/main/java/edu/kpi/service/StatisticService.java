package edu.kpi.service;

import edu.kpi.model.Event;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StatisticService {
    Mono<Long> getNumberOfIssuesByAction(String action, long repoId);
    Mono<Long> getAverageTimeByAction(String action, long repoId);
    Flux<Event> getUnclosedEvents();
}
