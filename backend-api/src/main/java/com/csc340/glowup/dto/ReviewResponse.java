package com.csc340.glowup.dto;

import com.csc340.glowup.entity.Appointment;
import com.csc340.glowup.entity.Review;
import com.csc340.glowup.enums.ContentStatus;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public record ReviewResponse(
    Long id,
    Long appointmentId,
    LocalDateTime appointmentDate,
    Long providerId,
    String providerName,
    String serviceName,
    String customerName,
    Integer rating,
    String comment,
    ContentStatus status,
    boolean editable,
    OffsetDateTime createdAt,
    OffsetDateTime editedAt,
    String reply,
    OffsetDateTime repliedAt) {

    // a hidden reply is left out
    public static ReviewResponse from(Review review) {
        Appointment appointment = review.getAppointment();
        boolean replyVisible = review.getReplyText() != null && review.getReplyStatus() == ContentStatus.VISIBLE;
        return new ReviewResponse(
                review.getId(),
                appointment.getId(),
                appointment.getStartsAt(),
                appointment.getProvider().getId(),
                appointment.getProvider().getName(),
                appointment.getService().getName(),
                appointment.getCustomer().getName(),
                review.getRating(),
                review.getComment(),
                review.getStatus(),
                review.getStatus() == ContentStatus.VISIBLE && review.getReplyText() == null,
                review.getCreatedAt(),
                review.getEditedAt(),
                replyVisible ? review.getReplyText() : null,
                replyVisible ? review.getRepliedAt() : null);
    }
}
