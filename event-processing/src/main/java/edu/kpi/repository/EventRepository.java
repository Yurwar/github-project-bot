package edu.kpi.repository;

import edu.kpi.model.Event;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface EventRepository extends ReactiveCrudRepository<Event, Long> {
    Flux<Event> findAll();
}
