package edu.kpi.integration.telegram.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static edu.kpi.utils.Constants.Telegram.*;

@Slf4j
@Component
public class StartCommand extends BotCommand {
    public StartCommand() {
        super(START_COMMAND_NAME, START_COMMAND_DESCRIPTION);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        SendMessage sendMessage = new SendMessage(chat.getId().toString(), START_COMMAND_MESSAGE);

        try {
            absSender.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Can not send message from start command", e);
        }
    }
}
