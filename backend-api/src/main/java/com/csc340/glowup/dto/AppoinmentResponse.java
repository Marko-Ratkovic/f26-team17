package com.csc340.glowup.dto;

import com.csc340.glowup.enums.AppointmentStatus;
import com.csc340.glowup.entity.Appointment;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public record AppoinmentResponse(
        Long id,
        Long customerId,
        String customerName,
        Long serviceId,
        String serviceName,
        Long providerId,
        String providerName,
        LocalDateTime startAt,
        LocalDateTime endsAt,
        AppointmentStatus status,
        boolean completed,
        OffsetDateTime requestedAt) {
    
    public static AppoinmentResponse from(Appointment appointment){
        return new AppoinmentResponse(appointment.getId(),appointment.getCustomer().getId(),appointment.getCustomer().getName(),
                appointment.getService().getId(),appointment.getService().getName(),appointment.getProvider().getId(),
                appointment.getProvider().getName(),appointment.getStartsAt(),appointment.getEndsAt(),appointment.getStatus(),
                appointment.isCompleted(),appointment.getRequestedAt());
    }
}
