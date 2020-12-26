package edu.kpi.service.impl;

import edu.kpi.model.Event;
import edu.kpi.repository.EventRepository;
import edu.kpi.service.StatisticService;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static edu.kpi.utils.Constants.CLOSED;
import static edu.kpi.utils.Constants.OPENED;

public class DefaultStatisticService implements StatisticService {

    private final EventRepository eventRepository;

    public DefaultStatisticService(final EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public final Mono<Long> getNumberOfClosedIssues(long repoId) {
        return eventRepository.findAll()
                .filter(event -> event.getAction().equals(CLOSED))
                .filter(event -> isRepoEventHappenThisWeek(event, repoId))
                .count();
    }

    @Override
    public final Mono<Long> getNumberOfCreatedIssues(long repoId) {
        return eventRepository.findAll()
                .filter(event -> event.getAction().equals(OPENED))
                .filter(event -> isRepoEventHappenThisWeek(event, repoId))
                .count();
    }

    @Override
    public final Mono<Long> getAverageCloseTime(long repoId) {
        return Mono.never();
    }

    @Override
    public final Mono<Long> getAverageResponseTime(long repoId) {
        return Mono.never();
    }

    private boolean isRepoEventHappenThisWeek(final Event event, final long repoId) {
        return event.getEventTime().getDayOfYear() - LocalDateTime.now().getDayOfYear() < 7 &&
                event.getEventTime().getYear() == LocalDateTime.now().getYear() &&
                event.getRepoId() == repoId;
    }
}
