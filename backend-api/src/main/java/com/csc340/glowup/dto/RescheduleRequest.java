package com.csc340.glowup.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record RescheduleRequest(@NotNull LocalDateTime startDateTime) {
}
