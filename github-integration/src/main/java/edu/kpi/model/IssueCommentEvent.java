package edu.kpi.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class IssueCommentEvent {

    private String installationId;
    private String action;
    private String owner;
    private String repo;
    private String body;
}
