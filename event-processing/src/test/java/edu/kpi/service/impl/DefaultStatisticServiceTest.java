package edu.kpi.service.impl;

import edu.kpi.model.data.IssueCommentEvent;
import edu.kpi.model.data.IssueEvent;
import edu.kpi.model.data.Statistic;
import edu.kpi.model.index.Issue;
import edu.kpi.repository.data.IssueCommentEventRepository;
import edu.kpi.repository.data.IssueEventRepository;
import edu.kpi.service.ElasticsearchService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultStatisticServiceTest {
    private static final long ID = 1;
    private static final String MOST_MENTIONED_TOPIC = "test2";
    private static final String TEST_REPO_NAME = "TEST_REPO_NAME";
    private static final String FIRST_ISSUE_NUMBER = "1";
    private static final LocalDateTime HALF_AN_HOUR_BEFORE_TIME = LocalDateTime.now().minusMinutes(30);
    private static final String OPENED = "opened";
    private static final String SECOND_ISSUE_NUMBER = "2";
    private static final LocalDateTime FIFTEEN_MINUTES_BEFORE_TIME = LocalDateTime.now().minusMinutes(15);
    private static final String CLOSED = "closed";
    private static final String ISSUE_COMMENTED = "issue_commented";
    private static final String REPO_OWNER = "TEST_REPO_OWNER";
    private static final String BODY = "body";
    private static final String TITLE = "title";
    private static final String USER_SENDER_TYPE = "user";
    private static final String USER_LOGIN = "User";
    private static final LocalDateTime TWENTY_MINUTES_BEFORE_NOW = LocalDateTime.now().minusMinutes(20);


    private static final IssueEvent OPEN_FIRST_ISSUE = new IssueEvent(ID, FIRST_ISSUE_NUMBER, TITLE, BODY, TEST_REPO_NAME, HALF_AN_HOUR_BEFORE_TIME, OPENED);
    private static final IssueEvent CLOSE_FIRST_ISSUE = new IssueEvent(ID, FIRST_ISSUE_NUMBER, TITLE, BODY, TEST_REPO_NAME, FIFTEEN_MINUTES_BEFORE_TIME, CLOSED);
    private static final IssueEvent OPEN_SECOND_ISSUE = new IssueEvent(ID, SECOND_ISSUE_NUMBER, TITLE, BODY, TEST_REPO_NAME, HALF_AN_HOUR_BEFORE_TIME, OPENED);
    private static final IssueEvent CLOSE_SECOND_ISSUE = new IssueEvent(ID, SECOND_ISSUE_NUMBER, TITLE, BODY, TEST_REPO_NAME, FIFTEEN_MINUTES_BEFORE_TIME, CLOSED);

    private static final IssueCommentEvent USER_COMMENT_FIRST_ISSUE_EVENT = new IssueCommentEvent(ID, ISSUE_COMMENTED, REPO_OWNER, TEST_REPO_NAME, BODY, USER_SENDER_TYPE, USER_LOGIN, FIRST_ISSUE_NUMBER, TWENTY_MINUTES_BEFORE_NOW);
    private static final IssueCommentEvent OWNER_COMMENT_FIRST_ISSUE_EVENT = new IssueCommentEvent(ID, ISSUE_COMMENTED, REPO_OWNER, TEST_REPO_NAME, BODY, USER_SENDER_TYPE, REPO_OWNER, FIRST_ISSUE_NUMBER, TWENTY_MINUTES_BEFORE_NOW);
    private static final IssueCommentEvent USER_COMMENT_SECOND_ISSUE_EVENT = new IssueCommentEvent(ID, SECOND_ISSUE_NUMBER, REPO_OWNER, TEST_REPO_NAME, BODY, USER_SENDER_TYPE, USER_LOGIN, SECOND_ISSUE_NUMBER, TWENTY_MINUTES_BEFORE_NOW);

    private static final Issue FIRST_INDEX_ISSUE = new Issue(String.valueOf(ID), FIRST_ISSUE_NUMBER, TEST_REPO_NAME, "test1 test2", "test2 test3");
    private static final Issue SECOND_INDEX_ISSUE = new Issue(String.valueOf(ID), SECOND_ISSUE_NUMBER, TEST_REPO_NAME, MOST_MENTIONED_TOPIC, "test3");

    @Spy
    @InjectMocks
    private DefaultStatisticService testedInstance;

    @Mock
    private IssueEventRepository issueEventRepository;

    @Mock
    private IssueCommentEventRepository issueCommentEventRepository;

    @Mock
    private ElasticsearchService elasticsearchService;

    @Before
    public void setUp() {
        //Mock Issue event repo
        when(issueEventRepository.findRepositories()).thenReturn(Flux.just(TEST_REPO_NAME));
        when(issueEventRepository.findAllByActionAndRepoId(OPENED, TEST_REPO_NAME)).thenReturn(Flux.just(OPEN_FIRST_ISSUE, OPEN_SECOND_ISSUE));
        when(issueEventRepository.findAllByActionAndRepoId(CLOSED, TEST_REPO_NAME)).thenReturn(Flux.just(CLOSE_FIRST_ISSUE, CLOSE_SECOND_ISSUE));
        when(issueEventRepository.findByActionAndIssueIdAndRepoId(CLOSED, FIRST_ISSUE_NUMBER, TEST_REPO_NAME)).thenReturn(Mono.just(CLOSE_FIRST_ISSUE));
        when(issueEventRepository.findByActionAndIssueIdAndRepoId(CLOSED, SECOND_ISSUE_NUMBER, TEST_REPO_NAME)).thenReturn(Mono.just(CLOSE_SECOND_ISSUE));

        //Mock issue comment event repository
        when(issueCommentEventRepository.findAllByRepo(TEST_REPO_NAME)).thenReturn(Flux.just(USER_COMMENT_FIRST_ISSUE_EVENT, OWNER_COMMENT_FIRST_ISSUE_EVENT, USER_COMMENT_SECOND_ISSUE_EVENT));
        when(issueCommentEventRepository.findAllByRepoAndIssueNumber(TEST_REPO_NAME, FIRST_ISSUE_NUMBER)).thenReturn(Flux.just(USER_COMMENT_FIRST_ISSUE_EVENT, OWNER_COMMENT_FIRST_ISSUE_EVENT));
        when(issueCommentEventRepository.findAllByRepoAndIssueNumber(TEST_REPO_NAME, SECOND_ISSUE_NUMBER)).thenReturn(Flux.just(USER_COMMENT_SECOND_ISSUE_EVENT));

        //Mock elasticsearch service
        when(elasticsearchService.findIssuesByRepository(TEST_REPO_NAME)).thenReturn(Flux.just(FIRST_INDEX_ISSUE, SECOND_INDEX_ISSUE));
    }

    @Test
    public void shouldFindMostMentionedTopic() {
        Mono<String> mostMentionedTopic = testedInstance.getMostMentionedTopic(TEST_REPO_NAME);
        StepVerifier.create(mostMentionedTopic)
                .expectNext(MOST_MENTIONED_TOPIC)
                .as("Most mentioned topic")
                .verifyComplete();
    }

    @Test
    public void shouldFindOpenedIssuesPerWeek() {
        IssueEvent openedMoreThanWeekAgo  =
                new IssueEvent(ID, "3", TITLE, BODY, TEST_REPO_NAME, LocalDateTime.now().minusWeeks(2), OPENED);

        when(issueEventRepository.findAllByActionAndRepoId(OPENED, TEST_REPO_NAME)).thenReturn(Flux.just(OPEN_FIRST_ISSUE, OPEN_SECOND_ISSUE, openedMoreThanWeekAgo));

        Mono<Long> openedIssuesPerWeek = testedInstance.getNumberOfIssuesByActionPerWeek(OPENED, TEST_REPO_NAME);

        StepVerifier.create(openedIssuesPerWeek)
                .expectNext(2L)
                .as("Opened issues per week")
                .verifyComplete();
    }

    @Test
    public void shouldFindClosedIssuesPerWeek() {
        IssueEvent closedMoreThanWeekAgo  =
                new IssueEvent(ID, "3", TITLE, BODY, TEST_REPO_NAME, LocalDateTime.now().minusWeeks(2), CLOSED);

        when(issueEventRepository.findAllByActionAndRepoId(CLOSED, TEST_REPO_NAME)).thenReturn(Flux.just(CLOSE_FIRST_ISSUE, CLOSE_SECOND_ISSUE, closedMoreThanWeekAgo));

        Mono<Long> closedIssuesPerWeek = testedInstance.getNumberOfIssuesByActionPerWeek(CLOSED, TEST_REPO_NAME);

        StepVerifier.create(closedIssuesPerWeek)
                .expectNext(2L)
                .as("Closed issues per week")
                .verifyComplete();
    }

    @Test
    public void shouldFindAnswerAverageTime() {
        Mono<Double> answerAverageTime = testedInstance.getAnswerAverageTime(TEST_REPO_NAME);

        StepVerifier.create(answerAverageTime)
                .expectNext(10d)
                .verifyComplete();
    }

    @Test
    public void shouldFindCloseAverageTime() {
        Mono<Double> closedAverageTime = testedInstance.getClosedAverageTime(TEST_REPO_NAME);

        StepVerifier.create(closedAverageTime)
                .expectNext(15d)
                .verifyComplete();
    }

    @Test
    public void shouldFindUnansweredIssues() {
        Mono<List<String>> unansweredIssues = testedInstance.getUnansweredIssues(TEST_REPO_NAME);

        StepVerifier.create(unansweredIssues)
                .expectNext(List.of(SECOND_ISSUE_NUMBER))
                .verifyComplete();
    }

    @Test
    public void shouldFindWaitingForResponseIssues() {
        Mono<List<String>> waitingForResponseIssues = testedInstance.getWaitingForResponseIssues(TEST_REPO_NAME);

        StepVerifier.create(waitingForResponseIssues)
                .expectNext(List.of(FIRST_ISSUE_NUMBER))
                .verifyComplete();
    }

    @Test
    public void shouldWaitToMondayToSendStatistic() {
        LocalDateTime now = LocalDateTime.now();
        Duration durationToMonday = Duration.between(now, now.with(TemporalAdjusters.next(DayOfWeek.MONDAY)));

        StepVerifier
                .withVirtualTime(() -> testedInstance.createStatistic().take(2))
                .expectTimeout(durationToMonday)
                .verify();
    }

    @Test
    public void shouldSendStatisticEveryWeek() {
        LocalDateTime now = LocalDateTime.now();
        Duration durationToMonday = Duration.between(now, now.with(TemporalAdjusters.next(DayOfWeek.MONDAY)));

        StepVerifier
                .withVirtualTime(() -> testedInstance.createStatistic().take(2))
                .thenAwait(durationToMonday)
                .thenAwait(Duration.ofDays(7))
                .expectNext(Statistic.builder()
                        .numberOfIssuesClosedPerWeek(2L)
                        .numberOfIssuesCreatedPerWeek(2L)
                        .mostMentionedTopic(MOST_MENTIONED_TOPIC)
                        .repo(TEST_REPO_NAME)
                        .unansweredIssues(List.of(SECOND_ISSUE_NUMBER))
                        .waitingForResponseIssues(List.of(FIRST_ISSUE_NUMBER))
                        .averageTimeBetweenCreateAndClose(15d)
                        .averageTimeBetweenCreateAndComment(10d)
                        .build())
                .verifyTimeout(Duration.ofDays(7));
    }
}