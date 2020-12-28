package edu.kpi.service;

import edu.kpi.model.data.IssueEvent;
import edu.kpi.model.data.Statistic;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface StatisticService {
    Flux<Statistic> getStatisticFlux();

    Mono<Long> getNumberOfIssuesByAction(String action, String repo);
    Mono<Long> getAverageTimeByAction(String action, String repo);
    Mono<List<IssueEvent>> getUnclosedEvents(String repo);
    Mono<String> getMostMentionedTopic(String repo);
}
