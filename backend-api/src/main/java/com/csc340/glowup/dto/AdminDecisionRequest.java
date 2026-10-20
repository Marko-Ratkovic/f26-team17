package com.csc340.glowup.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Body for approve / suspend / reinstate. Every admin decision needs a reason for the audit log. */
public record AdminDecisionRequest(
    @NotBlank @Size(max = 1000) String reason) {
}
