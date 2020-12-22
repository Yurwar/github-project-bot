package edu.kpi.service.integration;

import edu.kpi.dto.IssueLabelDto;

public interface IssueLabelIntegrationService {

    void addLabelsForIssue(final IssueLabelDto data);
}
