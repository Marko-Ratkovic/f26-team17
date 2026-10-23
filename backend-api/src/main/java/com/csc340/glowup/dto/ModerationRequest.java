package com.csc340.glowup.dto;

import com.csc340.glowup.enums.AdminTargetType;
import com.csc340.glowup.enums.ModerationDecision;
import jakarta.validation.constraints.*;

// target is REVIEW (the default) or REPLY
public record ModerationRequest(
    AdminTargetType target,
    @NotNull ModerationDecision decision,
    @NotBlank @Size(max = 1000) String reason) {
}
