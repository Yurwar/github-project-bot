package edu.kpi.service;

import edu.kpi.client.EventProcessorClient;
import edu.kpi.dto.StatisticsData;
import edu.kpi.entities.Sentiment;
import edu.kpi.entities.TweetData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;
import twitter4j.*;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class OutboundTwitterService {

    private static final int MAX_COUNT_PER_DAY = 10000;
    private static final String FILTER_RETWEETS_FILTER_REPLIES = " -filter:retweets -filter:replies";
    private final StatisticsService statisticsService;
    private final EventProcessorClient eventProcessorClient;
    private final Twitter twitter;
    private final Flux<List<String>> keywordsFlux;
    private final Flux<Integer> countsFlux;

    public OutboundTwitterService(StatisticsService statisticsService, EventProcessorClient eventProcessorClient, Twitter twitter) {

        this.statisticsService = statisticsService;
        this.eventProcessorClient = eventProcessorClient;
        this.twitter = twitter;

        this.keywordsFlux = eventProcessorClient.receiveKeywords();
        this.countsFlux = eventProcessorClient.receiveCounts();
    }

    public Flux<List<TweetData>> fetchTweets() {

        return Flux.combineLatest(getKeywordsFluxWithInterval(), countsFlux, Tuples::of)
                .map(tuple -> tuple.getT1().stream()
                        .flatMap(keyword -> searchTweets(createQuery(keyword, tuple.getT2())))
                        .distinct()
                        .collect(Collectors.toList()));
    }

    private Flux<List<String>> getKeywordsFluxWithInterval() {

        return Flux.combineLatest(keywordsFlux, Flux.interval(Duration.ofSeconds(5)), (keywords, interval) -> keywords);
    }

    //todo: search by list of keywords, suggestion: use flux join
    public Flux<StatisticsData> fetchStatisticsDaily(String keyword) {

        Query query = createQuery(keyword, MAX_COUNT_PER_DAY);

        return Flux.interval(Duration.ofDays(1)).map(instance -> {
            try {
                return new StatisticsData(twitter
                        .search(query)
                        .getTweets()
                        .stream()
                        .filter(status -> status
                                .getCreatedAt()
                                .after((new Date(System.currentTimeMillis() - 86400000))))
                        .map(this::trimTweet)
                        .collect(Collectors.groupingBy(data -> Sentiment.values()[data.getSentiment()], Collectors.counting())));
            } catch (TwitterException e) {
                e.printStackTrace();
                return new StatisticsData(new HashMap<>());
            }
        });
    }

//    public Flux<TweetData> streamTweets(String keyword){
//
//        TwitterStream stream = config.twitterStream();
//        FilterQuery tweetFilterQuery = new FilterQuery();
//        tweetFilterQuery.track(new String[]{keyword});
//        tweetFilterQuery.language(new String[]{"en"});
//        return Flux.create(sink -> {
//            stream.onStatus(status -> sink.next(this.trimTweet(status)));
//            stream.onException(sink::error);
//            stream.filter(tweetFilterQuery);
//            sink.onCancel(stream::shutdown);
//        });
//    }

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
}
