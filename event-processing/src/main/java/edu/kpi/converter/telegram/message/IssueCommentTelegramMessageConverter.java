package edu.kpi.converter.telegram.message;

import edu.kpi.converter.Converter;
import edu.kpi.dto.IssueCommentEventDto;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

import static edu.kpi.utils.Constants.GitHub.BOT_TYPE;
import static edu.kpi.utils.Constants.GitHub.USER_TYPE;
import static edu.kpi.utils.Constants.Telegram.Message.BOT_ICON;
import static edu.kpi.utils.Constants.Telegram.Message.CONVERSATION_BALLOON;

@Component
public class IssueCommentTelegramMessageConverter implements Converter<IssueCommentEventDto, String> {

    private static final Map<String, String> indicatorsMap =
            Map.ofEntries(
                    Map.entry(BOT_TYPE, BOT_ICON),
                    Map.entry(USER_TYPE, CONVERSATION_BALLOON)
            );

    @Override
    public String convert(IssueCommentEventDto source) {

        StringBuilder result = new StringBuilder();

        result.append(indicatorsMap.get(source.getSenderType()));
        result.append(" ").append(getCommenterName(source));
        result.append(" [commented](").append(source.getCommentUrl()).append(") on issue: \n");
        result.append("_\"").append(source.getBody()).append("\"_\n");

        return result.toString();
    }

    private String getCommenterName(IssueCommentEventDto source) {

        return Optional.of(source)
                .filter(event -> USER_TYPE.equals(event.getSenderType()))
                .map(event -> "[" + event.getLogin() + "](" + event.getSenderUrl() + ")")
                .orElse(BOT_TYPE);
    }
}
