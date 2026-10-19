package com.csc340.glowup.dto;

import com.csc340.glowup.entity.PreferredTimeRange;
import com.csc340.glowup.enums.Weekday;
import jakarta.validation.constraints.*;

import java.time.LocalTime;

public record PreferredTimeRangeDto(
        @NotNull Weekday dayOfWeek,
        @NotNull LocalTime startTime,
        @NotNull LocalTime endTime) {
    
    public static PreferredTimeRangeDto from(PreferredTimeRange preferredTimeRange) {
        return new PreferredTimeRangeDto(
                preferredTimeRange.getDayOfWeek(),
                preferredTimeRange.getStartTime(),
                preferredTimeRange.getEndTime());
    }
}
