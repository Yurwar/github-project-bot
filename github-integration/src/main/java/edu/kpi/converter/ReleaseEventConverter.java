package edu.kpi.converter;

import com.fasterxml.jackson.databind.JsonNode;
import edu.kpi.model.ReleaseEvent;

public class ReleaseEventConverter extends CommonEventConverter {

    public static ReleaseEvent convert(final String event) {

        return readToJsonTree(event)
                .map(ReleaseEventConverter::convertInternal)
                .orElse(ReleaseEvent.builder().build());
    }

    private static ReleaseEvent convertInternal(final JsonNode event) {

        return ReleaseEvent.builder()
                .installationId(event.get("installation").get("id").asText())
                .action(event.get("action").asText())
                .owner(event.get("repository").get("owner").get("login").asText())
                .repo(event.get("repository").get("name").asText())
                .build();
    }
}
