package edu.kpi.service.impl;

import edu.kpi.model.data.IssueEvent;
import edu.kpi.repository.data.IssueEventRepository;
import edu.kpi.service.StatisticService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static edu.kpi.utils.Constants.CLOSED;
import static edu.kpi.utils.Constants.OPENED;

@Service
public class DefaultStatisticService implements StatisticService {

    private final IssueEventRepository issueEventRepository;

    public DefaultStatisticService(final IssueEventRepository issueEventRepository) {
        this.issueEventRepository = issueEventRepository;
    }

    @Override
    public final Mono<Long> getNumberOfIssuesByAction(final String action, final String repoId) {
        return issueEventRepository.findAllByActionAndRepoId(action, repoId)
                .log()
                .filter(this::isEventHappenThisWeek)
                .count();
    }

    @Override
    public final Mono<Long> getAverageTimeByAction(final String action, final String repoId) {
        return issueEventRepository.findAllByActionAndRepoId(OPENED, repoId)
                .map(openIssueEvent -> Tuples.of(openIssueEvent, issueEventRepository.findByActionAndIssueIdAndRepoId(action, openIssueEvent.getIssueNumber(), repoId)))
                .flatMap(tuple -> tuple.getT2()
                        .map(closedIssueEvent -> Duration.between(closedIssueEvent.getEventTime(), tuple.getT1().getEventTime()).toMinutes()))
                .collect(Collectors.toList())
                .map(list -> list.stream().reduce(0L, Long::sum) / list.size());
    }

    @Override
    public final Flux<IssueEvent> getUnclosedEvents() {
        return issueEventRepository.findAll()
                .filter(issueEvent -> !issueEvent.getAction().equals(CLOSED));
    }

    private boolean isEventHappenThisWeek(final IssueEvent issueEvent) {
        return issueEvent.getEventTime().getDayOfYear() - LocalDateTime.now().getDayOfYear() < 7 &&
                issueEvent.getEventTime().getYear() == LocalDateTime.now().getYear();
    }
}
