package edu.kpi.convertor.impl;

import edu.kpi.convertor.Convertor;
import edu.kpi.dto.IssueCommentEventDto;
import edu.kpi.model.data.IssueCommentEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ReversedIssueCommentEventConvertor implements Convertor<IssueCommentEventDto, IssueCommentEvent> {

    @Override
    public IssueCommentEvent convert(IssueCommentEventDto fromObject) {
        return IssueCommentEvent.builder()
                .body(fromObject.getBody())
                .owner(fromObject.getOwner())
                .action(fromObject.getAction())
                .login(fromObject.getLogin())
                .repo(fromObject.getRepo())
                .senderType(fromObject.getSenderType())
                .issueNumber(fromObject.getIssueNumber())
                //TODO Make github integration responsible for set date time
                .createdAt(LocalDateTime.now())
                .build();
    }
}
