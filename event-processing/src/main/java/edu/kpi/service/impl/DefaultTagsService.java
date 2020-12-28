package edu.kpi.service.impl;

import edu.kpi.dto.TagsData;
import edu.kpi.service.TagsService;
import edu.kpi.utils.EventSink;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class DefaultTagsService implements TagsService {
    private static final int MAX_TAGS_HISTORY = 1;
    private final EventSink<TagsData> tagsDataEventSink;
    private final Flux<TagsData> tagsData;

    public DefaultTagsService(EventSink<TagsData> tagsDataEventSink) {
        this.tagsDataEventSink = tagsDataEventSink;
        this.tagsData = Flux.create(tagsDataEventSink).cache(MAX_TAGS_HISTORY);
    }


    @Override
    public void publishTags(TagsData tags) {

        tagsDataEventSink.publish(tags);
    }

    @Override
    public Flux<TagsData> getTagsData() {

        return tagsData;
    }

}
