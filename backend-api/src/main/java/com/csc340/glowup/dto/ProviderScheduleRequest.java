package com.csc340.glowup.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

/**
 * Body of PUT /api/providers/{providerId}/schedules/{weekday}.
 * openTime and closeTime ("10:00", "19:30") are required when isAvailable is true.
 */
public record ProviderScheduleRequest(
    @NotNull Boolean isAvailable,
    LocalTime openTime,
    LocalTime closeTime){
}
