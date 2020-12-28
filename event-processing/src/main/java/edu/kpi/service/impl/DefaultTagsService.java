package edu.kpi.service.impl;

import edu.kpi.dto.TagsData;
import edu.kpi.service.TagsService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
public class DefaultTagsService implements TagsService {
    private static final int MAX_TAGS_HISTORY = 1;
    private final Sinks.Many<TagsData> sink;

    public DefaultTagsService() {
        this.sink = Sinks.many().multicast().onBackpressureBuffer(MAX_TAGS_HISTORY);
    }


    @Override
    public void publishTags(TagsData tags) {

        sink.tryEmitNext(tags);
    }

    @Override
    public Flux<TagsData> getTagsData() {

        return sink.asFlux();
    }

}
