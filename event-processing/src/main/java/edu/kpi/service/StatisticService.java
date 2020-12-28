package edu.kpi.service;

import edu.kpi.model.data.IssueEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface StatisticService {
    Mono<Long> getNumberOfIssuesByAction(String action, String repo);
    Mono<Long> getAverageTimeByAction(String action, String repo);
    Mono<List<IssueEvent>> getUnclosedEvents(String repo);
    Mono<String> getMostMentionedTopic(String repo);
}
