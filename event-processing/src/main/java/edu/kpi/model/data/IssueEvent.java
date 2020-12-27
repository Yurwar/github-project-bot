package edu.kpi.model.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("issue_event")
public class IssueEvent {
    @Id
    private Long id;
    private String issueNumber;
    private String title;
    private String body;
    private String repo;
    private LocalDateTime eventTime;
    private String action;
}
