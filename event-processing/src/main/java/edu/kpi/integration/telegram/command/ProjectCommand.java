package edu.kpi.integration.telegram.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;

import static edu.kpi.utils.Constants.Telegram.PROJECT_COMMAND_DESCRIPTION;
import static edu.kpi.utils.Constants.Telegram.PROJECT_COMMAND_NAME;

@Slf4j
@Component
public class ProjectCommand extends BotCommand {

    public ProjectCommand() {
        super(PROJECT_COMMAND_NAME, PROJECT_COMMAND_DESCRIPTION);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        try {
            SendMessage sendMessage =
                    new SendMessage(chat.getId().toString(),
                            Arrays.toString(strings) + " your project");

            absSender.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Can not send message", e);
        }
    }
}
