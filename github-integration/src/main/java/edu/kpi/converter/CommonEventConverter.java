package edu.kpi.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

public class CommonEventConverter {

    private static final ObjectMapper mapper = new ObjectMapper();

    protected static Optional<JsonNode> readToJsonTree(final String payload) {

        try {

            return Optional.of(mapper.readTree(payload));

        } catch (JsonProcessingException e) {

            e.printStackTrace();
        }

        return Optional.empty();
    }
}
