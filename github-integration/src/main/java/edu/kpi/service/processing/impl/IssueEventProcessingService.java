package edu.kpi.service.processing.impl;

import edu.kpi.client.EventProcessorClient;
import edu.kpi.converter.IssueEventConverter;
import edu.kpi.model.IssueEvent;
import edu.kpi.service.integration.IssueIntegrationService;
import edu.kpi.service.processing.EventProcessingService;
import edu.kpi.service.processing.utils.EventSink;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IssueEventProcessingService implements EventProcessingService {

    private static final List<String> acceptableIssueEvents = List.of("opened", "closed");

    private final EventSink<IssueEvent> eventSink;
    private final Flux<IssueEvent> eventFlux;
    private final EventProcessorClient eventProcessorClient;
    private final IssueIntegrationService issueIntegrationService;

    public IssueEventProcessingService(final EventProcessorClient eventProcessorClient,
                                       final IssueIntegrationService issueIntegrationService) {

        this.eventSink = new EventSink<>();
        this.eventFlux = Flux.create(eventSink);

        this.eventProcessorClient = eventProcessorClient;
        this.issueIntegrationService = issueIntegrationService;

        establishConnection();
    }

    @Override
    public void processEvent(final String event) {

        eventSink.publish(IssueEventConverter.convert(event));
    }

    private void establishConnection() {

        eventProcessorClient.connectToIssueProcessor(getInputFlux())
                .flatMap(this::processResponse)
                .subscribe();
    }

    private Flux<IssueEvent> getInputFlux() {

        return eventFlux
                .log()
                .filter(element -> acceptableIssueEvents.contains(element.getAction()));
    }

    private Mono<Void> processResponse(final IssueEvent event) {

        if (event.isAwaitingTriage()) {

            event.setLabels(Collections.singletonList("awaiting-triage"));
            return issueIntegrationService.addLabelsForIssue(event);

        } else {

            event.setLabels(Collections.singletonList("duplicate"));
            event.setComment("Duplicate of: " + getSimilarIssuesString(event.getSimilarIssues()));

            return issueIntegrationService.addCommentForIssue(event)
                    .mergeWith(issueIntegrationService.addLabelsForIssue(event))
                    .then();
        }
    }

    private String getSimilarIssuesString(final List<String> similarIssues) {

        return similarIssues.stream()
                .map(issue -> "#" + issue)
                .collect(Collectors.joining(", "));
    }
}
