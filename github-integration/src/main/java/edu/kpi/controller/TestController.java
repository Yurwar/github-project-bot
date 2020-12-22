package edu.kpi.controller;

import edu.kpi.client.EventProcessorClient;
import edu.kpi.model.Message;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


@RestController
public class TestController {
    private final EventProcessorClient eventProcessorClient;

    public TestController(EventProcessorClient eventProcessorClient) {
        this.eventProcessorClient = eventProcessorClient;
    }

    @GetMapping(value = "/event/{id}")
    public Flux<Message> testEventProcessor(@PathVariable String id) {
        return eventProcessorClient
                .getMessage(id)
                .log();
    }
}
