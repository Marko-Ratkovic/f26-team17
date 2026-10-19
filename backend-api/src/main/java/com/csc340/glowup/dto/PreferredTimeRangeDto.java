package com.csc340.glowup.dto;

import com.csc340.glowup.entity.PreferredTimeRange;
import com.csc340.glowup.enums.Weekday;

import java.time.LocalTime;

public record PreferredTimeRangeDto(
        Weekday dayOfWeek,
        LocalTime startTime,
        LocalTime endTime) {
    
    public static PreferredTimeRangeDto from(PreferredTimeRange preferredTimeRange) {
        return new PreferredTimeRangeDto(
                preferredTimeRange.getDayOfWeek(),
                preferredTimeRange.getStartTime(),
                preferredTimeRange.getEndTime());
    }
}
