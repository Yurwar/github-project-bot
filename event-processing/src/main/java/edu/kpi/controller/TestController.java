package edu.kpi.controller;

import edu.kpi.model.data.IssueEvent;
import edu.kpi.repository.data.IssueEventRepository;
import edu.kpi.service.StatisticService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping
public class TestController {
    private static final String GITHUB_WEBHOOK_TEST_REPO = "github-webhook-test-repo";
    private final StatisticService statisticService;
    private final IssueEventRepository issueEventRepository;

    public TestController(StatisticService statisticService,
                          IssueEventRepository issueEventRepository) {
        this.statisticService = statisticService;
        this.issueEventRepository = issueEventRepository;
    }


    @GetMapping("/test")
    public Mono<List<IssueEvent>> test() {
        return Mono.empty();
//        return statisticService.getUnclosedEvents(GITHUB_WEBHOOK_TEST_REPO);
    }
}
