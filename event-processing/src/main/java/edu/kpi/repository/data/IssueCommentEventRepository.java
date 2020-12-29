package edu.kpi.repository.data;

import edu.kpi.model.data.IssueCommentEvent;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface IssueCommentEventRepository extends R2dbcRepository<IssueCommentEvent, Long> {
    Flux<IssueCommentEvent> findAllByRepo(String repo);

    Flux<IssueCommentEvent> findAllByRepoAndIssueNumber(String repo, String issueNumber);
}
