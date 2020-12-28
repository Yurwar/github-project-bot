package edu.kpi.repository.data;

import edu.kpi.model.data.IssueCommentEvent;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface IssueCommentEventRepository extends R2dbcRepository<IssueCommentEvent, Long> {
}
