package edu.kpi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(indexName = "github-issues")
public class Issue {
    @Id
    private String id;
    @Field
    private String number;
    @Field
    private String repo;
    @Field
    private String title;
    @Field
    private String body;
}
