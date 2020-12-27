package edu.kpi.dto.issue;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentDto {

    private String body;
}
