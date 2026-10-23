package com.csc340.glowup.dto;

import com.csc340.glowup.entity.AdminAction;
import com.csc340.glowup.entity.Appointment;
import com.csc340.glowup.entity.Review;
import com.csc340.glowup.entity.ReviewReport;
import com.csc340.glowup.enums.AdminTargetType;
import com.csc340.glowup.enums.ContentStatus;

import java.time.OffsetDateTime;
import java.util.List;

// one page for the review and its reply, with every report and admin decision
public record AdminReviewResponse(
    Long id,
    Long appointmentId,
    Long customerId,
    String customerName,
    Long providerId,
    String providerName,
    String serviceName,
    Integer rating,
    String comment,
    ContentStatus status,
    OffsetDateTime createdAt,
    OffsetDateTime editedAt,
    String reply,
    ContentStatus replyStatus,
    OffsetDateTime repliedAt,
    long openReviewReports,
    long openReplyReports,
    List<ReviewReportResponse> reports,
    List<AdminActionResponse> history) {

    public static AdminReviewResponse from(Review review, List<ReviewReport> reports, List<AdminAction> history) {
        Appointment appointment = review.getAppointment();
        return new AdminReviewResponse(
                review.getId(),
                appointment.getId(),
                appointment.getCustomer().getId(),
                appointment.getCustomer().getName(),
                appointment.getProvider().getId(),
                appointment.getProvider().getName(),
                appointment.getService().getName(),
                review.getRating(),
                review.getComment(),
                review.getStatus(),
                review.getCreatedAt(),
                review.getEditedAt(),
                review.getReplyText(),
                review.getReplyStatus(),
                review.getRepliedAt(),
                countOpen(reports, AdminTargetType.REVIEW),
                countOpen(reports, AdminTargetType.REPLY),
                reports.stream().map(ReviewReportResponse::from).toList(),
                history.stream().map(AdminActionResponse::from).toList());
    }

    private static long countOpen(List<ReviewReport> reports, AdminTargetType target) {
        return reports.stream().filter(report -> report.isOpen() && report.getTarget() == target).count();
    }
}
