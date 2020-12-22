package edu.kpi.service.integration;

import edu.kpi.dto.IssueLabelDto;
import reactor.core.publisher.Mono;

public interface IssueLabelIntegrationService {

    Mono<Void> addLabelsForIssue(final IssueLabelDto data);
}
