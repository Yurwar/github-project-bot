package edu.kpi.service.impl;

import edu.kpi.model.Issue;
import edu.kpi.service.ElasticsearchService;
import edu.kpi.service.IssueService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class DefaultIssueService implements IssueService {
    private final ElasticsearchService elasticsearchService;

    public DefaultIssueService(ElasticsearchService elasticsearchService) {
        this.elasticsearchService = elasticsearchService;
    }

    @Override
    public Flux<Issue> findSimilarIssuesInPast(Issue issue) {
        String title = issue.getTitle();
        String body = issue.getBody();

        Flux<Issue> issuesByTitleKeyword = elasticsearchService.findIssuesByKeyword(title);
        Flux<Issue> issuesByTitleBody = elasticsearchService.findIssuesByKeyword(body);

        return issuesByTitleBody
                .mergeWith(issuesByTitleKeyword);
    }

    @Override
    public void saveIssue(Issue issue) {
        elasticsearchService.saveIssue(issue);
    }
}
