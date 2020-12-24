package edu.kpi.converter;

import com.fasterxml.jackson.databind.JsonNode;
import edu.kpi.model.IssueEvent;

public class IssueEventConverter extends CommonEventConverter {

    public static IssueEvent convert(final String event) {

        return readToJsonTree(event)
                .map(IssueEventConverter::convertInternal)
                .orElse(IssueEvent.builder().build());
    }

    private static IssueEvent convertInternal(final JsonNode event) {

        return IssueEvent.builder()
                .installationId(event.get("installation").get("id").asText())
                .action(event.get("action").asText())
                .owner(event.get("repository").get("owner").get("login").asText())
                .repo(event.get("repository").get("name").asText())
                .issueNumber(event.get("issue").get("number").asText())
                .build();
    }
}
