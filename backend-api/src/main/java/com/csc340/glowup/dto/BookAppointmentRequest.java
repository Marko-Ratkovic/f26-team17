package com.csc340.glowup.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

/** startDateTime is the provider's local time, e.g. "2026-10-16T17:00". */
public record BookAppointmentRequest(
    @NotNull Long serviceId,
    @NotNull LocalDateTime startDateTime){
}
