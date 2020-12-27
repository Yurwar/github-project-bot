package edu.kpi.controller;

import edu.kpi.dto.*;
import edu.kpi.dto.IssueCommentEvent;
import edu.kpi.dto.IssueEvent;
import edu.kpi.dto.PullRequestEvent;
import edu.kpi.dto.ReleaseEvent;
import edu.kpi.model.Event;
import edu.kpi.model.Issue;
import edu.kpi.repository.EventRepository;
import edu.kpi.service.IssueService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

import static edu.kpi.utils.Constants.OPENED;


@Controller
@MessageMapping("processorController")
public class ProcessorController {
    private final IssueService issueService;
    private final EventRepository eventRepository;

    public ProcessorController(IssueService issueService, EventRepository eventRepository) {
        this.issueService = issueService;
        this.eventRepository = eventRepository;
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
    public Flux<IssueEvent> connectIssue(Flux<IssueEvent> eventFlux) {

        return eventFlux
                .filter(event -> OPENED.equals(event.getAction()))

                .map(event -> Tuples.of(event, issueService.findSimilarIssue(event)))
                .flatMap(tuple -> tuple.getT2()
                        .map(Issue::getNumber)
                        .map(String::valueOf)
                        .collectList()
                        .map(list -> {
                            tuple.getT1().setSimilarIssues(list);
                            return tuple.getT1();
                        }))

                .map(event -> Tuples.of(event, issueService.saveIssueEvent(event)))
                .flatMap(tuple -> tuple.getT2()
                        .map(voidResponse -> tuple.getT1()))

                .map(event -> {
                    Mono<Event> savedEvent = eventRepository.save(Event.builder()
                            .action(event.getAction())
                            .comment(event.getBody())
                            .repo(event.getRepo())
                            .eventTime(LocalDateTime.now())
                            .issueId(event.getIssueNumber())
                            .build());
                    return Tuples.of(event, savedEvent);
                })
                .flatMap(tuple -> tuple.getT2()
                        .map(voidResponse -> tuple.getT1()));
    }

    @MessageMapping("pullRequest")
    public Flux<PullRequestEvent> connectPullRequest(Flux<PullRequestEvent> eventFlux) {

        return eventFlux;
    }

    @MessageMapping("release")
    public Flux<ReleaseEvent> connectRelease(Flux<ReleaseEvent> eventFlux) {

        return eventFlux;
    }

    @MessageMapping("issueComment")
    public Flux<IssueCommentEvent> connectIssueComment(Flux<IssueCommentEvent> eventFlux) {

        return eventFlux;
    }
}
