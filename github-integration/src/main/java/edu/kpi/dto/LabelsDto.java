package edu.kpi.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LabelsDto {

    private List<String> labels;
}
