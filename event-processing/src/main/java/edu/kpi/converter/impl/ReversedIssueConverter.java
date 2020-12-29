package edu.kpi.converter.impl;

import edu.kpi.converter.Converter;
import edu.kpi.dto.IssueEventDto;
import edu.kpi.model.index.Issue;
import org.springframework.stereotype.Component;

@Component
public class ReversedIssueConverter implements Converter<IssueEventDto, Issue> {
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
