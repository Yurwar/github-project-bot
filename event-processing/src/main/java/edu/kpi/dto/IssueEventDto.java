package edu.kpi.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@Builder
@ToString
public class IssueEventDto {

    private String id;
    private String title;
    private String body;
    private String installationId;
    private String action;
    private String owner;
    private String repo;
    private String issueNumber;
    private List<String> labels;
    private boolean awaitingTriage;
    private List<String> similarIssues;
    private String url;
    private String label;
}
