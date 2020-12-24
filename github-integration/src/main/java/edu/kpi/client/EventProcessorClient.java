package edu.kpi.client;

import edu.kpi.dto.EventDto;
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


    public EventProcessorClient(final RSocketRequester requester) {

        this.requester = requester;
    }

    public Flux<EventDto> connectToProcessor(final Flux<EventDto> eventFlux) {

        return requester.route("githubIntegrationController.connect")
                .data(eventFlux)
                .retrieveFlux(EventDto.class);
    }

    public Flux<EventDto> publishEvent(final EventDto event) {

        return requester.route("githubIntegrationController.issueCreated")
                .data(event)
                .retrieveFlux(EventDto.class);
    }


    public Flux<Message> getMessage(final String id) {

        return requester.route("githubIntegrationController.getTestMessage")
                .data(new MessageRequest(id))
                .retrieveFlux(Message.class);
    }

}
