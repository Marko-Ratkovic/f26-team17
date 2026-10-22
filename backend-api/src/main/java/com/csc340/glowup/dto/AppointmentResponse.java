package com.csc340.glowup.dto;

import com.csc340.glowup.enums.AccountRole;
import com.csc340.glowup.enums.AppointmentStatus;
import com.csc340.glowup.entity.Appointment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public record AppointmentResponse(
        Long id,
        Long customerId,
        String customerName,
        Long serviceId,
        String serviceName,
        Long providerId,
        String providerName,
        LocalDateTime startAt,
        LocalDateTime endsAt,
        Integer durationMin,
        BigDecimal price,
        AppointmentStatus status,
        boolean completed,
        boolean expired,
        String statusReason,
        AccountRole cancelledBy,
        Long rescheduledFromId,
        OffsetDateTime requestedAt) {
    
    public static AppointmentResponse from(Appointment appointment){
        return new AppointmentResponse(appointment.getId(),appointment.getCustomer().getId(),appointment.getCustomer().getName(),
                appointment.getService().getId(),appointment.getService().getName(),appointment.getProvider().getId(),
                appointment.getProvider().getName(),appointment.getStartsAt(),appointment.getEndsAt(),
                appointment.getBookedDurationMin(),appointment.getBookedPrice(),appointment.getStatus(),
                appointment.isCompleted(),appointment.isExpired(),appointment.getStatusReason(),appointment.getCancelledBy(),
                appointment.getRescheduledFrom() == null ? null : appointment.getRescheduledFrom().getId(),
                appointment.getRequestedAt());
    }
}
