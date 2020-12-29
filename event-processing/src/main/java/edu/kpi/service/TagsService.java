package edu.kpi.service;

import edu.kpi.dto.TagsData;
import reactor.core.publisher.Flux;

public interface TagsService {
    void publishTags(TagsData tags);

    Flux<TagsData> getTagsData();
}
