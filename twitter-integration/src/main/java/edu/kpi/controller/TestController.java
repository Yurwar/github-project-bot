package edu.kpi.controller;

import edu.kpi.entities.TweetsEvent;
import edu.kpi.service.OutboundTwitterService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping
public class TestController {

    private final OutboundTwitterService outboundTwitterService;

    public TestController(OutboundTwitterService outboundTwitterService) {
        this.outboundTwitterService = outboundTwitterService;
    }

    @GetMapping("/test")
    public Flux<TweetsEvent> getTweets() {

//        return outboundTwitterService.fetchTweets();
        return outboundTwitterService.fetchTweetMocks();
    }
}
