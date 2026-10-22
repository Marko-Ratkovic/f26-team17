package com.csc340.glowup.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/** The new time the customer asks for, in the provider's local time, e.g. "2026-10-19T11:00". */
public record RescheduleRequest(@NotNull LocalDateTime startDateTime) {
}
