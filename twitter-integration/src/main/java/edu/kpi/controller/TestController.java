package edu.kpi.controller;

import edu.kpi.entities.TweetData;
import edu.kpi.service.OutboundTwitterService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping
public class TestController {

    private final OutboundTwitterService outboundTwitterService;

    public TestController(OutboundTwitterService outboundTwitterService) {
        this.outboundTwitterService = outboundTwitterService;
    }

    @GetMapping("/test")
    public Flux<List<TweetData>> getTweets() {

        return outboundTwitterService.fetchTweets(5);
    }
}
