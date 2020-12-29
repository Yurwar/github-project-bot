package edu.kpi.service.impl;

import edu.kpi.model.data.IssueCommentEvent;
import edu.kpi.model.data.IssueEvent;
import edu.kpi.model.data.Statistic;
import edu.kpi.repository.data.IssueCommentEventRepository;
import edu.kpi.repository.data.IssueEventRepository;
import edu.kpi.service.ElasticsearchService;
import edu.kpi.service.StatisticService;
import edu.kpi.utils.Sum;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static edu.kpi.utils.Constants.CLOSED;
import static edu.kpi.utils.Constants.OPENED;

@Service
public class DefaultStatisticService implements StatisticService {

    private final IssueEventRepository issueEventRepository;
    private final IssueCommentEventRepository issueCommentEventRepository;
    private final ElasticsearchService elasticsearchService;
    private final Flux<Statistic> statisticFlux;

    public DefaultStatisticService(IssueEventRepository issueEventRepository,
                                   IssueCommentEventRepository issueCommentEventRepository,
                                   ElasticsearchService elasticsearchService) {

        this.issueEventRepository = issueEventRepository;
        this.issueCommentEventRepository = issueCommentEventRepository;
        this.elasticsearchService = elasticsearchService;
        this.statisticFlux = createStatistic();
    }

    @Override
    public Flux<Statistic> getStatisticFlux() {
        return statisticFlux;
    }

    protected Flux<Statistic> createStatistic() {
        LocalDateTime now = LocalDateTime.now();
        return Flux.interval(Duration.between(now, now.with(TemporalAdjusters.next(DayOfWeek.MONDAY))), Duration.ofDays(7))
                .log()
                .flatMap(tick -> issueEventRepository.findRepositories())
                .map(repo -> Statistic.builder().repo(repo).build())
                .concatMap(statistic -> getAnswerAverageTime(statistic.getRepo()).map(time -> {
                    statistic.setAverageTimeBetweenCreateAndComment(time);
                    return statistic;
                }))
                .concatMap(statistic -> getClosedAverageTime(statistic.getRepo()).map(time -> {
                    statistic.setAverageTimeBetweenCreateAndClose(time);
                    return statistic;
                }))
                .concatMap(statistic -> getNumberOfIssuesByActionPerWeek(OPENED, statistic.getRepo()).map(number -> {
                    statistic.setNumberOfIssuesCreatedPerWeek(number);
                    return statistic;
                }))
                .concatMap(statistic -> getNumberOfIssuesByActionPerWeek(CLOSED, statistic.getRepo()).map(number -> {
                    statistic.setNumberOfIssuesClosedPerWeek(number);
                    return statistic;
                }))
                .concatMap(statistic -> getMostMentionedTopic(statistic.getRepo()).map(topic -> {
                    statistic.setMostMentionedTopic(topic);
                    return statistic;
                }))
                .concatMap(statistic -> getUnansweredIssues(statistic.getRepo()).map(issues -> {
                    statistic.setUnansweredIssues(issues);
                    return statistic;
                }))
                .concatMap(statistic -> getWaitingForResponseIssues(statistic.getRepo()).map(issues -> {
                    statistic.setWaitingForResponseIssues(issues);
                    return statistic;
                }));
    }

    @Override
    public Mono<Long> getNumberOfIssuesByActionPerWeek(final String action, final String repo) {
        return issueEventRepository.findAllByActionAndRepoId(action, repo)
                .filter(this::isEventHappenThisWeek)
                .filter(distinctByKey(IssueEvent::getIssueNumber))
                .count();
    }

    @Override
    public Mono<Double> getClosedAverageTime(final String repo) {
        return issueEventRepository.findAllByActionAndRepoId(OPENED, repo)
                .map(openIssueEvent -> Tuples.of(openIssueEvent, issueEventRepository.findByActionAndIssueIdAndRepoId(CLOSED, openIssueEvent.getIssueNumber(), repo)))
                .flatMap(tuple -> tuple.getT2()
                        .map(closedIssueEvent -> Duration.between(tuple.getT1().getEventTime(), closedIssueEvent.getEventTime()).toMinutes()))
                .reduce(Sum.empty(), Sum::add)
                .map(Sum::avg);
    }

    @Override
    public Mono<Double> getAnswerAverageTime(final String repo) {
        return issueEventRepository.findAllByActionAndRepoId(OPENED, repo)
                .filter(distinctByKey(IssueEvent::getIssueNumber))
                .flatMap(issueEvent -> {
                    Mono<IssueCommentEvent> answerComment = issueCommentEventRepository.findAllByRepoAndIssueNumber(repo, issueEvent.getIssueNumber())
                            .filter(comment -> comment.getOwner().equals(comment.getLogin()))
                            .sort(Comparator.comparing(IssueCommentEvent::getCreatedAt))
                            .next();

                    return answerComment.map(comment -> Duration.between(issueEvent.getEventTime(), comment.getCreatedAt()).toMinutes());
                })
                .reduce(Sum.empty(), Sum::add)
                .map(Sum::avg);
    }

    @Override
    public Mono<List<String>> getUnansweredIssues(final String repo) {
        Flux<IssueCommentEvent> issueComments = issueCommentEventRepository.findAllByRepo(repo);

        return issueComments
                .collect(Collectors.groupingBy(IssueCommentEvent::getIssueNumber))
                .flatMapIterable(Map::entrySet)
                .filter(entry -> {
                    List<IssueCommentEvent> commentEvents = entry.getValue();

                    return commentEvents.stream().noneMatch(issueComment ->
                            issueComment.getOwner().equals(issueComment.getLogin()));
                })
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public Mono<List<String>> getWaitingForResponseIssues(final String repo) {
        Flux<IssueCommentEvent> issueComments = issueCommentEventRepository.findAllByRepo(repo);

        return issueComments
                .collect(Collectors.groupingBy(IssueCommentEvent::getIssueNumber))
                .flatMapIterable(Map::entrySet)
                .filter(entry -> {
                    List<IssueCommentEvent> commentEvents = entry.getValue();

                    IssueCommentEvent lastCommentEvent = commentEvents.get(commentEvents.size() - 1);
                    return lastCommentEvent.getOwner().equals(lastCommentEvent.getLogin());
                })
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public Mono<String> getMostMentionedTopic(final String repo) {
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

    private <T> Predicate<T> distinctByKey(
            Function<? super T, ?> keyExtractor) {

        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    private boolean isEventHappenThisWeek(final IssueEvent issueEvent) {
        return Duration.between(issueEvent.getEventTime(), LocalDateTime.now())
                .compareTo(Duration.ofDays(7)) < 0;
    }
}
