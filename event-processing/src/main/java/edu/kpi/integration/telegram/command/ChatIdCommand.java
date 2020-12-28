package edu.kpi.integration.telegram.command;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import static edu.kpi.utils.Constants.Telegram.*;

@Component
public class ChatIdCommand extends AbstractBotCommand {

    public ChatIdCommand() {
        super(CHAT_ID_COMMAND_NAME, CHAT_ID_COMMAND_DESCRIPTION);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {

        SendMessage sendChatId = SendMessage.builder()
                .chatId(String.valueOf(chat.getId()))
                .text(CHAT_ID_COMMAND_MESSAGE + chat.getId())
                .build();

        executeSendMessage(absSender, sendChatId);
    }
}
