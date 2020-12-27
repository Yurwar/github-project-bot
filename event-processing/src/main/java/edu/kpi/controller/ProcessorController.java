package edu.kpi.controller;

import edu.kpi.dto.*;
import edu.kpi.model.Issue;
import edu.kpi.service.IssueService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;

import java.util.ArrayList;
import java.util.List;

import static edu.kpi.utils.Constants.OPENED;


@Controller
@MessageMapping("processorController")
public class ProcessorController {
    private final IssueService issueService;

    public ProcessorController(IssueService issueService) {
        this.issueService = issueService;
    }

    @MessageMapping("fetchTweets")
    public Flux<TweetData> fetchTweets(Flux<TweetData> dataFlux) {

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
