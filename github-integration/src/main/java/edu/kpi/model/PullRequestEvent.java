package edu.kpi.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class PullRequestEvent {

    private String installationId;
    private String action;
    private String owner;
    private String repo;
    private String number;
    private String sourceRepo;
    private String destinationRepo;
    private boolean merged;
}
