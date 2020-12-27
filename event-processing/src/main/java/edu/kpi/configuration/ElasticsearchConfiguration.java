package edu.kpi.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient;
import org.springframework.data.elasticsearch.client.reactive.ReactiveRestClients;
import org.springframework.data.elasticsearch.config.AbstractReactiveElasticsearchConfiguration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;

@Configuration
public class ElasticsearchConfiguration extends AbstractReactiveElasticsearchConfiguration {

    private final String elasticHost;
    private final String elasticPort;

    public ElasticsearchConfiguration(@Value("${elasticsearch.host}") String elasticHost,
                                      @Value("${elasticsearch.port}") String elasticPort) {
        this.elasticHost = elasticHost;
        this.elasticPort = elasticPort;
    }

    @Override
    public ReactiveElasticsearchClient reactiveElasticsearchClient() {

        ClientConfiguration elasticsearchClientConfiguration = ClientConfiguration.builder()
                .connectedTo(elasticHost + ":" + elasticPort)
                .withWebClientConfigurer(webClient -> {
                    ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                            .codecs(configurer -> configurer.defaultCodecs()
                                    .maxInMemorySize(-1))
                            .build();
                    return webClient.mutate().exchangeStrategies(exchangeStrategies).build();
                })
                .build();

        return ReactiveRestClients.create(elasticsearchClientConfiguration);
    }
}
