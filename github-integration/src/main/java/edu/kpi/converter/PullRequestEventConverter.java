package edu.kpi.converter;

import com.fasterxml.jackson.databind.JsonNode;
import edu.kpi.model.PullRequestEvent;

public class PullRequestEventConverter extends CommonEventConverter {

    public static PullRequestEvent convert(final String event) {

        return readToJsonTree(event)
                .map(PullRequestEventConverter::convertInternal)
                .orElse(PullRequestEvent.builder().build());
    }

    private static PullRequestEvent convertInternal(final JsonNode event) {

        return PullRequestEvent.builder()
                .installationId(event.get("installation").get("id").asText())
                .action(event.get("action").asText())
                .owner(event.get("repository").get("owner").get("login").asText())
                .repo(event.get("repository").get("name").asText())
                .build();
    }
}
