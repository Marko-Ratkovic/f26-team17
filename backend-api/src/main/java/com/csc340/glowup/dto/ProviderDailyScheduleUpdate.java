package com.csc340.glowup.dto;

import java.time.LocalTime;

import com.csc340.glowup.enums.Weekday;

import jakarta.validation.constraints.NotNull;

public record ProviderDailyScheduleUpdate(
    @NotNull Weekday weekday,
    @NotNull Boolean isAvailable,
    LocalTime openTime,
    LocalTime closeTime) {
}
