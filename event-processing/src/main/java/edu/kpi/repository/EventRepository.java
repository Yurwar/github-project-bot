package edu.kpi.repository;

import edu.kpi.model.Event;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventRepository extends ReactiveCrudRepository<Event, Long> {
    Flux<Event> findAllByActionAndRepoId(String action, Long repoId);

    Mono<Event> findByActionAndIssueIdAndRepoId(String action, Long issueId, Long repoId);
}
