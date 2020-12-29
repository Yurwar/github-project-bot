package edu.kpi.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.util.MimeTypeUtils;

import java.net.URI;

@Configuration
public class EventProcessorClientConfiguration {

    private final String schema;
    private final String host;
    private final String port;

    public EventProcessorClientConfiguration(@Value("${event-processing.server.scheme}") String schema,
                                             @Value("${event-processing.server.host}") String host,
                                             @Value("${event-processing.server.port}") String port) {

        this.schema = schema;
        this.host = host;
        this.port = port;
    }

    @Bean
    RSocketRequester eventProcessorSocketRequester(RSocketRequester.Builder builder, RSocketStrategies strategies) {

        return builder
                .setupRoute("twitter-integration")
                .rsocketStrategies(strategies)
                .dataMimeType(MimeTypeUtils.APPLICATION_JSON)
                .websocket(URI.create(String.format("%s://%s:%s", schema, host, port)));
    }


}
