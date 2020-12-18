package edu.kpi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kpi.dto.IssueLabelDto;
import edu.kpi.service.integration.IssueLabelIntegrationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
public class GithubWebhookController {

    private final IssueLabelIntegrationService issueLabelIntegrationService;
    private final ObjectMapper mapper;

    public GithubWebhookController(final IssueLabelIntegrationService issueLabelIntegrationService) {

        this.issueLabelIntegrationService = issueLabelIntegrationService;
        this.mapper = new ObjectMapper();
    }

    @PostMapping
    public void handleEvent(@RequestBody final String payload) {

        try {

            JsonNode event = mapper.readTree(payload);

            final IssueLabelDto issueLabelDto = IssueLabelDto.builder()
                    .installationId(event.get("installation").get("id").asText())
                    .owner(event.get("sender").get("login").asText())
                    .repo(event.get("repository").get("name").asText())
                    .issueNumber(event.get("issue").get("number").asText())
                    .labels(Collections.singletonList("Pretty Custom Label"))
                    .build();


            issueLabelIntegrationService.addLabelsForIssue(issueLabelDto);

        } catch (JsonProcessingException e) {

            e.printStackTrace();
        }
    }
}
