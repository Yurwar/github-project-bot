package edu.kpi.controller;

import edu.kpi.dto.IssueCommentEvent;
import edu.kpi.dto.IssueEvent;
import edu.kpi.dto.PullRequestEvent;
import edu.kpi.dto.ReleaseEvent;
import edu.kpi.model.Issue;
import edu.kpi.service.IssueService;
import edu.kpi.utils.Constants;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;


@Controller
@MessageMapping("processorController")
public class ProcessorController {
    private final IssueService issueService;

    public ProcessorController(IssueService issueService) {
        this.issueService = issueService;
    }

    @MessageMapping("issue")
    public Flux<IssueEvent> connectIssue(Flux<IssueEvent> eventFlux) {

        return eventFlux
                .filter(event -> Constants.OPENED.equals(event.getAction()))
                .map(event -> Tuples.of(event, issueService.findSimilarIssue(event)))
                .flatMap(tuple -> tuple.getT2()
                        .map(Issue::getNumber)
                        .map(String::valueOf)
                        .collectList()
                        .map(list -> {
                            tuple.getT1().setSimilarIssues(list);
                            return tuple.getT1();
                        }))
                //FIXME Do not save issue to elastic
                .map(issueEvent -> {
                    issueService.saveIssueEvent(issueEvent);
                    return issueEvent;
                });
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