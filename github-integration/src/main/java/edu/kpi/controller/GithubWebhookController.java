package edu.kpi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kpi.client.EventProcessorClient;
import edu.kpi.controller.utils.GithubWebhookSink;
import edu.kpi.dto.EventDto;
import edu.kpi.service.integration.IssueLabelIntegrationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Collections;

@RestController
public class GithubWebhookController {

    private final IssueLabelIntegrationService issueLabelIntegrationService;
    private final EventProcessorClient eventProcessorClient;
    private final ObjectMapper mapper;
    private final GithubWebhookSink eventSink;
    private final Flux<EventDto> eventFlux;

    public GithubWebhookController(final IssueLabelIntegrationService issueLabelIntegrationService, final EventProcessorClient eventProcessorClient) {

        this.issueLabelIntegrationService = issueLabelIntegrationService;
        this.eventProcessorClient = eventProcessorClient;
        this.mapper = new ObjectMapper();
        this.eventSink = new GithubWebhookSink();
        this.eventFlux = Flux.create(eventSink);

        eventProcessorClient.connectToProcessor(eventFlux)
                .log()
                .filter(element -> "opened".equals(element.getAction()))
                .flatMap(issueLabelIntegrationService::addLabelsForIssue)
                .subscribe();
    }

    @PostMapping
    public void handleEvent(@RequestBody final String payload) {

        try {

            JsonNode event = mapper.readTree(payload);

            final EventDto eventDto = EventDto.builder()
                    .installationId(event.get("installation").get("id").asText())
                    .action(event.get("action").asText())
                    .owner(event.get("repository").get("owner").get("login").asText())
                    .repo(event.get("repository").get("name").asText())
                    .issueNumber(event.get("issue").get("number").asText())
                    .labels(Collections.singletonList("Pretty Custom Label"))
                    .build();

            eventSink.publish(eventDto);

//            eventProcessorClient.publishEvent(eventDto)
//                    .log()
//                    .filter(element -> "opened".equals(element.getAction()))
//                    .flatMap(issueLabelIntegrationService::addLabelsForIssue)
//                    .subscribe();


        } catch (JsonProcessingException e) {

            e.printStackTrace();
        }
    }
}

