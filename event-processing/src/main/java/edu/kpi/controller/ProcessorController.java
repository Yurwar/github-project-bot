package edu.kpi.controller;

import edu.kpi.dto.TweetData;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Controller
@MessageMapping("processorController")
public class ProcessorController {

    @MessageMapping("fetchTweets")
    public Flux<TweetData> fetchTweets(Flux<TweetData> dataFlux) {

        return dataFlux;
    }

    @MessageMapping("keywords")
    public Flux<List<String>> getKeywords() {
        return Flux.just(new ArrayList<>());
    }
}
