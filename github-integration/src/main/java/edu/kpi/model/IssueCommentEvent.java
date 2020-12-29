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
    private String senderType;
    private String login;
    private String senderUrl;
    private String commentUrl;
    private String issueNumber;
}
