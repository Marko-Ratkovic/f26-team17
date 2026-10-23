package com.csc340.glowup.dto;

import com.csc340.glowup.enums.AdminTargetType;
import com.csc340.glowup.enums.ReportReason;
import jakarta.validation.constraints.*;

// target is REVIEW (the default) or REPLY
public record ReportRequest(
    @NotNull Long reviewId,
    AdminTargetType target,
    @NotNull ReportReason reason,
    @Size(max = 500) String details) {
}
