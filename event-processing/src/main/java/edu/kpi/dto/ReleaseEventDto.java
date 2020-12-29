package edu.kpi.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class ReleaseEventDto {

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
