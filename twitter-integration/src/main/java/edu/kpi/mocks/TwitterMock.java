package edu.kpi.mocks;

import org.springframework.stereotype.Service;
import twitter4j.Status;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class TwitterMock {

    public static final int STATUSES_COUNT = 11;
    public List<Status> mockStatuses;

    public TwitterMock() {

        this.mockStatuses = IntStream.range(0, STATUSES_COUNT)
                .mapToObj(x -> new StatusMock())
                .collect(Collectors.toList());
    }

    public List<Status> getMockStatuses(String keyword) {

        return mockStatuses.stream()
                .filter(status -> status.getText().contains(keyword))
                .collect(Collectors.toList());
    }

}
