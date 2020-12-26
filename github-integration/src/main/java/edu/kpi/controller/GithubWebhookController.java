package edu.kpi.controller;

import edu.kpi.service.processing.EventProcessingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
public class GithubWebhookController {

    private final Map<String, EventProcessingService> eventProcessingServiceMap;

    public GithubWebhookController(@Qualifier("eventProcessingServiceMap") final Map<String, EventProcessingService> eventProcessingServiceMap) {

        this.eventProcessingServiceMap = eventProcessingServiceMap;
    }

    @PostMapping
    public void handleEvent(@RequestBody final String payload, @RequestHeader("X-Github-Event") final String eventType) {

        log.info(eventType);
        log.info(payload);

        eventProcessingServiceMap.get(eventType).processEvent(payload);
    }
}

