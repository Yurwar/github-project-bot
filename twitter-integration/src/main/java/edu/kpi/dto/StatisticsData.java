package edu.kpi.dto;

import edu.kpi.entities.Sentiment;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

@Data
public class StatisticsData {

    Date dateOfMetrics;
    Map<Sentiment, Long> sentimentToCountMap;

    public StatisticsData(Map<Sentiment, Long> sentimentToCountMap) {

        this.sentimentToCountMap = sentimentToCountMap;
        this.dateOfMetrics = new Date(System.currentTimeMillis());
    }
}
