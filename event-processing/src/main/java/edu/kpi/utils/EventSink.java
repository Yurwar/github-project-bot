package edu.kpi.utils;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import reactor.core.publisher.FluxSink;

import java.util.function.Consumer;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
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
