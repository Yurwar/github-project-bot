package edu.kpi.integration.telegram.command;

import edu.kpi.utils.Constants;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Arrays;

@Component
public class TagsCommand extends BotCommand {
    public TagsCommand() {
        super(Constants.Telegram.TAGS_COMMAND_NAME, Constants.Telegram.TAGS_COMMAND_DESCRIPTION);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        System.out.println(Arrays.toString(strings));
    }
}
