package edu.kpi.client;

import edu.kpi.model.Message;
import edu.kpi.model.MessageRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class EventProcessorClient {

    private final RSocketRequester requester;


    public EventProcessorClient(RSocketRequester requester) {

        this.requester = requester;
    }


    public Flux<Message> getMessage(String id) {

        return requester.route("githubIntegrationController.getTestMessage")
                .data(new MessageRequest(id))
                .retrieveFlux(Message.class);
    }

}
