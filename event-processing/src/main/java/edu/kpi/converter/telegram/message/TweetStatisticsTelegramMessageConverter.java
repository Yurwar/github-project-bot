package edu.kpi.converter.telegram.message;

import edu.kpi.converter.Converter;
import edu.kpi.dto.StatisticsData;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static edu.kpi.utils.Constants.Telegram.Message.ANGRY_FACE;
import static edu.kpi.utils.Constants.Telegram.Message.CALENDAR;
import static edu.kpi.utils.Constants.Telegram.Message.NEUTRAL_FACE;
import static edu.kpi.utils.Constants.Telegram.Message.RAGE_FACE;
import static edu.kpi.utils.Constants.Telegram.Message.RELAXED_FACE;
import static edu.kpi.utils.Constants.Telegram.Message.STAR;

@Component
public class TweetStatisticsTelegramMessageConverter implements Converter<StatisticsData, String> {

    private static final Map<Long, String> labelsMap =
            Map.ofEntries(
                    Map.entry(1L, "Very Negative: "),
                    Map.entry(2L, "Negative: "),
                    Map.entry(3L, "Neutral: "),
                    Map.entry(4L, "Positive: "),
                    Map.entry(5L, "Very Positive: "));

    private static final Map<Integer, String> emotionsMap =
            Map.ofEntries(
                    Map.entry(1, RAGE_FACE),
                    Map.entry(2, ANGRY_FACE),
                    Map.entry(3, NEUTRAL_FACE),
                    Map.entry(4, RELAXED_FACE),
                    Map.entry(5, STAR));

    @Override
    public String convert(StatisticsData source) {

        StringBuilder result = new StringBuilder();

        result.append(CALENDAR).append(" - *[TWEETS STATISTICS]*\n");

        long sumOfTweets = source.getSentimentToCountMap().values()
                .stream()
                .mapToLong(i -> i)
                .sum();

        source.getSentimentToCountMap().entrySet()
                .stream().map(entry -> Map.entry(Integer.parseInt(entry.getKey()) + 1, entry.getValue()))
                .sorted(Map.Entry.<Integer, Long>comparingByKey().reversed())
                .map(entry -> createRating(entry.getKey(), entry.getValue(), sumOfTweets))
                .forEach(result::append);

        return result.toString();
    }

    private String createRating(long sentiment, long tweetsBySentiment, long sumOfTweets) {

        double percent = ((double) tweetsBySentiment) / sumOfTweets;
        int chunks = ((int) (percent * 100)) / 10;

        System.out.println("CHUNKS: " + chunks);

        String chartLine = IntStream.range(0, chunks)
                .mapToObj(index -> emotionsMap.get((int) sentiment))
                .collect(Collectors.joining());

        return labelsMap.get(sentiment) + new DecimalFormat("##.##%").format(percent) + " - " + chartLine + "\n";
    }
}
