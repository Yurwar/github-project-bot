package edu.kpi.integration.telegram.command;

import edu.kpi.dto.TagsData;
import edu.kpi.service.TagsService;
import edu.kpi.utils.Constants;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.List;
import java.util.stream.Collectors;

import static edu.kpi.utils.Constants.Telegram.TAGS_COMMAND_MESSAGE;

@Component
public class TagsCommand extends AbstractBotCommand {
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

        if (!tags.getTags().isEmpty()) {
            executeSendMessage(absSender,
                    new SendMessage(String.valueOf(chat.getId()), TAGS_COMMAND_MESSAGE + String.join(", ", tags.getTags())));
            tagsService.publishTags(tags);
        }
    }
}
