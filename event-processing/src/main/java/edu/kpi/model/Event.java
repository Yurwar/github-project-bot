package edu.kpi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {
    private Long id;
    private Long issueId;
    private Long repoId;
    private LocalDateTime eventTime;
    private String action;
    private String comment;
}
