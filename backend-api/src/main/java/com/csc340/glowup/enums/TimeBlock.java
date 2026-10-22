package com.csc340.glowup.enums;

import java.time.LocalTime;

/**
 * Parts of the day for the customer's browse filter only ("open in the morning").
 * Providers set real opening hours per weekday (ProviderSchedule), not blocks.
 */
public enum TimeBlock {
    /** 9 AM – 12 PM */
    MORNING(LocalTime.of(9, 0), LocalTime.of(12, 0)),
    /** 12 PM – 5 PM */
    AFTERNOON(LocalTime.of(12, 0), LocalTime.of(17, 0)),
    /** 5 PM – 9 PM */
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
