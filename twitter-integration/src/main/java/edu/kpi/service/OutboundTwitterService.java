package edu.kpi.service;

import edu.kpi.client.EventProcessorClient;
import edu.kpi.dto.StatisticsData;
import edu.kpi.entities.TweetData;
import edu.kpi.entities.TweetsEvent;
import edu.kpi.mocks.TwitterMock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class OutboundTwitterService {

    private static final Duration INTERVAL_TWEETS = Duration.ofSeconds(5);
    private static final Duration SEARCH_STATISTICS_PERIOD = Duration.ofDays(1);
    private static final Duration SEND_STATISTICS_INTERVAL = Duration.ofSeconds(30);
    private static final int MAX_COUNT_PER_DAY = 100;
    private static final int MILLIS_PER_DAY = 86400000;

    private static final String LOCALE_EN = "en";
    private static final String FILTER_RETWEETS_FILTER_REPLIES = " -filter:retweets -filter:replies";
    private static final String ENV_CONSUMER_KEY = System.getenv("twitter4j.oauth.consumerKey");
    private static final String ENV_CONSUMER_SECRET = System.getenv("twitter4j.oauth.consumerSecret");
    private static final String ENV_ACCESS_TOKEN = System.getenv("twitter4j.oauth.accessToken");
    private static final String ENV_ACCESS_TOKEN_SECRET = System.getenv("twitter4j.oauth.accessTokenSecret");

    private TwitterStream twitterStream;
    private final StatisticsService statisticsService;
    private final EventProcessorClient eventProcessorClient;
    private final Twitter twitter;
    private final TwitterMock twitterMock;
    private final Flux<List<String>> keywordsFlux;
    private final Flux<Integer> countsFlux;

    public OutboundTwitterService(StatisticsService statisticsService, EventProcessorClient eventProcessorClient,
                                  Twitter twitter, TwitterMock twitterMock) {

        this.statisticsService = statisticsService;
        this.eventProcessorClient = eventProcessorClient;
        this.twitter = twitter;
        this.twitterMock = twitterMock;

        this.keywordsFlux = eventProcessorClient.receiveKeywords();
        this.countsFlux = eventProcessorClient.receiveCounts();

        eventProcessorClient.streamTweets(streamTweets());
        eventProcessorClient.streamStatistics(fetchStatisticsDaily());
    }

    public Flux<TweetData> streamTweets() {

        return keywordsFlux
                .flatMap(keywords -> {
                    FilterQuery tweetFilterQuery = new FilterQuery();
                    tweetFilterQuery.track(keywords.toArray(new String[0]));
                    tweetFilterQuery.language(LOCALE_EN);

                    TwitterStream twitterStream = getTwitterStream();

                    return Flux.create(sink -> {
                        twitterStream.onStatus(status -> sink.next(this.trimTweet(status)));
                        twitterStream.onException(sink::error);
                        twitterStream.filter(tweetFilterQuery);
                        sink.onCancel(twitterStream::shutdown);
                    });
                });
    }

    public Flux<TweetsEvent> fetchTweetMocks() {

        return Flux.combineLatest(getKeywordsFluxWithInterval(INTERVAL_TWEETS), countsFlux, Tuples::of)
                .map(tuple -> tuple.getT1().stream()
                        .flatMap(this::getTweetMocks)
                        .distinct()
                        .collect(Collectors.toList()))
                .map(TweetsEvent::new);
    }

    private Flux<List<String>> getKeywordsFluxWithInterval(Duration interval) {

        return Flux.combineLatest(keywordsFlux, Flux.interval(interval), (keywords, intervalEvent) -> keywords);
    }

    public Flux<StatisticsData> fetchStatisticsDaily() {

        return getKeywordsFluxWithInterval(SEND_STATISTICS_INTERVAL)
                .flatMap(keywordList ->
                        Flux.fromStream(keywordList.stream())
                                .map(keywords -> createStatisticsData(keywords, SEARCH_STATISTICS_PERIOD)));
    }

    private StatisticsData createStatisticsData(String keyword, Duration duration) {

        Query query = createQuery(keyword, MAX_COUNT_PER_DAY, duration);

        try {
            List<Status> tweets = twitter
                    .search(query)
                    .getTweets();

            StatisticsData statisticsData = new StatisticsData(tweets
                    .stream()
                    .map(this::trimTweet)
                    .collect(Collectors.groupingBy(data -> "" + data.getSentiment(), Collectors.counting())));
            return statisticsData;
        } catch (TwitterException e) {
            e.printStackTrace();
            return new StatisticsData(new HashMap<>());
        }
    }

    private StatisticsData createStatisticMocksData(String keyword) {

        return new StatisticsData(twitterMock.getMockStatuses(keyword)
                .stream()
                .filter(status -> status
                        .getCreatedAt()
                        .after((new Date(System.currentTimeMillis() - MILLIS_PER_DAY))))
                .map(this::trimTweet)
                .collect(Collectors.groupingBy(data -> "" + data.getSentiment(), Collectors.counting())));
    }

    private TweetData trimTweet(Status status) {

        TweetData tweetData = new TweetData(status.getId(),
                status.getCreatedAt(),
                status.getText(),
                null,
                status.getUser().getName(),
                status.getUser().getScreenName(),
                status.getUser().getProfileImageURL());

        String text = status.getText().trim()
                .replaceAll("http.*?[\\S]+", "") // remove links
                .replaceAll("@[\\S]+", "") // remove usernames
                .replaceAll("#", "") // replace hashtags by just words
                .replaceAll("[\\s]+", " "); // correct all multiple white spaces to a single white space

        tweetData.setText(text);
        tweetData.setSentiment(statisticsService.analyze(text));
        return tweetData;
    }

    private Query createQuery(String keyword, int count, Duration duration) {

        Query query = new Query(keyword.concat(FILTER_RETWEETS_FILTER_REPLIES));
        query.setCount(count);
        query.setLocale("en");
        query.setLang("en");
        query.since(LocalDateTime.now()
                .minus(duration)
                .toString());

        return query;
    }

    private Stream<TweetData> searchTweets(Query query) {

        try {
            return twitter
                    .search(query)
                    .getTweets()
                    .stream()
                    .map(this::trimTweet);
        } catch (TwitterException e) {
            log.error("Twitter Exception", e);
        }

        return Stream.empty();
    }

    private Stream<TweetData> getTweetMocks(String keyword) {

        return twitterMock.getMockStatuses(keyword)
                .stream()
                .map(this::trimTweet);
    }

    private ConfigurationBuilder getConfigurationBuilder() {

        if (Objects.isNull(ENV_CONSUMER_KEY) ||
                Objects.isNull(ENV_CONSUMER_SECRET) ||
                Objects.isNull(ENV_ACCESS_TOKEN) ||
                Objects.isNull(ENV_ACCESS_TOKEN_SECRET)) {

            log.error("Twitter4j properties not configured properly!");
            throw new RuntimeException("Configuration error: Twitter4j properties not configured properly!");
        }

        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder
                .setOAuthConsumerKey(ENV_CONSUMER_KEY)
                .setOAuthConsumerSecret(ENV_CONSUMER_SECRET)
                .setOAuthAccessToken(ENV_ACCESS_TOKEN)
                .setOAuthAccessTokenSecret(ENV_ACCESS_TOKEN_SECRET);

        return configurationBuilder;
    }

    public TwitterStreamFactory getTwitterStreamFactory() {

        ConfigurationBuilder configurationBuilder = getConfigurationBuilder();
        return new TwitterStreamFactory(configurationBuilder.build());
    }

    public TwitterStream getTwitterStream() {

        if (Objects.isNull(this.twitterStream))
            this.twitterStream = getTwitterStreamFactory().getInstance();
        return this.twitterStream;
    }

    public Flux<TweetData> fetchTweets() {
        return getKeywordsFluxWithInterval(INTERVAL_TWEETS)
                .flatMapIterable(keywords -> keywords.stream()
                        .flatMap(keyword -> searchTweets(createQuery(keyword, 5, INTERVAL_TWEETS)))
                        .distinct()
                        .collect(Collectors.toList()));
    }

    public Flux<StatisticsData> fetchStatisticMocksDaily() {

        return getKeywordsFluxWithInterval(SEND_STATISTICS_INTERVAL)
                .flatMap(keywordList ->
                        Flux.fromStream(keywordList.stream())
                                .map(this::createStatisticMocksData));
    }
}
