package edu.kpi.converter.telegram.message;

import edu.kpi.converter.Converter;
import edu.kpi.dto.TweetData;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static edu.kpi.utils.Constants.Telegram.Message.ANGRY_FACE;
import static edu.kpi.utils.Constants.Telegram.Message.NEUTRAL_FACE;
import static edu.kpi.utils.Constants.Telegram.Message.RAGE_FACE;
import static edu.kpi.utils.Constants.Telegram.Message.RELAXED_FACE;
import static edu.kpi.utils.Constants.Telegram.Message.SPEAKING_HEAD;
import static edu.kpi.utils.Constants.Telegram.Message.STAR;

@Component
public class TweetMessageConverter implements Converter<TweetData, String> {

    private static final Map<Integer, String> emotionsMap =
            Map.ofEntries(
                    Map.entry(1, RAGE_FACE),
                    Map.entry(2, ANGRY_FACE),
                    Map.entry(3, NEUTRAL_FACE),
                    Map.entry(4, RELAXED_FACE),
                    Map.entry(5, STAR));

    private static final String TWITTER_PREFIX = "http://twitter.com/";
    private static final int TWEET_MAX_LENGTH = 80;
    private static final String TWEET_POSTFIX = "...";

    @Override
    public String convert(TweetData source) {

        StringBuilder result = new StringBuilder();

        result.append(SPEAKING_HEAD).append(" - *[TWEET PUBLISHED]*\n");
        result.append("*Author*: ").append("[").append(source.getUserName()).append("](").append(TWITTER_PREFIX).append(source.getUserName()).append(")\n");
        result.append("_\"").append(getText(source.getText())).append("\"_\n");
        result.append("*Emotion:* ").append(getEmotion(source.getSentiment() + 1));

        return result.toString();
    }

    private String getText(String body) {

        return Optional.of(body)
                .filter(description -> description.length() <= TWEET_MAX_LENGTH)
                .map(description -> description + "\n")
                .orElse(body.substring(0, Math.min(body.length(), TWEET_MAX_LENGTH)).trim() + TWEET_POSTFIX);
    }

    private String getEmotion(int sentiment) {

        return IntStream.rangeClosed(1, sentiment)
                .mapToObj(index -> emotionsMap.get(sentiment))
                .collect(Collectors.joining());
    }
}
