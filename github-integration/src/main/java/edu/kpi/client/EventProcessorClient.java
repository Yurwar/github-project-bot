package edu.kpi.client;

import edu.kpi.model.IssueCommentEvent;
import edu.kpi.model.IssueEvent;
import edu.kpi.model.PullRequestEvent;
import edu.kpi.model.ReleaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class EventProcessorClient {

    private final RSocketRequester requester;


    public EventProcessorClient(final RSocketRequester requester) {

        this.requester = requester;
    }

    public Flux<IssueEvent> connectToIssueProcessor(final Flux<IssueEvent> eventFlux) {

        return requester.route("processorController.issue")
                .data(eventFlux)
                .retrieveFlux(IssueEvent.class);
    }

    public Flux<PullRequestEvent> connectToPullRequestProcessor(final Flux<PullRequestEvent> eventFlux) {

        return requester.route("processorController.pullRequest")
                .data(eventFlux)
                .retrieveFlux(PullRequestEvent.class);
    }

    public Flux<ReleaseEvent> connectToReleaseProcessor(final Flux<ReleaseEvent> eventFlux) {

        return requester.route("processorController.release")
                .data(eventFlux)
                .retrieveFlux(ReleaseEvent.class);
    }

    public Flux<IssueCommentEvent> connectToIssueCommentProcessor(final Flux<IssueCommentEvent> eventFlux) {

        return requester.route("processorController.issueComment")
                .data(eventFlux)
                .retrieveFlux(IssueCommentEvent.class);
    }
}
