package com.csc340.glowup.dto;

import com.csc340.glowup.enums.ServiceDecision;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AdminServiceDecisionRequest(
    @NotNull ServiceDecision decision,
    @Size(max = 1000) String note) {
}
