package edu.kpi.entities;

public enum Sentiment {
    VERY_NEGATIVE(0),
    NEGATIVE(1),
    NEUTRAL(2),
    POSITIVE(3),
    VERY_POSITIVE(4);

    private final int value;
    Sentiment(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
