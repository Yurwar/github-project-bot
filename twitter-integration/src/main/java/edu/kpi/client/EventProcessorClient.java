package edu.kpi.client;

import edu.kpi.dto.TagsData;
import edu.kpi.entities.TweetData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@Service
public class EventProcessorClient {

    private final RSocketRequester requester;

    public EventProcessorClient(RSocketRequester requester) {

        this.requester = requester;
    }

    public Flux<TweetData> streamTweets(final Flux<TweetData> dataFlux) {

        return requester.route("processorController.fetchTweets")
                .data(dataFlux)
                .retrieveFlux(TweetData.class);
    }

    public Flux<List<String>> receiveKeywords() {

        return requester.route("processorController.keywords")
                .retrieveFlux(TagsData.class)
                .map(TagsData::getTags);
    }

    public Flux<Integer> receiveCounts() {

        return requester.route("processorController.tweetsCount")
                .retrieveFlux(Integer.class);
    }
}
