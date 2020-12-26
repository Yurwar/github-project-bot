package edu.kpi.controller;

import edu.kpi.service.processing.EventProcessingService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class GithubWebhookController {

    private final Map<String, EventProcessingService> eventProcessingServiceMap;

    public GithubWebhookController(@Qualifier("eventProcessingServiceMap") final Map<String, EventProcessingService> eventProcessingServiceMap) {

        this.eventProcessingServiceMap = eventProcessingServiceMap;
    }

    @PostMapping
    public void handleEvent(@RequestBody final String payload, @RequestHeader("X-Github-Event") final String eventType) {

        System.out.println(eventType);
        System.out.println(payload);

        eventProcessingServiceMap.get(eventType).processEvent(payload);
    }
}

