package edu.kpi.converter.telegram.message;

import edu.kpi.converter.Converter;
import edu.kpi.dto.IssueEventDto;
import io.netty.util.internal.StringUtil;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static edu.kpi.utils.Constants.CLOSED;
import static edu.kpi.utils.Constants.LABELED;
import static edu.kpi.utils.Constants.OPENED;
import static edu.kpi.utils.Constants.Telegram.Message.BLUE_CIRCLE;
import static edu.kpi.utils.Constants.Telegram.Message.GREEN_CIRCLE;
import static edu.kpi.utils.Constants.Telegram.Message.YELLOW_CIRCLE;

@Component
public class IssueTelegramMessageConverter implements Converter<IssueEventDto, String> {

    private static final int BODY_MAX_LENGTH = 40;
    private static final String BODY_POSTFIX = "...\n";

    private static final Map<String, String> indicatorsMap =
            Map.ofEntries(
                    Map.entry(OPENED, BLUE_CIRCLE),
                    Map.entry(CLOSED, GREEN_CIRCLE),
                    Map.entry(LABELED, YELLOW_CIRCLE));

    private final Map<String, Function<IssueEventDto, String>> methodsMap =
            Map.ofEntries(
                    Map.entry(OPENED, this::convertOpenedOrClosed),
                    Map.entry(CLOSED, this::convertOpenedOrClosed),
                    Map.entry(LABELED, this::convertLabeled));

    @Override
    public String convert(IssueEventDto source) {

        return methodsMap.get(source.getAction()).apply(source);
    }

    private String convertOpenedOrClosed(IssueEventDto source) {

        StringBuilder result = new StringBuilder();

        result.append(indicatorsMap.get(source.getAction())).append(" - *[ISSUE ").append(source.getAction().toUpperCase()).append("]*\n");
        result.append("*Issue:* ").append("[").append(source.getTitle()).append("](").append(source.getUrl()).append(")").append("\n");

        Optional.ofNullable(source.getBody())
                .filter(body -> !StringUtil.isNullOrEmpty(body))
                .map(body -> "*Description:* " + getDescription(body))
                .ifPresent(result::append);

        result.append("*Repository:* ").append(source.getRepo()).append("\n");

        return result.toString();
    }

    private String getDescription(String body) {

        return Optional.of(body)
                .filter(description -> description.length() <= BODY_MAX_LENGTH)
                .map(description -> description + "\n")
                .orElse(body.substring(0, Math.min(body.length(), BODY_MAX_LENGTH)).trim() + BODY_POSTFIX);
    }

    private String convertLabeled(IssueEventDto source) {

        StringBuilder result = new StringBuilder();

        result.append(indicatorsMap.get(source.getAction())).append(" - *[ISSUE ").append(source.getAction().toUpperCase()).append("]*\n");
        result.append("*Issue:* ").append("[").append(source.getTitle()).append("](").append(source.getUrl()).append(")").append("\n");
        result.append("*Label:* ").append("`").append(source.getLabel()).append("`");

        return result.toString();
    }
}
