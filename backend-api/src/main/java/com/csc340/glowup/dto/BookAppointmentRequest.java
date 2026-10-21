package com.csc340.glowup.dto;

import java.time.LocalDateTime;

import com.csc340.glowup.enums.TimeBlock;

public record BookAppointmentRequest(
    Long serviceId,
    LocalDateTime startDateTime){
}
