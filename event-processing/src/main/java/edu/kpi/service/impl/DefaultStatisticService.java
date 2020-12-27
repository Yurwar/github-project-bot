package edu.kpi.service.impl;

import edu.kpi.model.Event;
import edu.kpi.repository.EventRepository;
import edu.kpi.service.StatisticService;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static edu.kpi.utils.Constants.OPENED;

public class DefaultStatisticService implements StatisticService {

    private final EventRepository eventRepository;

    public DefaultStatisticService(final EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public final Mono<Long> getNumberOfIssuesByAction(final String action, final long repoId) {
        return eventRepository.findAllByActionAndRepoId(action, repoId)
                .filter(this::isEventHappenThisWeek)
                .count();
    }

    @Override
    public final Mono<Long> getAverageTimeByAction(final String action, final long repoId) {
        return eventRepository.findAllByActionAndRepoId(OPENED, repoId)
                .map(openEvent -> Tuples.of(openEvent, eventRepository.findByActionAndIssueIdAndRepoId(action, openEvent.getIssueId(), repoId)))
                .flatMap(tuple -> tuple.getT2()
                        .map(closedEvent -> Duration.between(closedEvent.getEventTime(), tuple.getT1().getEventTime()).toMinutes()))
                .collect(Collectors.toList())
                .map(list -> list.stream().reduce(0L, Long::sum) / list.size());
    }

    private boolean isEventHappenThisWeek(final Event event) {
        return event.getEventTime().getDayOfYear() - LocalDateTime.now().getDayOfYear() < 7 &&
                event.getEventTime().getYear() == LocalDateTime.now().getYear();
    }
}
