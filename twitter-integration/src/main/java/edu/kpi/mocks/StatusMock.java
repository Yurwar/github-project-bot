package edu.kpi.mocks;

import twitter4j.*;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class StatusMock implements Status {

    public static final int BOUND_HOURS = 48;
    public static final int BOUND_ID = 10000000;
    private static final Random RANDOM = new Random();
    private static final String TWEETS_FILENAME = "twitter-integration/src/main/resources/mock-tweets.txt";
    private static final String REACTIONS_FILENAME = "twitter-integration/src/main/resources/mock-reactions.txt";

    @Override
    public Date getCreatedAt() {
        return Date.from(LocalDateTime.now()
                .minusHours(RANDOM.nextInt(BOUND_HOURS))
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    @Override
    public long getId() {
        return RANDOM.nextInt(BOUND_ID);
    }

    @Override
    public String getText() {
        try (BufferedInputStream ignored = new BufferedInputStream(new FileInputStream(TWEETS_FILENAME))) {
            List<String> tweetTexts = Files.lines(Path.of(TWEETS_FILENAME)).collect(Collectors.toList());
            List<String> reactions = Files.lines(Path.of(REACTIONS_FILENAME)).collect(Collectors.toList());
            return "" + tweetTexts.get(RANDOM.nextInt(tweetTexts.size())) + reactions.get(RANDOM.nextInt(reactions.size()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getDisplayTextRangeStart() {
        return 0;
    }

    @Override
    public int getDisplayTextRangeEnd() {
        return 0;
    }

    @Override
    public String getSource() {
        return null;
    }

    @Override
    public boolean isTruncated() {
        return false;
    }

    @Override
    public long getInReplyToStatusId() {
        return 0;
    }

    @Override
    public long getInReplyToUserId() {
        return 0;
    }

    @Override
    public String getInReplyToScreenName() {
        return null;
    }

    @Override
    public GeoLocation getGeoLocation() {
        return null;
    }

    @Override
    public Place getPlace() {
        return null;
    }

    @Override
    public boolean isFavorited() {
        return false;
    }

    @Override
    public boolean isRetweeted() {
        return false;
    }

    @Override
    public int getFavoriteCount() {
        return 0;
    }

    @Override
    public User getUser() {
        return new UserMock();
    }

    @Override
    public boolean isRetweet() {
        return false;
    }

    @Override
    public Status getRetweetedStatus() {
        return null;
    }

    @Override
    public long[] getContributors() {
        return new long[0];
    }

    @Override
    public int getRetweetCount() {
        return 0;
    }

    @Override
    public boolean isRetweetedByMe() {
        return false;
    }

    @Override
    public long getCurrentUserRetweetId() {
        return 0;
    }

    @Override
    public boolean isPossiblySensitive() {
        return false;
    }

    @Override
    public String getLang() {
        return null;
    }

    @Override
    public Scopes getScopes() {
        return null;
    }

    @Override
    public String[] getWithheldInCountries() {
        return new String[0];
    }

    @Override
    public long getQuotedStatusId() {
        return 0;
    }

    @Override
    public Status getQuotedStatus() {
        return null;
    }

    @Override
    public URLEntity getQuotedStatusPermalink() {
        return null;
    }

    @Override
    public int compareTo(Status o) {
        return 0;
    }

    @Override
    public UserMentionEntity[] getUserMentionEntities() {
        return new UserMentionEntity[0];
    }

    @Override
    public URLEntity[] getURLEntities() {
        return new URLEntity[0];
    }

    @Override
    public HashtagEntity[] getHashtagEntities() {
        return new HashtagEntity[0];
    }

    @Override
    public MediaEntity[] getMediaEntities() {
        return new MediaEntity[0];
    }

    @Override
    public SymbolEntity[] getSymbolEntities() {
        return new SymbolEntity[0];
    }

    @Override
    public RateLimitStatus getRateLimitStatus() {
        return null;
    }

    @Override
    public int getAccessLevel() {
        return 0;
    }
}
