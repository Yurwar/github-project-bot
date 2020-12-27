package edu.kpi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Twitter Integration Module
// Apart from the core features, the maintainer should be aware of the latest news/feedback on the project.
// Write integration with Twitter API and listen to specific tags related to the project.
// Analyze the feedback and categorize it by sentiments (a.k.a sentiment analysis).
// Collect statistics from the tweets such as tweets per day with categorizations on the sentiments groups.
// Provide daily digest through the notification center
@SpringBootApplication
public class TwitterIntegrationApp {

    public static void main(String[] args) {
        SpringApplication.run(TwitterIntegrationApp.class, args);
    }
}
