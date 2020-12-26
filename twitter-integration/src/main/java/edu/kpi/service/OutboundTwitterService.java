package edu.kpi.service;

import edu.kpi.client.EventProcessorClient;
import edu.kpi.dto.StatisticsData;
import edu.kpi.entities.Sentiment;
import edu.kpi.entities.TweetData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import twitter4j.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OutboundTwitterService {

    private final StatisticsService statisticsService;
    private final EventProcessorClient eventProcessorClient;

    public OutboundTwitterService(StatisticsService statisticsService, EventProcessorClient eventProcessorClient) {
        this.statisticsService = statisticsService;
        this.eventProcessorClient = eventProcessorClient;
    }

    private static Twitter getTwitterInstance() {
        return new TwitterFactory().getInstance();
    }

    public Mono<Flux<TweetData>> fetchTweets(int count) {

        Flux<List<String>> keywordsFlux = eventProcessorClient.receiveKeywords();

        return keywordsFlux.map(keywords -> keywords.stream() //List<Flux<TweetData>> list for one keyword
                .map(keyword -> {
                    Twitter twitter = getTwitterInstance();
                    Query query = new Query(keyword.concat(" -filter:retweets -filter:replies"));
                    query.setCount(count);
                    query.setLocale("en");
                    query.setLang("en");

                    try {
                        return Flux.fromStream(twitter
                                .search(query)
                                .getTweets()
                                .stream())
                                .map(this::trimTweet);
                    } catch (TwitterException e) {
                        log.error("Twitter Exception", e);
                    }

                    return Flux.<TweetData>empty();
                })
                .collect(Collectors.toList()))
                .reduce((list1, list2) -> {
                    List<Flux<TweetData>> res = new ArrayList<>();
                    res.addAll(list1);
                    res.addAll(list2);
                    return res;
                }).map(list -> {
                    if (!list.isEmpty()) {
                        Flux<TweetData> resultFlux = list.get(0);
                        list.remove(0);
                        for (Flux<TweetData> tweetDataFlux : list) {
                            resultFlux = resultFlux.mergeWith(tweetDataFlux);
                        }
                        return resultFlux;
                    } else {
                        return Flux.empty();
                    }
                });
//                .map(x -> x); //todo: ask Roma
    }

    //todo: search by list of keywords, suggestion: use flux join
    public Flux<StatisticsData> fetchStatisticsDaily(String keyword, int count) {

        Twitter twitter = getTwitterInstance();
        Query query = new Query(keyword.concat(" -filter:retweets -filter:replies"));
        query.setCount(count); //todo: mind removing count
        query.setLocale("en");
        query.setLang("en");

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

//    public Flux<StatisticsData> fetchTweetsDaily(int count) {
//
//        Flux<List<String>> keywordsFlux = eventProcessorClient.receiveKeywords();
//
//        return keywordsFlux.map(keywords -> keywords.stream() //List<Flux<TweetData>> list for one keyword
//                .map(keyword -> {
//                    Twitter twitter = getTwitterInstance();
//                    Query query = new Query(keyword.concat(" -filter:retweets -filter:replies"));
//                    query.setCount(count);
//                    query.setLocale("en");
//                    query.setLang("en");
//
//                    try {
//                        return new StatisticsData(twitter
//                                .search(query)
//                                .getTweets()
//                                .stream()
//                                .filter(status -> status
//                                        .getCreatedAt()
//                                        .after((new Date(System.currentTimeMillis() - 86400000))))
//                                .map(this::trimTweet)
//                                .collect(Collectors.groupingBy(data -> Sentiment.values()[data.getSentiment()], Collectors.counting())));
//                    } catch (TwitterException e) {
//                        log.error("Twitter Exception", e);
//                    }
//
//                    return Flux.<TweetData>empty();
//                })
//                .collect(Collectors.toList()))
//                .reduce((list1, list2) -> {
//                    List<Flux<TweetData>> res = new ArrayList<>();
//                    res.addAll(list1);
//                    res.addAll(list2);
//                    return res;
//                }).map(list -> {
//                    if (!list.isEmpty()) {
//                        Flux<TweetData> resultFlux = list.get(0);
//                        list.remove(0);
//                        for (Flux<TweetData> tweetDataFlux : list) {
//                            resultFlux = resultFlux.mergeWith(tweetDataFlux);
//                        }
//                        return resultFlux;
//                    } else {
//                        return Flux.empty();
//                    }
//                }).flatMap(x -> x); //todo: ask Roma
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

//    public Flux<TweetData> streamTweets(String keyword) {
//
//        TwitterStream stream = config.twitterStream();
//        FilterQuery tweetFilterQuery = new FilterQuery();
//        tweetFilterQuery.track(new String[]{keyword});
//        tweetFilterQuery.language(new String[]{"en"});
//
//        return Flux.create(sink -> {
//            stream.onStatus(tweet -> sink.next(this.trimTweet(tweet)));
//            stream.onException(sink::error);
//            stream.filter(tweetFilterQuery);
//            sink.onCancel(stream::shutdown);
//        });
//    }
}
