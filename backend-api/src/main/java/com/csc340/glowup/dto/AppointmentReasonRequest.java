package com.csc340.glowup.dto;

import jakarta.validation.constraints.Size;

public record AppointmentReasonRequest(@Size(max = 500) String reason) {
}
