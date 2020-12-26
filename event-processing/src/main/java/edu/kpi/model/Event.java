package edu.kpi.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "event")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "issue_id", nullable = false)
    private Long issueId;
    @Column(name = "time", nullable = false)
    private LocalDateTime eventTime;
    @Column(name = "action", nullable = false)
    private String action;
    @Column(name = "comment", nullable = false)
    private String comment;
}
