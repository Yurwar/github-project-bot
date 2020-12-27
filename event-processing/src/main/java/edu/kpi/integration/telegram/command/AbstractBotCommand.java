package edu.kpi.integration.telegram.command;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
public abstract class AbstractBotCommand extends BotCommand {
    public AbstractBotCommand(String commandIdentifier, String description) {
        super(commandIdentifier, description);
    }

    public void executeSendMessage(AbsSender sender, SendMessage sendMessage) {
        try {
            sender.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Error while sending message", e);
        }
    }
}
