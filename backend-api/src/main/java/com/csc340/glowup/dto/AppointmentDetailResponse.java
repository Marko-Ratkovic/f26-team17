package com.csc340.glowup.dto;

import java.time.LocalDateTime;

import com.csc340.glowup.entity.Appointment;
import com.csc340.glowup.entity.CustomerSafetyNotes;
import com.csc340.glowup.enums.AppointmentStatus;
import java.math.BigDecimal;

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
        CustomerSafetyNotes customerSafetyNotes) {
        
    public static AppointmentDetailResponse from(Appointment appointment){
        return new AppointmentDetailResponse(appointment.getId(), appointment.getCustomer().getName(), appointment.getService().getName(), 
        appointment.getService().getDurationMin(), appointment.getStatus(), appointment.getStartsAt(), appointment.getService().getPrice(), appointment.getCustomer().getUserAccount().getEmail(), 
        appointment.getCustomer().getUserAccount().getPhoneNumber(), appointment.getCustomer().getSafetyNotes());
    }
    
}
