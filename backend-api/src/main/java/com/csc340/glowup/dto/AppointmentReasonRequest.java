package com.csc340.glowup.dto;

import jakarta.validation.constraints.Size;

/** Body for declining or cancelling. The reason is required for providers and optional for customers. */
public record AppointmentReasonRequest(@Size(max = 500) String reason) {
}
