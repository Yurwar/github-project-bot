package edu.kpi.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix= TwitterProperties.TWITTER_PROP_PREFIX)
public class TwitterProperties {

    public static final String TWITTER_PROP_PREFIX = "twitter";

    private String consumerKey;
    private String consumerSecret;
    private String accessToken;
    private String accessTokenSecret;

}
