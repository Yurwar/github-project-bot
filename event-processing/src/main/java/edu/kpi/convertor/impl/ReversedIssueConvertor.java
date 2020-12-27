package edu.kpi.convertor.impl;

import edu.kpi.convertor.Convertor;
import edu.kpi.dto.IssueEventDto;
import edu.kpi.model.index.Issue;
import org.springframework.stereotype.Component;

@Component
public class ReversedIssueConvertor implements Convertor<IssueEventDto, Issue> {
    @Override
    public Issue convert(IssueEventDto fromObject) {
        return Issue.builder()
                .id(fromObject.getId())
                .body(fromObject.getBody())
                .title(fromObject.getTitle())
                .repo(fromObject.getRepo())
                .number(fromObject.getIssueNumber())
                .build();
    }
}
