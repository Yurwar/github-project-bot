package edu.kpi.controller;

import edu.kpi.convertor.Convertor;
import edu.kpi.dto.*;
import edu.kpi.model.data.IssueEvent;
import edu.kpi.model.index.Issue;
import edu.kpi.repository.data.IssueEventRepository;
import edu.kpi.service.IssueService;
import edu.kpi.service.NotificationService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.util.List;

import static edu.kpi.utils.Constants.*;


@Controller
@MessageMapping("processorController")
public class ProcessorController {
    private final IssueService issueService;
    private final IssueEventRepository issueEventRepository;
    private final NotificationService notificationService;
    private final Convertor<IssueEventDto, IssueEvent> reversedIssueEventConvertor;
    private final Convertor<IssueEventDto, Issue> reversedIssueConvertor;

    public ProcessorController(IssueService issueService,
                               IssueEventRepository issueEventRepository,
                               NotificationService notificationService,
                               Convertor<IssueEventDto, IssueEvent> reversedIssueEventConvertor,
                               Convertor<IssueEventDto, Issue> reversedIssueConvertor) {
        this.issueService = issueService;
        this.issueEventRepository = issueEventRepository;
        this.notificationService = notificationService;
        this.reversedIssueEventConvertor = reversedIssueEventConvertor;
        this.reversedIssueConvertor = reversedIssueConvertor;
    }

    @MessageMapping("fetchTweets")
    public Flux<TweetsEvent> fetchTweets(Flux<TweetsEvent> dataFlux) {

        return dataFlux;
    }

    @MessageMapping("fetchStatistics")
    public Flux<StatisticsData> fetchStatistics(Flux<StatisticsData> dataFlux) {

        return dataFlux;
    }

    @MessageMapping("keywords")
    public Flux<TagsData> getKeywords() {

        return Flux.just(TagsData.builder().tags(List.of("Subaru", "BMW", "Mercedes-Benz")).build());
    }

    @MessageMapping("tweetsCount")
    public Flux<Integer> getTweetsCount() {

        return Flux.just(5);
    }

    @MessageMapping("issue")
    public Flux<IssueEventDto> connectIssue(Flux<IssueEventDto> issueEventFlux) {
        Flux<IssueEventDto> savedIssueFlux = issueEventFlux.map(issueEvent -> {
            Mono<IssueEvent> savedEvent = issueEventRepository
                    .save(reversedIssueEventConvertor.convert(issueEvent));
            return Tuples.of(issueEvent, savedEvent);
        }).flatMap(tuple -> tuple.getT2()
                .map(voidResponse -> tuple.getT1()));

        Flux<IssueEventDto> shared = savedIssueFlux.share();

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

                .map(issueEvent -> Tuples.of(issueEvent, issueService.saveIssueEvent(reversedIssueConvertor.convert(issueEvent))))
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
    public Flux<IssueCommentEventDto> connectIssueComment(Flux<IssueCommentEventDto> eventFlux) {

        return eventFlux
                .doOnNext(notificationService::issueCommentNotify);
    }
}
