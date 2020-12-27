package edu.kpi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableReactiveElasticsearchRepositories;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication
@EnableR2dbcRepositories(basePackages = "edu.kpi.repository.r2")
@EnableReactiveElasticsearchRepositories(basePackages = "edu.kpi.repository.el")
public class EventProcessingApp {
    public static void main(String[] args) {
        SpringApplication.run(EventProcessingApp.class, args);
    }
}
