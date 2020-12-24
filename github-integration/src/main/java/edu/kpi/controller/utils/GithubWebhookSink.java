package edu.kpi.controller.utils;

import edu.kpi.dto.EventDto;
import reactor.core.publisher.FluxSink;

import java.util.function.Consumer;

public class GithubWebhookSink implements Consumer<FluxSink<EventDto>> {

    private FluxSink<EventDto> fluxSink;

    @Override
    public void accept(final FluxSink<EventDto> fluxSink) {

        this.fluxSink = fluxSink;
    }

    public void publish(final EventDto event) {

        fluxSink.next(event);
    }
}
