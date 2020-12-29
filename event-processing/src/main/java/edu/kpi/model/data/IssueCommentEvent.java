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
@Table("issue_comment_event")
public class IssueCommentEvent {

    @Id
    private Long id;
    private String action;
    private String owner;
    private String repo;
    private String body;
    private String senderType;
    private String login;
    private String issueNumber;
    private LocalDateTime createdAt;
}
