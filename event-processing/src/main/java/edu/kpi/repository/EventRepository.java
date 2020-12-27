package edu.kpi.repository;

import edu.kpi.model.Event;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface EventRepository extends ReactiveCrudRepository<Event, Long> {

    @Query("SELECT * FROM event e WHERE e.action = :action AND e.repo = :repo")
    Flux<Event> findAllByActionAndRepoId(String action, String repo);

    @Query("SELECT * FROM event e WHERE e.action = :action AND e.repo = :repo AND e.issue_id = :issueId")
    Mono<Event> findByActionAndIssueIdAndRepoId(String action, String issue, String repo);
}
