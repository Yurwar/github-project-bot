package edu.kpi.converter.telegram.message;

import edu.kpi.converter.Converter;
import edu.kpi.dto.PullRequestEventDto;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

import static edu.kpi.utils.Constants.CLOSED;
import static edu.kpi.utils.Constants.DECLINED;
import static edu.kpi.utils.Constants.MERGED;
import static edu.kpi.utils.Constants.Telegram.Message.CHECK_MARK;
import static edu.kpi.utils.Constants.Telegram.Message.CROSS_MARK;
import static edu.kpi.utils.Constants.Telegram.Message.RIGHT_ARROW;
import static edu.kpi.utils.Constants.Telegram.Message.TWISTED_ARROWS;

@Component
public class PullRequestTelegramMessageConverter implements Converter<PullRequestEventDto, String> {

    private static final Map<Boolean, String> closedIndicatorsMap =
            Map.ofEntries(
                    Map.entry(true, CHECK_MARK),
                    Map.entry(false, CROSS_MARK));

    @Override
    public String convert(PullRequestEventDto source) {

        StringBuilder result = new StringBuilder();

        result.append(getIcon(source)).append(" - *[PULL REQUEST ").append(getAction(source)).append("]*\n");
        result.append("*Title:* ").append("[").append(source.getTitle()).append("](").append(source.getUrl()).append(")\n");
        result.append("*Branches:* ").append(source.getSourceBranch()).append(" " + RIGHT_ARROW + " ").append(source.getDestinationBranch()).append("\n");
        result.append("*Author:* ").append("[").append(source.getAuthorLogin()).append("](").append(source.getAuthorUrl()).append(")");

        System.out.println(result.toString());
        return result.toString();
    }

    private String getAction(PullRequestEventDto source) {

        return Optional.of(source)
                .filter(event -> CLOSED.equals(event.getAction()))
                .map(event -> event.isMerged() ? MERGED : DECLINED)
                .orElse(source.getAction())
                .toUpperCase();
    }

    private String getIcon(PullRequestEventDto source) {

        return Optional.of(source)
                .filter(event -> CLOSED.equals(event.getAction()))
                .map(event -> closedIndicatorsMap.get(event.isMerged()))
                .orElse(TWISTED_ARROWS);
    }
}
