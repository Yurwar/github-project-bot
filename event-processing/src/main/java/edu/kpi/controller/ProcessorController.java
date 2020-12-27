package edu.kpi.controller;

import edu.kpi.convertor.Convertor;
import edu.kpi.dto.*;
import edu.kpi.model.data.IssueEvent;
import edu.kpi.model.index.Issue;
import edu.kpi.repository.data.IssueEventRepository;
import edu.kpi.service.IssueService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.util.ArrayList;
import java.util.List;

import static edu.kpi.utils.Constants.OPENED;


@Controller
@MessageMapping("processorController")
public class ProcessorController {
    private final IssueService issueService;
    private final IssueEventRepository issueEventRepository;
    private final Convertor<IssueEventDto, IssueEvent> reversedIssueEventConvertor;
    private final Convertor<IssueEventDto, Issue> reversedIssueConvertor;

    public ProcessorController(IssueService issueService,
                               IssueEventRepository issueEventRepository,
                               Convertor<IssueEventDto, IssueEvent> reversedIssueEventConvertor,
                               Convertor<IssueEventDto, Issue> reversedIssueConvertor) {
        this.issueService = issueService;
        this.issueEventRepository = issueEventRepository;
        this.reversedIssueEventConvertor = reversedIssueEventConvertor;
        this.reversedIssueConvertor = reversedIssueConvertor;
    }

    @MessageMapping("fetchTweets")
    public Flux<TweetData> fetchTweets(Flux<TweetData> dataFlux) {

        return dataFlux;
    }

    @MessageMapping("keywords")
    public Flux<List<String>> getKeywords() {
        return Flux.just(new ArrayList<>());
    }

    @MessageMapping("issue")
    public Flux<IssueEventDto> connectIssue(Flux<IssueEventDto> issueEventFlux) {

        return issueEventFlux
                .filter(issueEvent -> OPENED.equals(issueEvent.getAction()))

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
                        .map(voidResponse -> tuple.getT1()))

                .map(issueEvent -> {
                    Mono<IssueEvent> savedEvent = issueEventRepository
                            .save(reversedIssueEventConvertor.convert(issueEvent));
                    return Tuples.of(issueEvent, savedEvent);
                })
                .flatMap(tuple -> tuple.getT2()
                        .map(voidResponse -> tuple.getT1()));
    }

    @MessageMapping("pullRequest")
    public Flux<PullRequestEventDto> connectPullRequest(Flux<PullRequestEventDto> eventFlux) {

        return eventFlux;
    }

    @MessageMapping("release")
    public Flux<ReleaseEventDto> connectRelease(Flux<ReleaseEventDto> eventFlux) {

        return eventFlux;
    }

    @MessageMapping("issueComment")
    public Flux<IssueCommentEventDto> connectIssueComment(Flux<IssueCommentEventDto> eventFlux) {

        return eventFlux;
    }
}
