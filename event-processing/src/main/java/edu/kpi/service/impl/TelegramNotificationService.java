package edu.kpi.service.impl;

import edu.kpi.dto.*;
import edu.kpi.integration.telegram.GithubProjectNotificationBot;
import edu.kpi.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@Slf4j
public class TelegramNotificationService implements NotificationService {
    private final GithubProjectNotificationBot githubProjectNotificationBot;
    private final String telegramChatId;

    public TelegramNotificationService(GithubProjectNotificationBot githubProjectNotificationBot,
                                       @Value("${telegram.bot.chatId}") String telegramChatId) {
        this.githubProjectNotificationBot = githubProjectNotificationBot;
        this.telegramChatId = telegramChatId;
    }

    @Override
    public void pullRequestNotify(PullRequestEventDto pullRequestEvent) {
        executeSendMessage(pullRequestEvent.toString());
    }

    @Override
    public void issueCommentNotify(IssueCommentEventDto issueCommentEvent) {
        executeSendMessage(issueCommentEvent.toString());
    }

    @Override
    public void issueNotify(IssueEventDto issueEvent) {
        executeSendMessage(issueEvent.toString());
    }

    @Override
    public void releaseNotify(ReleaseEventDto releaseEvent) {
        executeSendMessage(releaseEvent.toString());
    }

    @Override
    public void tweetNotify(TweetData tweet) {
        throw new UnsupportedOperationException();
    }

    private void executeSendMessage(String text) {
        try {
            githubProjectNotificationBot.execute(new SendMessage(telegramChatId, text));
        } catch (TelegramApiException e) {
            log.warn(e.getMessage(), e);
        }
    }
}
