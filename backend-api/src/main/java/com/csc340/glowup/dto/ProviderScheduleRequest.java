package com.csc340.glowup.dto;

import java.time.LocalTime;
import jakarta.validation.constraints.NotNull;

public record ProviderScheduleRequest(
    @NotNull LocalTime starTime,
    @NotNull LocalTime endTime,
    @NotNull Boolean isAvailable){
}
