package edu.kpi.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix= TwitterConfiguration.TWITTER_PROP_PREFIX)
public class TwitterConfiguration {

    public static final String TWITTER_PROP_PREFIX = "twitter";
    private TwitterStream twitterStream;

    private String consumerKey;
    private String consumerSecret;
    private String accessToken;
    private String accessTokenSecret;

    @Bean
    public Twitter twitter() {

        return TwitterFactory.getSingleton();
    }
}
