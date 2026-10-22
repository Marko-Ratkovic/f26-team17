package com.csc340.glowup.enums;

import java.time.LocalTime;

public enum TimeBlock {
    MORNING(LocalTime.of(9, 0), LocalTime.of(12, 0)),
    AFTERNOON(LocalTime.of(12, 0), LocalTime.of(17, 0)),
    EVENING(LocalTime.of(17, 0), LocalTime.of(21, 0));

    private final LocalTime start;
    private final LocalTime end;

    TimeBlock(LocalTime start, LocalTime end) {
        this.start = start;
        this.end = end;
    }

    public LocalTime start() {
        return start;
    }

    public LocalTime end() {
        return end;
    }
}
