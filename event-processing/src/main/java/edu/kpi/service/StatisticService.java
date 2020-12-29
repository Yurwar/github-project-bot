package edu.kpi.service;

import edu.kpi.model.data.Statistic;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface StatisticService {
    Flux<Statistic> getStatisticFlux();

    Mono<Long> getNumberOfIssuesByActionPerWeek(String action, String repo);

    Mono<Double> getClosedAverageTime(String repo);

    Mono<Double> getAnswerAverageTime(String repo);

    Mono<List<String>> getUnansweredIssues(String repo);

    Mono<List<String>> getWaitingForResponseIssues(String repo);

    Mono<String> getMostMentionedTopic(String repo);
}
