package edu.kpi.util;

import edu.kpi.config.TwitterProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.util.Objects;

@Slf4j
@Component
public class TwitterUtils {

    private TwitterProperties properties;

    @Autowired
    public TwitterUtils(TwitterProperties properties) {
        this.properties = properties;
    }

    public Twitter getTwitterInstance() {

        return TwitterFactory.getSingleton();
    }

    private ConfigurationBuilder getConfigurationBuilder() {

        if (isAnyPropertyNull()) {
            log.error("Twitter4j properties not configured properly!");
            throw new RuntimeException("Configuration error: Twitter4j properties not configured properly!");
        }

        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setOAuthConsumerKey(properties.getConsumerKey())
                .setOAuthConsumerSecret(properties.getConsumerSecret())
                .setOAuthAccessToken(properties.getAccessToken())
                .setOAuthAccessTokenSecret(properties.getAccessTokenSecret());

        return configurationBuilder;
    }

    private boolean isAnyPropertyNull() {
        return Objects.isNull(properties.getConsumerKey())
                || Objects.isNull(properties.getConsumerSecret())
                || Objects.isNull(properties.getAccessToken())
                || Objects.isNull(properties.getAccessTokenSecret());
    }
}
