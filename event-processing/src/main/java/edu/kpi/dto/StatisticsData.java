package edu.kpi.dto;

import edu.kpi.model.Sentiment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsData {

    Date dateOfMetrics;
    Map<Sentiment, Long> sentimentToCountMap;
}
