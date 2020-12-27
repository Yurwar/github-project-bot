package edu.kpi.controller;

import edu.kpi.service.StatisticService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping
public class TestController {
    private final StatisticService statisticService;

    public TestController(StatisticService statisticService) {
        this.statisticService = statisticService;
    }


    @GetMapping("/test")
    public Mono<Long> test() {
        return statisticService.getNumberOfIssuesByAction("opened", "github-webhook-test-repo");
    }
}
