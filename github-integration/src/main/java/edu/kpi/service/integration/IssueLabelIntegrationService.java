package edu.kpi.service.integration;

import edu.kpi.dto.EventDto;
import reactor.core.publisher.Mono;

public interface IssueLabelIntegrationService {

    Mono<Void> addLabelsForIssue(final EventDto data);
}
