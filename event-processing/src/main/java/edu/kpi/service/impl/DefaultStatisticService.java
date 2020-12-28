package edu.kpi.service.impl;

import edu.kpi.model.data.IssueEvent;
import edu.kpi.model.data.Statistic;
import edu.kpi.repository.data.IssueEventRepository;
import edu.kpi.service.StatisticService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static edu.kpi.utils.Constants.*;

@Service
public class DefaultStatisticService implements StatisticService {

    private final IssueEventRepository issueEventRepository;

    public DefaultStatisticService(final IssueEventRepository issueEventRepository) {
        this.issueEventRepository = issueEventRepository;
    }

    public final Flux<Statistic> createStatistic(final String repo) {
        return Flux.interval(Duration.ofDays(7))
                .map(tick -> Statistic.builder().build())
                .zipWith(getNumberOfIssuesByAction(OPENED, repo), (statistic, number) -> {
                    statistic.setNumberOfIssuesCreatedPerWeek(number);
                    return statistic;
                })
                .zipWith(getNumberOfIssuesByAction(CLOSED, repo), (statistic, number) -> {
                    statistic.setNumberOfIssuesClosedPerWeek(number);
                    return statistic;
                })
                .zipWith(getAverageTimeByAction(CLOSED, repo), (statistic, time) -> {
                    statistic.setAverageTimeBetweenCreateAndClose(time);
                    return statistic;
                })
                .zipWith(getAverageTimeByAction(COMMENTED, repo), (statistic, time) -> {
                    statistic.setAverageTimeBetweenCreateAndComment(time);
                    return statistic;
                })
                .zipWith(getUnclosedEvents(repo), (statistic, events) -> {
                    statistic.setUnclosedIssues(events);
                    return statistic;
                })
                .zipWith(getMostMentionedTopic(repo), (statistic, topic) -> {
                    statistic.setMostMentionedTopic(topic);
                    return statistic;
                });
    }

    @Override
    public final Mono<Long> getNumberOfIssuesByAction(final String action, final String repo) {
        return issueEventRepository.findAllByActionAndRepoId(action, repo)
                .log()
                .filter(this::isEventHappenThisWeek)
                .count();
    }

    @Override
    public final Mono<Long> getAverageTimeByAction(final String action, final String repo) {
        return issueEventRepository.findAllByActionAndRepoId(OPENED, repo)
                .map(openIssueEvent -> Tuples.of(openIssueEvent, issueEventRepository.findByActionAndIssueIdAndRepoId(action, openIssueEvent.getIssueNumber(), repo)))
                .flatMap(tuple -> tuple.getT2()
                        .map(closedIssueEvent -> Duration.between(closedIssueEvent.getEventTime(), tuple.getT1().getEventTime()).toMinutes()))
                .collect(Collectors.toList())
                .map(list -> list.stream().reduce(0L, Long::sum) / list.size());
    }

    @Override
    public final Mono<List<IssueEvent>> getUnclosedEvents(final String repo) {
        return issueEventRepository.findAllByActionAndRepoId(OPENED, repo)
                .filter(openIssueEvent -> issueEventRepository.findByActionAndIssueIdAndRepoId(CLOSED, openIssueEvent.getIssueNumber(), repo).equals(Mono.empty()))
                .collect(Collectors.toList());
    }

    @Override
    public final Mono<String> getMostMentionedTopic(final String repo) {
        return Mono.empty();
    }


    private boolean isEventHappenThisWeek(final IssueEvent issueEvent) {
        return issueEvent.getEventTime().getDayOfYear() - LocalDateTime.now().getDayOfYear() < 7 &&
                issueEvent.getEventTime().getYear() == LocalDateTime.now().getYear();
    }
}
