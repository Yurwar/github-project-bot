package edu.kpi.converter.telegram.message;

import edu.kpi.converter.Converter;
import edu.kpi.model.data.Statistic;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static edu.kpi.utils.Constants.Telegram.Message.CHART_SYMBOL;
import static edu.kpi.utils.Constants.Telegram.Message.DIAMOND_SYMBOL;

@Component
public class StatisticTelegramMessageConverter implements Converter<Statistic, String> {

    @Override
    public String convert(Statistic fromObject) {
        StringBuilder result = new StringBuilder();

        result.append(CHART_SYMBOL).append(" - *[STATISTIC for ").append(fromObject.getRepo()).append(" repository]*\n");
        result.append(DIAMOND_SYMBOL).append(" Average time between create and close: *").append(fromObject.getAverageTimeBetweenCreateAndClose()).append("*\n");
        result.append(DIAMOND_SYMBOL).append(" Average time between create and response: *").append(fromObject.getAverageTimeBetweenCreateAndComment()).append("*\n");
        result.append(DIAMOND_SYMBOL).append(" Number of issues created per week: *").append(fromObject.getNumberOfIssuesCreatedPerWeek()).append("*\n");
        result.append(DIAMOND_SYMBOL).append(" Number of issues closed per week: *").append(fromObject.getNumberOfIssuesClosedPerWeek()).append("*\n");
        result.append(DIAMOND_SYMBOL).append(" Most mentioned topic: *").append(fromObject.getMostMentionedTopic()).append("*\n");
        result.append(DIAMOND_SYMBOL).append(" Waiting for response issues: *").append(convertIssueList(fromObject.getWaitingForResponseIssues())).append("*\n");
        result.append(DIAMOND_SYMBOL).append(" Unanswered issues: *").append(convertIssueList(fromObject.getUnansweredIssues())).append("*");

        return result.toString();
    }

    private String convertIssueList(List<String> source) {
        return source.stream().map(issue -> "#" + issue)
                .collect(Collectors.joining(", "));
    }
}
