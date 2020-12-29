package edu.kpi.service.processing.impl;

import edu.kpi.client.EventProcessorClient;
import edu.kpi.converter.PullRequestEventConverter;
import edu.kpi.model.PullRequestEvent;
import edu.kpi.service.processing.EventProcessingService;
import edu.kpi.service.processing.utils.EventSink;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
public class PullRequestEventProcessingService implements EventProcessingService {

    private final EventSink<PullRequestEvent> eventSink;
    private final Flux<PullRequestEvent> eventFlux;
    private final EventProcessorClient eventProcessorClient;

    public PullRequestEventProcessingService(final EventProcessorClient eventProcessorClient) {

        this.eventSink = new EventSink<>();
        this.eventFlux = Flux.create(eventSink);

        this.eventProcessorClient = eventProcessorClient;

        establishConnection();
    }

    @Override
    public void processEvent(final String event) {

        eventSink.publish(PullRequestEventConverter.convert(event));
    }

    private void establishConnection() {

        eventProcessorClient.connectToPullRequestProcessor(getInputFlux())
                .retryWhen(Retry.backoff(10, Duration.ofMillis(500)))
                .subscribe();
    }

    private Flux<PullRequestEvent> getInputFlux() {

        return eventFlux.log();
    }
}
