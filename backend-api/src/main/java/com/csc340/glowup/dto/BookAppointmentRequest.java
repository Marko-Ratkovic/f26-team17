package com.csc340.glowup.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

public record BookAppointmentRequest(
    @NotNull Long serviceId,
    @NotNull LocalDateTime startDateTime){
}
