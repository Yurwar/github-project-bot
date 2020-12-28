package edu.kpi.integration.telegram.command;

import edu.kpi.dto.TagsData;
import edu.kpi.service.TagsService;
import edu.kpi.utils.Constants;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.List;

@Component
public class TagsCommand extends BotCommand {
    private final TagsService tagsService;

    public TagsCommand(TagsService tagsService) {
        super(Constants.Telegram.TAGS_COMMAND_NAME, Constants.Telegram.TAGS_COMMAND_DESCRIPTION);
        this.tagsService = tagsService;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        TagsData tags = TagsData.builder()
                .tags(List.of(strings))
                .build();

        tagsService.publishTags(tags);
    }
}
