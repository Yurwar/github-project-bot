package edu.kpi.converter.telegram.message;

import edu.kpi.converter.Converter;
import edu.kpi.dto.ReleaseEventDto;
import org.springframework.stereotype.Component;

import static edu.kpi.utils.Constants.Telegram.Message.TAG_SYMBOL;

@Component
public class ReleaseTelegramMessageConverter implements Converter<ReleaseEventDto, String> {

    @Override
    public String convert(ReleaseEventDto source) {

        StringBuilder result = new StringBuilder();

        result.append(TAG_SYMBOL).append(" - *[RELEASE ").append(source.getAction().toUpperCase()).append("]*\n");
        result.append("*Tag:* ").append(source.getBranch()).append(" ").append("[").append(source.getTag()).append("](").append(source.getUrl()).append(")").append("\n");
        result.append("*Title:* ").append(source.getName()).append("\n");
        result.append("*Description:* ").append(source.getBody());

        return result.toString();
    }
}
