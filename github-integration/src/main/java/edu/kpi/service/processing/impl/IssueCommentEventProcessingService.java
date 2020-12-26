package edu.kpi.service.processing.impl;

import edu.kpi.client.EventProcessorClient;
import edu.kpi.converter.IssueCommentEventConverter;
import edu.kpi.model.IssueCommentEvent;
import edu.kpi.service.processing.EventProcessingService;
import edu.kpi.service.processing.utils.EventSink;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class IssueCommentEventProcessingService implements EventProcessingService {

    private final EventSink<IssueCommentEvent> eventSink;
    private final Flux<IssueCommentEvent> eventFlux;
    private final EventProcessorClient eventProcessorClient;

    public IssueCommentEventProcessingService(final EventProcessorClient eventProcessorClient) {

        this.eventSink = new EventSink<>();
        this.eventFlux = Flux.create(eventSink);

        this.eventProcessorClient = eventProcessorClient;

        establishConnection();
    }

    @Override
    public void processEvent(final String event) {

        eventSink.publish(IssueCommentEventConverter.convert(event));
    }

    private void establishConnection() {

        eventProcessorClient.connectToIssueCommentProcessor(getInputFlux())
                .subscribe();
    }

    private Flux<IssueCommentEvent> getInputFlux() {

        return eventFlux.log();
    }
}
