package edu.kpi.service.impl;

import edu.kpi.converter.Converter;
import edu.kpi.dto.*;
import edu.kpi.integration.telegram.GithubProjectNotificationBot;
import edu.kpi.model.data.Statistic;
import edu.kpi.service.NotificationService;
import edu.kpi.service.StatisticService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;

@Service
@Slf4j
public class TelegramNotificationService implements NotificationService {
    private final GithubProjectNotificationBot githubProjectNotificationBot;
    private final String telegramChatId;
    private final Converter<IssueEventDto, String> issueEventConverter;
    private final Converter<IssueCommentEventDto, String> issueCommentEventConverter;
    private final Converter<PullRequestEventDto, String> pullRequestEventConverter;
    private final Converter<ReleaseEventDto, String> releaseEventConverter;
    private final Converter<Statistic, String> statisticConverter;
    private final StatisticService statisticService;

    public TelegramNotificationService(GithubProjectNotificationBot githubProjectNotificationBot,
                                       @Value("${telegram.bot.chatId}") String telegramChatId,
                                       Converter<IssueEventDto, String> issueEventConverter,
                                       Converter<IssueCommentEventDto, String> issueCommentEventConverter,
                                       Converter<PullRequestEventDto, String> pullRequestEventConverter,
                                       Converter<ReleaseEventDto, String> releaseEventConverter,
                                       Converter<Statistic, String> statisticConverter,
                                       StatisticService statisticService) {
        this.githubProjectNotificationBot = githubProjectNotificationBot;
        this.telegramChatId = telegramChatId;
        this.issueEventConverter = issueEventConverter;
        this.issueCommentEventConverter = issueCommentEventConverter;
        this.pullRequestEventConverter = pullRequestEventConverter;
        this.releaseEventConverter = releaseEventConverter;
        this.statisticConverter = statisticConverter;
        this.statisticService = statisticService;
    }

    @Override
    public void pullRequestNotify(PullRequestEventDto pullRequestEvent) {
        executeSendMessage(pullRequestEventConverter.convert(pullRequestEvent));
    }

    @Override
    public void issueCommentNotify(IssueCommentEventDto issueCommentEvent) {
        executeSendMessage(issueCommentEventConverter.convert(issueCommentEvent));
    }

    @Override
    public void issueNotify(IssueEventDto issueEvent) {
        executeSendMessage(issueEventConverter.convert(issueEvent));
    }

    @Override
    public void releaseNotify(ReleaseEventDto releaseEvent) {
        executeSendMessage(releaseEventConverter.convert(releaseEvent));
    }

    @Override
    public void tweetNotify(TweetData tweet) {
        throw new UnsupportedOperationException();
    }

    private void executeSendMessage(String text) {
        try {
            githubProjectNotificationBot.execute(new SendMessage(telegramChatId, text, "markdown", false, false, null, null, null, false));
        } catch (TelegramApiException e) {
            log.warn(e.getMessage(), e);
        }
    }

    @PostConstruct
    private void statisticNotify() {
        statisticService.getStatisticFlux()
                .doOnNext(statistic -> executeSendMessage(statisticConverter.convert(statistic)))
                .subscribe();
    }

}
