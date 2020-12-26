package edu.kpi.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.w3c.dom.events.Event;

@Repository
public interface EventRepository extends ReactiveCrudRepository<Event, Long> {
}
