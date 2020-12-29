package edu.kpi.service;

import edu.kpi.dto.*;

public interface NotificationService {
    void pullRequestNotify(PullRequestEventDto pullRequestEvent);

    void issueCommentNotify(IssueCommentEventDto issueCommentEvent);

    void issueNotify(IssueEventDto issueEvent);

    void releaseNotify(ReleaseEventDto releaseEvent);

    void tweetNotify(TweetData tweet);

    void tweetStatisticNotify(StatisticsData statisticsData);
}
