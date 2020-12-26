package edu.kpi.service.processing.utils;

import reactor.core.publisher.FluxSink;

import java.util.function.Consumer;

public class EventSink<T> implements Consumer<FluxSink<T>> {

    private FluxSink<T> fluxSink;

    @Override
    public void accept(final FluxSink<T> fluxSink) {

        this.fluxSink = fluxSink;
    }

    public void publish(final T event) {

        fluxSink.next(event);
    }
}
