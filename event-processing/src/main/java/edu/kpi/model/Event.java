package edu.kpi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {
    @Id private Long id;
    private String issueId;
    private String repo;
    private LocalDateTime eventTime;
    private String action;
    private String comment;
}
