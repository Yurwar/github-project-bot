package edu.kpi.service.impl;

import edu.kpi.model.Issue;
import edu.kpi.service.ElasticsearchService;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class DefaultElasticsearchService implements ElasticsearchService {
    private final ReactiveElasticsearchOperations elasticsearchOperations;

    public DefaultElasticsearchService(ReactiveElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    @Override
    public void saveIssue(Issue issue) {
        elasticsearchOperations.save(issue)
                .log()
                .subscribe();
    }

    @Override
    public Flux<Issue> findIssuesByKeyword(String keyword) {
        Criteria criteria = new Criteria("body").contains(keyword);
        Query query = new CriteriaQuery(criteria);
        return elasticsearchOperations.search(query, Issue.class)
                .map(SearchHit::getContent);
    }
}
