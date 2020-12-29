package edu.kpi.controller;

import edu.kpi.converter.Converter;
import edu.kpi.dto.*;
import edu.kpi.model.data.IssueCommentEvent;
import edu.kpi.model.data.IssueEvent;
import edu.kpi.model.index.Issue;
import edu.kpi.repository.data.IssueCommentEventRepository;
import edu.kpi.repository.data.IssueEventRepository;
import edu.kpi.service.IssueService;
import edu.kpi.service.NotificationService;
import edu.kpi.service.TagsService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import static edu.kpi.utils.Constants.*;


@Controller
@MessageMapping("processorController")
public class ProcessorController {
    private final IssueService issueService;
    private final IssueEventRepository issueEventRepository;
    private final IssueCommentEventRepository issueCommentEventRepository;
    private final NotificationService notificationService;
    private final TagsService tagsService;
    private final Converter<IssueCommentEventDto, IssueCommentEvent> reversedIssueCommentEventConverter;
    private final Converter<IssueEventDto, IssueEvent> reversedIssueEventConverter;
    private final Converter<IssueEventDto, Issue> reversedIssueConverter;

    public ProcessorController(IssueService issueService,
                               IssueEventRepository issueEventRepository,
                               IssueCommentEventRepository issueCommentEventRepository,
                               NotificationService notificationService,
                               TagsService tagsService,
                               Converter<IssueCommentEventDto, IssueCommentEvent> reversedIssueCommentEventConverter,
                               Converter<IssueEventDto, IssueEvent> reversedIssueEventConverter,
                               Converter<IssueEventDto, Issue> reversedIssueConverter) {
        this.issueService = issueService;
        this.issueEventRepository = issueEventRepository;
        this.issueCommentEventRepository = issueCommentEventRepository;
        this.notificationService = notificationService;
        this.tagsService = tagsService;
        this.reversedIssueCommentEventConverter = reversedIssueCommentEventConverter;
        this.reversedIssueEventConverter = reversedIssueEventConverter;
        this.reversedIssueConverter = reversedIssueConverter;
    }

    @MessageMapping("fetchTweets")
    public Flux<TweetData> fetchTweets(Flux<TweetData> dataFlux) {

        return dataFlux
                .log()
                .doOnNext(notificationService::tweetNotify);
    }

    @MessageMapping("fetchStatistics")
    public Flux<StatisticsData> fetchStatistics(Flux<StatisticsData> dataFlux) {

        return dataFlux
                .log()
                .doOnNext(notificationService::tweetStatisticNotify);
    }

    @MessageMapping("keywords")
    public Flux<TagsData> getKeywords() {

        return tagsService.getTagsData();
    }

    @MessageMapping("tweetsCount")
    public Flux<Integer> getTweetsCount() {

        return Flux.just(5);
    }

    @MessageMapping("issue")
    public Flux<IssueEventDto> connectIssue(Flux<IssueEventDto> issueEventFlux) {
        Flux<IssueEventDto> savedIssueFlux = issueEventFlux.map(issueEvent -> {
            Mono<IssueEvent> savedEvent = issueEventRepository
                    .save(reversedIssueEventConverter.convert(issueEvent));
            return Tuples.of(issueEvent, savedEvent);
        }).flatMap(tuple -> tuple.getT2()
                .map(voidResponse -> tuple.getT1()));

        Flux<IssueEventDto> notifiedIssueFlux = savedIssueFlux
                .doOnNext(notificationService::issueNotify);

        Flux<IssueEventDto> shared = notifiedIssueFlux.share();

        Flux<IssueEventDto> openedIssuesEvent = shared.filter(issueEvent -> OPENED.equals(issueEvent.getAction()));
        Flux<IssueEventDto> closedIssuesEvent = shared.filter(issueEvent -> CLOSED.equals(issueEvent.getAction()));
        Flux<IssueEventDto> commentedIssuesEvent = shared.filter(issueEvent -> COMMENTED.equals(issueEvent.getAction()));

        return openedIssuesEvent
                .map(issueEvent -> Tuples.of(issueEvent, issueService.findSimilarIssue(issueEvent)))
                .flatMap(tuple -> tuple.getT2()
                        .map(Issue::getNumber)
                        .map(String::valueOf)
                        .collectList()
                        .map(similarIssues -> {
                            tuple.getT1().setSimilarIssues(similarIssues);
                            return tuple.getT1();
                        }))

                .map(issueEvent -> Tuples.of(issueEvent, issueService.saveIssueEvent(reversedIssueConverter.convert(issueEvent))))
                .flatMap(tuple -> tuple.getT2()
                        .map(voidResponse -> tuple.getT1()));
    }

    @MessageMapping("pullRequest")
    public Flux<PullRequestEventDto> connectPullRequest(Flux<PullRequestEventDto> eventFlux) {

        return eventFlux
                .doOnNext(notificationService::pullRequestNotify);
    }

    @MessageMapping("release")
    public Flux<ReleaseEventDto> connectRelease(Flux<ReleaseEventDto> eventFlux) {

        return eventFlux
                .doOnNext(notificationService::releaseNotify);
    }

    @MessageMapping("issueComment")
    public Flux<IssueCommentEventDto> connectIssueComment(Flux<IssueCommentEventDto> issueCommentEventFlux) {

        return issueCommentEventFlux
                .doOnNext(notificationService::issueCommentNotify)
                .map(issueCommentEvent -> {
                    Mono<IssueCommentEvent> savedEvent = issueCommentEventRepository
                            .save(reversedIssueCommentEventConverter.convert(issueCommentEvent));

                    return Tuples.of(issueCommentEvent, savedEvent);
                })
                .flatMap(tuple -> tuple.getT2()
                        .map(voidResponse -> tuple.getT1()));
    }
}
