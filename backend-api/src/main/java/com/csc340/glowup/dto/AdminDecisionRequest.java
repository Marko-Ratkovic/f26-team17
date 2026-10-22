package com.csc340.glowup.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminDecisionRequest(
    @NotBlank @Size(max = 1000) String reason) {
}
