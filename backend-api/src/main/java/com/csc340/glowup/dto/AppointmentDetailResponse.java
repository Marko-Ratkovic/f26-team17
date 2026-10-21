package com.csc340.glowup.dto;

import java.time.LocalDateTime;

import com.csc340.glowup.entity.Appointment;
import com.csc340.glowup.enums.AppointmentStatus;
import java.math.BigDecimal;

/**
 * One appointment as its provider sees it. The customer's email, phone and safety notes are only
 * included while the appointment is booked (CONFIRMED or COMPLETED); otherwise they are null (US-1, US-7).
 */
public record AppointmentDetailResponse(
        Long appointmentId,
        String customerName,
        String serviceName,
        Integer durationMin,
        AppointmentStatus status,
        LocalDateTime startAt,
        BigDecimal price,
        String customerEmail,
        String customerPhoneNumber,
        SafetyNotesDto customerSafetyNotes) {
        
    public static AppointmentDetailResponse from(Appointment appointment){
        boolean booked = appointment.getStatus() == AppointmentStatus.CONFIRMED
                || appointment.getStatus() == AppointmentStatus.COMPLETED;
        return new AppointmentDetailResponse(appointment.getId(), appointment.getCustomer().getName(),
                appointment.getService().getName(), appointment.getService().getDurationMin(),
                appointment.getStatus(), appointment.getStartsAt(), appointment.getService().getPrice(),
                booked ? appointment.getCustomer().getUserAccount().getEmail() : null,
                booked ? appointment.getCustomer().getUserAccount().getPhoneNumber() : null,
                booked ? SafetyNotesDto.from(appointment.getCustomer().getSafetyNotes()) : null);
    }
}
