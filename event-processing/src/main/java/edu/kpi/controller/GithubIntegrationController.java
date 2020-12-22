package edu.kpi.controller;

import edu.kpi.model.Message;
import edu.kpi.model.MessageRequest;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Controller
@MessageMapping("githubIntegrationController")
public class GithubIntegrationController {


    @MessageMapping("getTestMessage")
    public Flux<Message> getTestMessage(MessageRequest request) {
        return Flux.range(0, request.getId())
                .map(id -> new Message("Test message: " + id));
    }

}
