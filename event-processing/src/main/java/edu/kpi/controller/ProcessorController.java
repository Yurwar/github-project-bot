package edu.kpi.controller;

import edu.kpi.dto.IssueCommentEvent;
import edu.kpi.dto.IssueEvent;
import edu.kpi.dto.PullRequestEvent;
import edu.kpi.dto.ReleaseEvent;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.util.Collections;

@Controller
@MessageMapping("processorController")
public class ProcessorController {

    @MessageMapping("issue")
    public Flux<IssueEvent> connectIssue(Flux<IssueEvent> eventFlux) {

        return eventFlux
                .filter(event -> "opened".equals(event.getAction()))
                .map(event -> {
                    event.setSimilarIssues(Collections.singletonList("TEST"));
                    return event;
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