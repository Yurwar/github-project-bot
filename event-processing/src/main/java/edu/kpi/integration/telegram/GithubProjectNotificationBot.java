package edu.kpi.integration.telegram;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static edu.kpi.utils.Constants.Telegram.UNRECOGNIZED_COMMAND_MESSAGE;

@Component
@Slf4j
public class GithubProjectNotificationBot extends TelegramLongPollingCommandBot {
    @Value("${telegram.bot.name}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    public GithubProjectNotificationBot(BotCommand startCommand,
                                        BotCommand tagsCommand,
                                        BotCommand chatIdCommand) {
        registerAll(startCommand, tagsCommand, chatIdCommand);
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        SendMessage sendMessage = new SendMessage(update.getMessage().getChatId().toString(),
                UNRECOGNIZED_COMMAND_MESSAGE);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Can not send message", e);
        }
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }
}
