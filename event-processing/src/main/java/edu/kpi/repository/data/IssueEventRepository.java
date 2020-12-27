package edu.kpi.repository.data;

import edu.kpi.model.data.IssueEvent;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface IssueEventRepository extends R2dbcRepository<IssueEvent, Long> {

    @Query("SELECT * FROM issue_event ie WHERE ie.action = :action AND ie.repo = :repo")
    Flux<IssueEvent> findAllByActionAndRepoId(String action, String repo);

    @Query("SELECT * FROM issue_event ie WHERE ie.action = :action AND ie.repo = :repo AND ie.issue_number = :issueNumber")
    Mono<IssueEvent> findByActionAndIssueIdAndRepoId(String action, String issueNumber, String repo);
}
