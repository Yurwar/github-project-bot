package edu.kpi.converter;

import com.fasterxml.jackson.databind.JsonNode;
import edu.kpi.model.IssueCommentEvent;

public class IssueCommentEventConverter extends CommonEventConverter {

    public static IssueCommentEvent convert(final String event) {

        return readToJsonTree(event)
                .map(IssueCommentEventConverter::convertInternal)
                .orElse(IssueCommentEvent.builder().build());
    }

    private static IssueCommentEvent convertInternal(final JsonNode event) {

        return IssueCommentEvent.builder()
                .installationId(event.get("installation").get("id").asText())
                .action(event.get("action").asText())
                .owner(event.get("repository").get("owner").get("login").asText())
                .repo(event.get("repository").get("name").asText())
                .body(event.get("comment").get("body").asText())
                .senderType(event.get("sender").get("type").asText())
                .login(event.get("sender").get("login").asText())
                .senderUrl(event.get("sender").get("html_url").asText())
                .commentUrl(event.get("comment").get("html_url").asText())
                .build();
    }
}
