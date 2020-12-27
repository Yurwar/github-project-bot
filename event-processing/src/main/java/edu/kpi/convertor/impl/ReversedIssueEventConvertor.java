package edu.kpi.convertor.impl;

import edu.kpi.convertor.Convertor;
import edu.kpi.dto.IssueEventDto;
import edu.kpi.model.data.IssueEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ReversedIssueEventConvertor implements Convertor<IssueEventDto, IssueEvent> {

    @Override
    public IssueEvent convert(IssueEventDto fromObject) {
        return IssueEvent.builder()
                .action(fromObject.getAction())
                //Find ability to get event time from dto
                .eventTime(LocalDateTime.now())
                .repo(fromObject.getRepo())
                .issueNumber(fromObject.getIssueNumber())
                .body(fromObject.getBody())
                .title(fromObject.getTitle())
                .build();
    }
}
