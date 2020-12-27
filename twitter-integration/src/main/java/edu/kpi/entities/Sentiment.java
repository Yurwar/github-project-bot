package edu.kpi.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Sentiment {

    VERY_NEGATIVE(0),
    NEGATIVE(1),
    NEUTRAL(2),
    POSITIVE(3),
    VERY_POSITIVE(4);

    private final int value;
}
