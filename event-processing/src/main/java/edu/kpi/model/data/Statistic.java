package edu.kpi.model.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Statistic {
    private String repo;
    private Long averageTimeBetweenCreateAndClose;
    private Long averageTimeBetweenCreateAndComment;
    private Long numberOfIssuesCreatedPerWeek;
    private Long numberOfIssuesClosedPerWeek;
    private String mostMentionedTopic;
    private List<IssueEvent> unclosedIssues;
}
