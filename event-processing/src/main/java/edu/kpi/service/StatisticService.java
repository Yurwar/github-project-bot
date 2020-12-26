package edu.kpi.service;

public interface StatisticService {
    long getNumberOfCreatedIssues();
    long getNumberOfClosedIssues();
    long getAverageResponseTime();
    long getAverageCloseTime();
}
