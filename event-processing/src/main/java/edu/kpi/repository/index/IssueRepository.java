package edu.kpi.repository.index;

import edu.kpi.model.index.Issue;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IssueRepository extends ReactiveElasticsearchRepository<Issue, String> {
}
