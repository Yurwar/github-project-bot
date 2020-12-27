package edu.kpi.service;

import edu.kpi.model.Event;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StatisticService {
    Mono<Long> getNumberOfIssuesByAction(String action, String repoId);
    Mono<Long> getAverageTimeByAction(String action, String repoId);
    Flux<Event> getUnclosedEvents();
}
