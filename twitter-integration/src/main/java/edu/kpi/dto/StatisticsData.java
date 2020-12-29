package edu.kpi.dto;

import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class StatisticsData {

    Date dateOfMetrics;
    Map<String, Long> sentimentToCountMap;

    public StatisticsData(Map<String, Long> sentimentToCountMap) {

        this.sentimentToCountMap = sentimentToCountMap;
        this.dateOfMetrics = new Date(System.currentTimeMillis());
    }
}
