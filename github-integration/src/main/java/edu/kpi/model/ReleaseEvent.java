package edu.kpi.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class ReleaseEvent {

    private String installationId;
    private String action;
    private String owner;
    private String repo;
    private String url;
    private String tag;
    private String branch;
    private String name;
    private String body;
}
