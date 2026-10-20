package com.csc340.glowup.dto;

import com.csc340.glowup.enums.TimeBlock;
import com.csc340.glowup.enums.Weekday;

import jakarta.validation.constraints.NotNull;

public record ProviderScheduleRequest(
    @NotNull Weekday weekday,
    @NotNull TimeBlock timeBlock,
    @NotNull Boolean isAvailable){
}
