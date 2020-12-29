package edu.kpi.converter;

import com.fasterxml.jackson.databind.JsonNode;
import edu.kpi.model.IssueEvent;

import java.util.Optional;

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
                .id(event.get("issue").get("id").asText())
                .title(event.get("issue").get("title").asText())
                .body(event.get("issue").get("body").asText())
                .issueNumber(event.get("issue").get("number").asText())
                .url(event.get("issue").get("html_url").asText())
                .label(getLabel(event))
                .build();
    }

    private static String getLabel(final JsonNode event) {

        return Optional.of(event)
                .filter(node -> node.has("label"))
                .map(node -> node.get("label").get("name").asText())
                .orElse(null);
    }
}
