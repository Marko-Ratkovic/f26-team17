package com.csc340.glowup.dto;

import com.csc340.glowup.entity.ReviewReport;
import com.csc340.glowup.enums.AccountRole;
import com.csc340.glowup.enums.AdminTargetType;
import com.csc340.glowup.enums.ContentStatus;
import com.csc340.glowup.enums.ReportReason;

import java.time.OffsetDateTime;

public record ReviewReportResponse(
    Long id,
    Long reviewId,
    AdminTargetType target,
    ReportReason reason,
    String details,
    Long reporterId,
    AccountRole reporterRole,
    OffsetDateTime createdAt,
    OffsetDateTime resolvedAt,
    ContentStatus outcome) {

    public static ReviewReportResponse from(ReviewReport report) {
        return new ReviewReportResponse(
                report.getId(),
                report.getReview().getId(),
                report.getTarget(),
                report.getReason(),
                report.getDetails(),
                report.getReporter().getId(),
                report.getReporter().getAccountRole(),
                report.getCreatedAt(),
                report.getResolvedAt(),
                report.getOutcome());
    }
}
