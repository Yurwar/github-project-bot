package edu.kpi.service;

import edu.kpi.client.EventProcessorClient;
import edu.kpi.dto.StatisticsData;
import edu.kpi.entities.Sentiment;
import edu.kpi.entities.TweetData;
import edu.kpi.entities.TweetsEvent;
import edu.kpi.mocks.TwitterMock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;
import twitter4j.*;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class OutboundTwitterService {

    public static final Duration INTERVAL_TWEETS = Duration.ofSeconds(5);
    private static final int MAX_COUNT_PER_DAY = 10000;
    public static final Duration INTERVAL_STATISTICS = Duration.ofDays(1);
    public static final int MILLIS_PER_DAY = 86400000;
    private static final String FILTER_RETWEETS_FILTER_REPLIES = " -filter:retweets -filter:replies";
    private final StatisticsService statisticsService;
    private final EventProcessorClient eventProcessorClient;
    private final Twitter twitter;
    private final Flux<List<String>> keywordsFlux;
    private final Flux<Integer> countsFlux;
    private final TwitterMock twitterMock;

    public OutboundTwitterService(StatisticsService statisticsService, EventProcessorClient eventProcessorClient,
                                  Twitter twitter, TwitterMock twitterMock) {

        this.statisticsService = statisticsService;
        this.eventProcessorClient = eventProcessorClient;
        this.twitter = twitter;
        this.twitterMock = twitterMock;

        this.keywordsFlux = eventProcessorClient.receiveKeywords();
        this.countsFlux = eventProcessorClient.receiveCounts();

        eventProcessorClient.streamTweets(fetchTweets());
        eventProcessorClient.streamStatistics(fetchStatisticsDaily());
    }

    public Flux<TweetData> fetchTweets() {
        return getKeywordsFluxWithInterval(INTERVAL_TWEETS)
                .flatMapIterable(keywords -> keywords.stream()
                        .flatMap(keyword -> searchTweets(createQuery(keyword, 5)))
                        .distinct()
                        .collect(Collectors.toList()));
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

        return getKeywordsFluxWithInterval(INTERVAL_STATISTICS)
                .flatMap(keywordList ->
                        Flux.fromStream(keywordList.stream())
                                .map(this::createStatisticsData));
    }

    public Flux<StatisticsData> fetchStatisticMocksDaily() {

        return getKeywordsFluxWithInterval(INTERVAL_STATISTICS)
                .flatMap(keywordList ->
                        Flux.fromStream(keywordList.stream())
                                .map(this::createStatisticMocksData));
    }

    private StatisticsData createStatisticsData(String keyword) {

        Query query = createQuery(keyword, MAX_COUNT_PER_DAY);

        try {
            return new StatisticsData(twitter
                    .search(query)
                    .getTweets()
                    .stream()
                    .filter(status -> status
                            .getCreatedAt()
                            .after((new Date(System.currentTimeMillis() - MILLIS_PER_DAY))))
                    .map(this::trimTweet)
                    .collect(Collectors.groupingBy(data -> Sentiment.values()[data.getSentiment()], Collectors.counting())));
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
                .collect(Collectors.groupingBy(data -> Sentiment.values()[data.getSentiment()], Collectors.counting())));

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

    private Query createQuery(String keyword, int count) {

        Query query = new Query(keyword.concat(FILTER_RETWEETS_FILTER_REPLIES));
        query.setCount(count);
        query.setLocale("en");
        query.setLang("en");

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

}
