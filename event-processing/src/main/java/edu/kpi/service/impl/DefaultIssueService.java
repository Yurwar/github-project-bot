package edu.kpi.service.impl;

import edu.kpi.dto.IssueEventDto;
import edu.kpi.model.index.Issue;
import edu.kpi.service.ElasticsearchService;
import edu.kpi.service.IssueService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DefaultIssueService implements IssueService {

    private static final int MIN_SYMBOLS_IN_KEYWORD = 3;
    private static final int MAX_KEYWORDS_COUNT = 2;

    private final ElasticsearchService elasticsearchService;

    public DefaultIssueService(ElasticsearchService elasticsearchService) {
        this.elasticsearchService = elasticsearchService;
    }

    @Override
    public Flux<Issue> findSimilarIssue(IssueEventDto issue) {
        String title = issue.getTitle();
        String body = issue.getBody();

        String fullDescription = title + " " + body;

        List<String> keywords = findKeywords(fullDescription);

        return elasticsearchService.findIssuesByKeywords(keywords);
    }

    @Override
    public Mono<Issue> saveIssueEvent(Issue issue) {

        return elasticsearchService.saveIssue(issue);
    }

    private List<String> findKeywords(String fullDescription) {

        Map<String, Long> wordsEntryCount = Arrays.stream(fullDescription.split("\\W+"))
                .filter(str -> !str.isBlank())
                .map(String::strip)
                .filter(str -> str.length() > MIN_SYMBOLS_IN_KEYWORD)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        Comparator<Map.Entry<String, Long>> reversed = getEntryReversedComparator();

        return wordsEntryCount.entrySet()
                .stream()
                .sorted(reversed)
                .limit(MAX_KEYWORDS_COUNT)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private Comparator<Map.Entry<String, Long>> getEntryReversedComparator() {
        Comparator<Map.Entry<String, Long>> plainComparator = Comparator
                .comparingLong(Map.Entry::getValue);

        return plainComparator.reversed();
    }
}
