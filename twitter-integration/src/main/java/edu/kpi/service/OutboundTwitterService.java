package edu.kpi.service;

import twitter4j.*;

import java.util.List;
import java.util.stream.Collectors;

public class OutboundTwitterService {

    public String createTweet(String tweet) throws TwitterException {
        Twitter twitter = getTwitterInstance();
        Status status = twitter.updateStatus("Shitposting");
        return status.getText();
    }

    public List<String> getTimeLine() throws TwitterException {
        Twitter twitter = getTwitterInstance();

        return twitter.getHomeTimeline().stream()
                .map(item -> item.getText())
                .collect(Collectors.toList());
    }

    public static String sendDirectMessage(String recipientName, String msg)
            throws TwitterException {

        Twitter twitter = getTwitterInstance();
        DirectMessage message = twitter.sendDirectMessage(recipientName, msg);
        return message.getText();
    }

    public static List<String> searchTweets() throws TwitterException {

        Twitter twitter = getTwitterInstance();
        Query query = new Query("source:ChocolateWarrior");
        QueryResult result = twitter.search(query);

        return result.getTweets().stream()
                .map(item -> item.getText())
                .collect(Collectors.toList());
    }

    private static Twitter getTwitterInstance() {
        return new TwitterFactory().getInstance();
    }
}
