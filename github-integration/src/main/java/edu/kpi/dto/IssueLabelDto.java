package edu.kpi.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@Builder
@ToString
public class IssueLabelDto {

    private String installationId;
    private String owner;
    private String repo;
    private String issueNumber;
    private List<String> labels;
}
