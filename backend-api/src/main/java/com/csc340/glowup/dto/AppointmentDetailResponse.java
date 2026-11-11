package com.csc340.glowup.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.csc340.glowup.entity.Appointment;
import com.csc340.glowup.enums.AppointmentStatus;
import java.math.BigDecimal;

public record AppointmentDetailResponse(
        Long appointmentId,
        String customerName,
        String serviceName,
        Integer durationMin,
        AppointmentStatus status,
        String startAt,
        BigDecimal price,
        String customerEmail,
        String customerPhoneNumber,
        SafetyNotesDto customerSafetyNotes) {
        
    public static AppointmentDetailResponse from(Appointment appointment){
        boolean booked = appointment.getStatus() == AppointmentStatus.CONFIRMED
                || appointment.getStatus() == AppointmentStatus.COMPLETED;
        return new AppointmentDetailResponse(appointment.getId(), appointment.getCustomer().getName(),
                appointment.getService().getName(), appointment.getBookedDurationMin(),
                appointment.getStatus(), formattedDate(appointment.getStartsAt()), appointment.getBookedPrice(),
                booked ? appointment.getCustomer().getUserAccount().getEmail() : null,
                booked ? appointment.getCustomer().getUserAccount().getPhoneNumber() : null,
                booked ? SafetyNotesDto.from(appointment.getCustomer().getSafetyNotes()) : null);
    }

    public static String formattedDate(LocalDateTime starTime){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, MMM dd, yyy · HH:mm a");
        return starTime.format(formatter);
    }

}
