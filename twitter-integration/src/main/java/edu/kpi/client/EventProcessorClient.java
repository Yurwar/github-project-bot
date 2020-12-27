package edu.kpi.client;

import edu.kpi.dto.StatisticsData;
import edu.kpi.dto.TagsData;
import edu.kpi.entities.TweetsEvent;
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

    public Flux<TweetsEvent> streamTweets(final Flux<TweetsEvent> dataFlux) {

        return requester.route("processorController.fetchTweets")
                .data(dataFlux)
                .retrieveFlux(TweetsEvent.class);
    }

    public Flux<StatisticsData> streamStatistics(final Flux<StatisticsData> dataFlux) {

        return requester.route("processorController.fetchStatistics")
                .data(dataFlux)
                .retrieveFlux(StatisticsData.class);
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
