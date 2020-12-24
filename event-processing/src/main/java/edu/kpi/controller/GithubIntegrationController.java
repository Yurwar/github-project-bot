package edu.kpi.controller;

import edu.kpi.model.Issue;
import edu.kpi.dto.EventDto;
import edu.kpi.model.Message;
import edu.kpi.model.MessageRequest;
import edu.kpi.service.IssueService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Controller
@MessageMapping("githubIntegrationController")
public class GithubIntegrationController {
    private final IssueService issueService;

    public GithubIntegrationController(IssueService issueService) {
        this.issueService = issueService;
    }


    @MessageMapping("getTestMessage")
    public Flux<Message> getTestMessage(MessageRequest request) {
        return Flux.range(0, request.getId())
                .map(id -> new Message("Test message: " + id));
    }

    @MessageMapping("connect")
    public Flux<EventDto> connect(Flux<EventDto> eventFlux) {
        return eventFlux;
    }

    @MessageMapping("issueCreated")
    public Flux<Issue> issueCreated(Issue issue) {
        Flux<Issue> similarIssues = issueService.findSimilarIssuesInPast(issue);
        issueService.saveIssue(issue);

        return similarIssues;
    }

}
