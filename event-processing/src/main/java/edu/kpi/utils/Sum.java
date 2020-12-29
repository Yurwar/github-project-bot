package edu.kpi.utils;

import lombok.Data;

@Data
public final class Sum {
    private final long value;
    private final int counter;

    public static Sum empty() {
        return new Sum(0, 0);
    }

    public Sum add(long value) {
        return new Sum(this.value + value, this.counter + 1);
    }

    public double avg() {
        return (double) value / (double) counter;
    }

}
