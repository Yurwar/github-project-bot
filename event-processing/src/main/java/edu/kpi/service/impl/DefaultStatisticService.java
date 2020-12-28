package edu.kpi.service.impl;

import edu.kpi.model.data.IssueEvent;
import edu.kpi.model.data.Statistic;
import edu.kpi.repository.data.IssueEventRepository;
import edu.kpi.service.ElasticsearchService;
import edu.kpi.service.StatisticService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static edu.kpi.utils.Constants.*;

@Service
public class DefaultStatisticService implements StatisticService {

    private final IssueEventRepository issueEventRepository;
    private final ElasticsearchService elasticsearchService;
    private final Flux<Statistic> statisticFlux;

    public DefaultStatisticService(final IssueEventRepository issueEventRepository,
                                   ElasticsearchService elasticsearchService) {

        this.issueEventRepository = issueEventRepository;
        this.elasticsearchService = elasticsearchService;
        this.statisticFlux = createStatistic();
    }

    @Override
    public Flux<Statistic> getStatisticFlux() {
        return statisticFlux;
    }

    private Flux<Statistic> createStatistic() {
        //TODO uncomment for PROD
//        LocalDateTime now = LocalDateTime.now();
//        return Flux.interval(Duration.between(now, now.with(TemporalAdjusters.next(DayOfWeek.MONDAY))), Duration.ofDays(7))
        return Flux.interval(Duration.ofSeconds(1000))
                .flatMap(tick -> issueEventRepository.findRepositories())
                .map(repo -> Statistic.builder().repo(repo).build())
                .concatMap(statistic -> getNumberOfIssuesByAction(OPENED, statistic.getRepo()).map(number -> {
                    statistic.setNumberOfIssuesCreatedPerWeek(number);
                    return statistic;
                }))
                .concatMap(statistic -> getNumberOfIssuesByAction(CLOSED, statistic.getRepo()).map(number -> {
                    statistic.setNumberOfIssuesClosedPerWeek(number);
                    return statistic;
                }))
                .concatMap(statistic -> getAverageTimeByAction(CLOSED, statistic.getRepo()).map(time -> {
                    statistic.setAverageTimeBetweenCreateAndClose(time);
                    return statistic;
                }))
                .concatMap(statistic -> getAverageTimeByAction(COMMENTED, statistic.getRepo()).map(time -> {
                    statistic.setAverageTimeBetweenCreateAndComment(time);
                    return statistic;
                }))
                .concatMap(statistic -> getUnclosedEvents(statistic.getRepo()).map(issueEvents -> {
                    statistic.setUnclosedIssues(issueEvents);
                    return statistic;
                }))
                .concatMap(statistic -> getMostMentionedTopic(statistic.getRepo()).map(topic -> {
                    statistic.setMostMentionedTopic(topic);
                    return statistic;
                }));
    }

    @Override
    public final Mono<Long> getNumberOfIssuesByAction(final String action, final String repo) {
        return issueEventRepository.findAllByActionAndRepoId(action, repo)
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
                .map(list -> list.size() == 0 ? 0 : list.stream().reduce(0L, Long::sum) / list.size());
    }

    @Override
    public final Mono<List<IssueEvent>> getUnclosedEvents(final String repo) {
        return issueEventRepository.findAllByActionAndRepoId(OPENED, repo)
                .filter(openIssueEvent -> issueEventRepository.findByActionAndIssueIdAndRepoId(CLOSED, openIssueEvent.getIssueNumber(), repo).equals(Mono.empty()))
                .collect(Collectors.toList());
    }

    @Override
    public final Mono<String> getMostMentionedTopic(final String repo) {
        Comparator<Tuple2<String, Long>> sortFunction = Comparator.comparingLong(Tuple2::getT2);

        return elasticsearchService.findIssuesByRepository(repo)
                .map(issue -> issue.getTitle() + " " + issue.getBody())
                .map(String::toLowerCase)
                .flatMap(fullDescription ->
                        Flux.fromStream(Arrays.stream(fullDescription.split("\\W+")).filter(word -> !word.isBlank())))
                .filter(word -> word.length() > 3)
                .groupBy(Function.identity())
                .flatMap(gf -> {
                    Mono<Long> count = gf.count();
                    String key = gf.key();

                    return count.map(amount -> Tuples.of(key, amount));
                })
                .sort(sortFunction.reversed())
                .map(Tuple2::getT1)
                .next();
    }


    private boolean isEventHappenThisWeek(final IssueEvent issueEvent) {
        return issueEvent.getEventTime().getDayOfYear() - LocalDateTime.now().getDayOfYear() < 7 &&
                issueEvent.getEventTime().getYear() == LocalDateTime.now().getYear();
    }
}
