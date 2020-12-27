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
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.function.BinaryOperator;

@Service
public class DefaultElasticsearchService implements ElasticsearchService {
    private final ReactiveElasticsearchOperations elasticsearchOperations;

    public DefaultElasticsearchService(ReactiveElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    @Override
    public Mono<Issue> saveIssue(Issue issue) {
        return elasticsearchOperations.save(issue);
    }

    @Override
    public Flux<Issue> findIssuesByKeywords(List<String> keywords) {
        Optional<Criteria> bodyCriteriaOpt = getConcatenatedFuzzyCriteria(keywords, "body", Criteria::and);

        Optional<Criteria> titleCriteriaOpt = getConcatenatedFuzzyCriteria(keywords, "title", Criteria::and);

        Optional<Criteria> multiCriteriaOpt = bodyCriteriaOpt.flatMap(bodyCriteria -> titleCriteriaOpt.map(bodyCriteria::or));

        Criteria multiCriteria = multiCriteriaOpt.orElseThrow();

        Query query = new CriteriaQuery(multiCriteria);
        return elasticsearchOperations.search(query, Issue.class)
                .log("ELASTICSEARCH")
                .map(SearchHit::getContent);
    }

    private Optional<Criteria> getConcatenatedFuzzyCriteria(List<String> keywords, String field, BinaryOperator<Criteria> acc) {
        return keywords.stream()
                .map(keyword -> new Criteria(field).contains(keyword))
                .reduce(acc);
    }
}
