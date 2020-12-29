package edu.kpi.service.processing.impl;

import edu.kpi.client.EventProcessorClient;
import edu.kpi.converter.ReleaseEventConverter;
import edu.kpi.model.ReleaseEvent;
import edu.kpi.service.processing.EventProcessingService;
import edu.kpi.service.processing.utils.EventSink;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
public class ReleaseEventProcessingService implements EventProcessingService {

    private final EventSink<ReleaseEvent> eventSink;
    private final Flux<ReleaseEvent> eventFlux;
    private final EventProcessorClient eventProcessorClient;

    public ReleaseEventProcessingService(final EventProcessorClient eventProcessorClient) {

        this.eventSink = new EventSink<>();
        this.eventFlux = Flux.create(eventSink);

        this.eventProcessorClient = eventProcessorClient;

        establishConnection();
    }

    @Override
    public void processEvent(final String event) {

        eventSink.publish(ReleaseEventConverter.convert(event));
    }

    private void establishConnection() {

        eventProcessorClient.connectToReleaseProcessor(getInputFlux())
                .retryWhen(Retry.backoff(10, Duration.ofMillis(500)))
                .subscribe();
    }

    private Flux<ReleaseEvent> getInputFlux() {

        return eventFlux.log();
    }
}
