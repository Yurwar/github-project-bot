package edu.kpi.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EventProcessorClient {

    private final RSocketRequester requester;


    public EventProcessorClient(RSocketRequester requester) {

        this.requester = requester;
    }


}
