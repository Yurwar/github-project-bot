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
                .number(event.get("number").asText())
                .sourceBranch(event.get("pull_request").get("head").get("ref").asText())
                .destinationBranch(event.get("pull_request").get("base").get("ref").asText())
                .merged(event.get("pull_request").get("merged").asBoolean())
                .build();
    }
}
